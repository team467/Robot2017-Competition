//*----------------------------------------------------------------------------*/
// Copyright (c) FIRST 2016. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team467.robot.imu;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.InterruptableSensorBase;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Timer;

/**
 * This class is for the ADIS16448 IMU that connects to the RoboRIO MXP port.
 */
public class LSM9DS1_IMU extends GyroBase implements Gyro, PIDSource, LiveWindowSendable, IMU {

	private static final double MEASURES_PER_DEGREE = 1.0;

	private static final double kTimeout = 0.1;
	private static final double kCalibrationSampleTime = 5.0;

	private NetworkTable table;

	public enum AHRSAlgorithm {
		kComplementary, kMadgwick
	}

	public enum Axis {
		kX, kY, kZ
	}

	// AHRS algorithm
	private AHRSAlgorithm m_algorithm;

	// AHRS yaw axis
	private Axis m_yaw_axis;

	// gyro offset
	private double m_gyro_offset_x = 0.0;
	private double m_gyro_offset_y = 0.0;
	private double m_gyro_offset_z = 0.0;

	// last read values (post-scaling)
	private double m_gyro_x = 0.0;
	private double m_gyro_y = 0.0;
	private double m_gyro_z = 0.0;
	private double m_accel_x = 0.0;
	private double m_accel_y = 0.0;
	private double m_accel_z = 0.0;
	private double m_mag_x = 0.0;
	private double m_mag_y = 0.0;
	private double m_mag_z = 0.0;
	private double m_temp = 0.0;

	// accumulated gyro values (for offset calculation)
	private int m_accum_count = 0;
	private double m_accum_gyro_x = 0.0;
	private double m_accum_gyro_y = 0.0;
	private double m_accum_gyro_z = 0.0;

	// integrated gyro values
	private double m_integ_gyro_x = 0.0;
	private double m_integ_gyro_y = 0.0;
	private double m_integ_gyro_z = 0.0;

	// last sample time
	private double m_last_sample_time = 0.0;

	// Kalman (AHRS)
	private static final double kGyroScale = 0.0174533; // rad/sec
	private static final double kAccelScale = 9.80665; // mg/sec/sec
	private static final double kMagScale = 0.1; // uTesla
	private static final double kBeta = 1;
	private double m_ahrs_q1 = 1, m_ahrs_q2 = 0, m_ahrs_q3 = 0, m_ahrs_q4 = 0;

	// Complementary AHRS
	private boolean m_first = true;
	private double m_gyro_x_prev;
	private double m_gyro_y_prev;
	private double m_gyro_z_prev;
	private double m_mag_angle_prev = 0.0;
	private boolean m_tilt_comp_yaw = true;

	// AHRS outputs
	private double m_yaw = 0.0;
	private double m_roll = 0.0;
	private double m_pitch = 0.0;

	private AtomicBoolean m_freed = new AtomicBoolean(false);

	private DigitalInput m_interrupt;

	// Sample from the IMU
	private static class Sample {
		public double gyro_x;
		public double gyro_y;
		public double gyro_z;
		public double accel_x;
		public double accel_y;
		public double accel_z;
		public double mag_x;
		public double mag_y;
		public double mag_z;
		public double dt;

		// Swap axis as appropriate for yaw axis selection
		public void adjustYawAxis(Axis yaw_axis) {
			switch (yaw_axis) {
			case kX: {
				// swap X and Z
				double tmp;
				tmp = accel_x;
				accel_x = accel_z;
				accel_z = tmp;
				tmp = mag_x;
				mag_x = mag_z;
				mag_z = tmp;
				tmp = gyro_x;
				gyro_x = gyro_z;
				gyro_z = tmp;
				break;
			}
			case kY: {
				// swap Y and Z
				double tmp;
				tmp = accel_y;
				accel_y = accel_z;
				accel_z = tmp;
				tmp = mag_y;
				mag_y = mag_z;
				mag_z = tmp;
				tmp = gyro_y;
				gyro_y = gyro_z;
				gyro_z = tmp;
				break;
			}
			case kZ:
			default:
				// no swap required
				break;
			}
		}
	}

	// Sample FIFO
	private static final int kSamplesDepth = 10;
	private final Sample[] m_samples;
	private final Lock m_samples_mutex;
	private final Condition m_samples_not_empty;
	private int m_samples_count = 0;
	private int m_samples_take_index = 0;
	private int m_samples_put_index = 0;
	private boolean m_calculate_started = false;

	private static class AcquireTask implements Runnable {
		private LSM9DS1_IMU imu;

		public AcquireTask(LSM9DS1_IMU imu) {
			this.imu = imu;
		}

		@Override
		public void run() {
			imu.acquire();
		}
	}

	private static class CalculateTask implements Runnable {
		private LSM9DS1_IMU imu;

		public CalculateTask(LSM9DS1_IMU imu) {
			this.imu = imu;
		}

		@Override
		public void run() {
			imu.calculate();
		}
	}

	private Thread m_acquire_task;
	private Thread m_calculate_task;

	/**
	 * Constructor.
	 */
	public LSM9DS1_IMU(Axis yaw_axis, AHRSAlgorithm algorithm) {
		m_yaw_axis = yaw_axis;
		m_algorithm = algorithm;

		table = NetworkTable.getTable("Sensors on Pi");

		// Create data acq FIFO. We make the FIFO 2 longer than it needs
		// to be so the input and output never overlap (we hold a reference
		// to the output while the lock is released).
		m_samples = new Sample[kSamplesDepth + 2];
		for (int i = 0; i < kSamplesDepth + 2; i++) {
			m_samples[i] = new Sample();
		}
		m_samples_mutex = new ReentrantLock();
		m_samples_not_empty = m_samples_mutex.newCondition();

		// Configure interrupt on MXP DIO0 and start acquire task
		m_interrupt = new DigitalInput(10); // MXP DIO0
		m_acquire_task = new Thread(new AcquireTask(this));
		m_interrupt.requestInterrupts();
		m_interrupt.setUpSourceEdge(false, true);
		m_acquire_task.setDaemon(true);
		m_acquire_task.start();

		calibrate();

		// Start AHRS processing
		m_calculate_task = new Thread(new CalculateTask(this));
		m_calculate_task.setDaemon(true);
		m_calculate_task.start();

		// UsageReporting.report(tResourceType.kResourceType_ADIS16448, 0);
		LiveWindow.addSensor("LSM9DS1_IMU", 0, this);
	}

	/*
	 * Constructor assuming Complementary AHRS algorithm.
	 */
	public LSM9DS1_IMU(Axis yaw_axis) {
		this(yaw_axis, AHRSAlgorithm.kComplementary);
	}

	/*
	 * Constructor assuming yaw axis is "Z" and Complementary AHRS algorithm.
	 */
	public LSM9DS1_IMU() {
		this(Axis.kZ, AHRSAlgorithm.kComplementary);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void calibrate() {
		Timer.delay(0.1);

		synchronized (this) {
			m_accum_count = 0;
			m_accum_gyro_x = 0.0;
			m_accum_gyro_y = 0.0;
			m_accum_gyro_z = 0.0;
		}

		Timer.delay(kCalibrationSampleTime);

		synchronized (this) {
			m_gyro_offset_x = m_accum_gyro_x / m_accum_count;
			m_gyro_offset_y = m_accum_gyro_y / m_accum_count;
			m_gyro_offset_z = m_accum_gyro_z / m_accum_count;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void reset() {
		synchronized (this) {
			m_integ_gyro_x = 0.0;
			m_integ_gyro_y = 0.0;
			m_integ_gyro_z = 0.0;
		}
	}

	/**
	 * Delete (free) the spi port used for the IMU.
	 */
	@Override
	public void free() {
		m_freed.set(true);
		if (m_samples_mutex != null) {
			m_samples_mutex.lock();
			try {
				m_samples_not_empty.signal();
			} finally {
				m_samples_mutex.unlock();
			}
		}
		try {
			if (m_acquire_task != null) {
				m_acquire_task.join();
			}
			if (m_calculate_task != null) {
				m_calculate_task.join();
			}
		} catch (InterruptedException e) {
		}
		if (m_interrupt != null) {
			m_interrupt.free();
			m_interrupt = null;
		}
	}

	private void acquire() {
		synchronized (this) {
			m_last_sample_time = Timer.getFPGATimestamp();
		}
		while (!m_freed.get()) {
			if (m_interrupt.waitForInterrupt(kTimeout) == InterruptableSensorBase.WaitResult.kTimeout)
				continue;

			double sample_time = m_interrupt.readFallingTimestamp();
			double dt;
			synchronized (this) {
				dt = sample_time - m_last_sample_time;
				m_last_sample_time = sample_time;
			}

			double gyro_x = table.getNumber("X-Axis Accelleration", 0.0);
			double gyro_y = table.getNumber("Y-Axis Accelleration", 0.0);
			double gyro_z = table.getNumber("Z-Axis Accelleration", 0.0);
			double accel_x = table.getNumber("X-Axis Angle", 0.0);
			double accel_y = table.getNumber("Y-Axis Angle", 0.0);
			double accel_z = table.getNumber("Z-Axis Angle", 0.0);
			double mag_x = table.getNumber("X-Axis Magnetometer", 0.0);
			double mag_y = table.getNumber("Y-Axis Magnetometer", 0.0);
			double mag_z = table.getNumber("Z-Axis Magnetometer", 0.0);
			double temp = table.getNumber("Temperature", 0.0);

			m_samples_mutex.lock();
			try {
				// If the FIFO is full, just drop it
				if (m_calculate_started && m_samples_count < kSamplesDepth) {
					Sample sample = m_samples[m_samples_put_index];
					sample.gyro_x = gyro_x;
					sample.gyro_y = gyro_y;
					sample.gyro_z = gyro_z;
					sample.accel_x = accel_x;
					sample.accel_y = accel_y;
					sample.accel_z = accel_z;
					sample.mag_x = mag_x;
					sample.mag_y = mag_y;
					sample.mag_z = mag_z;
					sample.dt = dt;
					m_samples_put_index += 1;
					if (m_samples_put_index == m_samples.length) {
						m_samples_put_index = 0;
					}
					m_samples_count += 1;
					m_samples_not_empty.signal();
				}
			} finally {
				m_samples_mutex.unlock();
			}

			// Update global state
			synchronized (this) {
				m_gyro_x = gyro_x;
				m_gyro_y = gyro_y;
				m_gyro_z = gyro_z;
				m_accel_x = accel_x;
				m_accel_y = accel_y;
				m_accel_z = accel_z;
				m_mag_x = mag_x;
				m_mag_y = mag_y;
				m_mag_z = mag_z;
				m_temp = temp;

				m_accum_count += 1;
				m_accum_gyro_x += gyro_x;
				m_accum_gyro_y += gyro_y;
				m_accum_gyro_z += gyro_z;

				m_integ_gyro_x += (gyro_x - m_gyro_offset_x) * dt;
				m_integ_gyro_y += (gyro_y - m_gyro_offset_y) * dt;
				m_integ_gyro_z += (gyro_z - m_gyro_offset_z) * dt;
			}
		}
	}

	private void calculate() {
		while (!m_freed.get()) {
			// Wait for next sample and get it
			Sample sample;
			m_samples_mutex.lock();
			try {
				m_calculate_started = true;
				while (m_samples_count == 0) {
					m_samples_not_empty.await();
					if (m_freed.get()) {
						return;
					}
				}
				sample = m_samples[m_samples_take_index];
				m_samples_take_index += 1;
				if (m_samples_take_index == m_samples.length) {
					m_samples_take_index = 0;
				}
				m_samples_count -= 1;
			} catch (InterruptedException e) {
				break;
			} finally {
				m_samples_mutex.unlock();
			}

			switch (m_algorithm) {
			case kMadgwick:
				calculateMadgwick(sample, 0.4);
				break;
			case kComplementary:
			default:
				calculateComplementary(sample);
				break;
			}
		}
	}

	private void calculateMadgwick(Sample sample, double beta) {
		// Make local copy of quaternion and angle global state
		double q1, q2, q3, q4;
		synchronized (this) {
			q1 = m_ahrs_q1;
			q2 = m_ahrs_q2;
			q3 = m_ahrs_q3;
			q4 = m_ahrs_q4;
		}

		// Swap axis as appropriate for yaw axis selection
		sample.adjustYawAxis(m_yaw_axis);

		// Kalman calculation
		// Code originated from: https://decibel.ni.com/content/docs/DOC-18964
		do {
			// If true, only use gyros and magnetos for updating the filter.
			boolean excludeAccel = false;

			// Convert accelerometer units to m/sec/sec
			double ax = sample.accel_x * kAccelScale;
			double ay = sample.accel_y * kAccelScale;
			double az = sample.accel_z * kAccelScale;
			// Normalize accelerometer measurement
			double norm = Math.sqrt(ax * ax + ay * ay + az * az);
			if (norm > 0.3 && !excludeAccel) {
				// normal larger than the sensor noise floor during freefall
				norm = 1.0 / norm;
				ax *= norm;
				ay *= norm;
				az *= norm;
			} else {
				ax = 0;
				ay = 0;
				az = 0;
			}

			// Convert magnetometer units to uTesla
			double mx = sample.mag_x * kMagScale;
			double my = sample.mag_y * kMagScale;
			double mz = sample.mag_z * kMagScale;
			// Normalize magnetometer measurement
			norm = Math.sqrt(mx * mx + my * my + mz * mz);
			if (norm > 0.0) {
				norm = 1.0 / norm;
				mx *= norm;
				my *= norm;
				mz *= norm;
			} else {
				break; // something is wrong with the magneto readouts
			}

			double _2q1 = 2.0 * q1;
			double _2q2 = 2.0 * q2;
			double _2q3 = 2.0 * q3;
			double _2q4 = 2.0 * q4;
			double _2q1q3 = 2.0 * q1 * q3;
			double _2q3q4 = 2.0 * q3 * q4;
			double q1q1 = q1 * q1;
			double q1q2 = q1 * q2;
			double q1q3 = q1 * q3;
			double q1q4 = q1 * q4;
			double q2q2 = q2 * q2;
			double q2q3 = q2 * q3;
			double q2q4 = q2 * q4;
			double q3q3 = q3 * q3;
			double q3q4 = q3 * q4;
			double q4q4 = q4 * q4;

			// Reference direction of Earth's magnetic field
			double _2q1mx = 2 * q1 * mx;
			double _2q1my = 2 * q1 * my;
			double _2q1mz = 2 * q1 * mz;
			double _2q2mx = 2 * q2 * mx;

			double hx = mx * q1q1 - _2q1my * q4 + _2q1mz * q3 + mx * q2q2 + _2q2 * my * q3 + _2q2 * mz * q4 - mx * q3q3
					- mx * q4q4;
			double hy = _2q1mx * q4 + my * q1q1 - _2q1mz * q2 + _2q2mx * q3 - my * q2q2 + my * q3q3 + _2q3 * mz * q4
					- my * q4q4;
			double _2bx = Math.sqrt(hx * hx + hy * hy);
			double _2bz = -_2q1mx * q3 + _2q1my * q2 + mz * q1q1 + _2q2mx * q4 - mz * q2q2 + _2q3 * my * q4 - mz * q3q3
					+ mz * q4q4;
			double _4bx = 2.0 * _2bx;
			double _4bz = 2.0 * _2bz;
			double _8bx = 2.0 * _4bx;
			double _8bz = 2.0 * _4bz;

			// Gradient descent algorithm corrective step
			double s1 = -_2q3 * (2.0 * q2q4 - _2q1q3 - ax) + _2q2 * (2.0 * q1q2 + _2q3q4 - ay)
					- _4bz * q3 * (_4bx * (0.5 - q3q3 - q4q4) + _4bz * (q2q4 - q1q3) - mx)
					+ (-_4bx * q4 + _4bz * q2) * (_4bx * (q2q3 - q1q4) + _4bz * (q1q2 + q3q4) - my)
					+ _4bx * q3 * (_4bx * (q1q3 + q2q4) + _4bz * (0.5 - q2q2 - q3q3) - mz);
			double s2 = _2q4 * (2.0 * q2q4 - _2q1q3 - ax) + _2q1 * (2.0 * q1q2 + _2q3q4 - ay)
					- 4.0 * q2 * (1.0 - 2.0 * q2q2 - 2.0 * q3q3 - az)
					+ _4bz * q4 * (_4bx * (0.5 - q3q3 - q4q4) + _4bz * (q2q4 - q1q3) - mx)
					+ (_4bx * q3 + _4bz * q1) * (_4bx * (q2q3 - q1q4) + _4bz * (q1q2 + q3q4) - my)
					+ (_4bx * q4 - _8bz * q2) * (_4bx * (q1q3 + q2q4) + _4bz * (0.5 - q2q2 - q3q3) - mz);
			double s3 = -_2q1 * (2.0 * q2q4 - _2q1q3 - ax) + _2q4 * (2.0 * q1q2 + _2q3q4 - ay)
					- 4.0 * q3 * (1.0 - 2.0 * q2q2 - 2.0 * q3q3 - az)
					+ (-_8bx * q3 - _4bz * q1) * (_4bx * (0.5 - q3q3 - q4q4) + _4bz * (q2q4 - q1q3) - mx)
					+ (_4bx * q2 + _4bz * q4) * (_4bx * (q2q3 - q1q4) + _4bz * (q1q2 + q3q4) - my)
					+ (_4bx * q1 - _8bz * q3) * (_4bx * (q1q3 + q2q4) + _4bz * (0.5 - q2q2 - q3q3) - mz);
			double s4 = _2q2 * (2.0 * q2q4 - _2q1q3 - ax) + _2q3 * (2.0 * q1q2 + _2q3q4 - ay)
					+ (-_8bx * q4 + _4bz * q2) * (_4bx * (0.5 - q3q3 - q4q4) + _4bz * (q2q4 - q1q3) - mx)
					+ (-_4bx * q1 + _4bz * q3) * (_4bx * (q2q3 - q1q4) + _4bz * (q1q2 + q3q4) - my)
					+ _4bx * q2 * (_4bx * (q1q3 + q2q4) + _4bz * (0.5 - q2q2 - q3q3) - mz);

			norm = Math.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4);
			if (norm > 0.0) {
				norm = 1.0 / norm; // normalise gradient step
				s1 *= norm;
				s2 *= norm;
				s3 *= norm;
				s4 *= norm;
			} else {
				break;
			}

			// Convert gyro units to rad/sec
			double gx = sample.gyro_x * kGyroScale;
			double gy = sample.gyro_y * kGyroScale;
			double gz = sample.gyro_z * kGyroScale;

			// Compute rate of change of quaternion
			double qDot1 = 0.5 * (-q2 * gx - q3 * gy - q4 * gz) - kBeta * s1;
			double qDot2 = 0.5 * (q1 * gx + q3 * gz - q4 * gy) - kBeta * s2;
			double qDot3 = 0.5 * (q1 * gy - q2 * gz + q4 * gx) - kBeta * s3;
			double qDot4 = 0.5 * (q1 * gz + q2 * gy - q3 * gx) - kBeta * s4;

			// Integrate to yield quaternion
			q1 += qDot1 * sample.dt;
			q2 += qDot2 * sample.dt;
			q3 += qDot3 * sample.dt;
			q4 += qDot4 * sample.dt;

			norm = Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);
			if (norm > 0.0) {
				norm = 1.0 / norm; // normalise quaternion
				q1 = q1 * norm;
				q2 = q2 * norm;
				q3 = q3 * norm;
				q4 = q4 * norm;
			}
		} while (false);

		// Convert quaternion to angles of rotation
		double xi = -Math.atan2(2 * q2 * q3 - 2 * q1 * q4, 2 * (q1 * q1) + 2 * (q2 * q2) - 1);
		double theta = -Math.asin(2 * q2 * q4 + 2 * q1 * q3);
		double rho = Math.atan2(2 * q3 * q4 - 2 * q1 * q2, 2 * (q1 * q1) + 2 * (q4 * q4) - 1);

		// Convert angles from radians to degrees
		xi = xi / Math.PI * 180.0;
		theta = theta / Math.PI * 180.0;
		rho = rho / Math.PI * 180.0;

		// Adjust angles for inverted mount of MXP sensor
		theta = -theta;
		if (rho < 0)
			rho = 180 - Math.abs(rho);
		else
			rho = Math.abs(rho) - 180;

		// Update global state
		synchronized (this) {
			m_ahrs_q1 = q1;
			m_ahrs_q2 = q2;
			m_ahrs_q3 = q3;
			m_ahrs_q4 = q4;
			m_yaw = xi;
			m_roll = theta;
			m_pitch = rho;
		}
	}

	// Thank you to the RoboBees for providing this elegant AHRS implementation
	// to the FIRST community!
	private void calculateComplementary(Sample sample) {
		// Description:
		// Accepts calibrated Rate Gyro, Accelerometer, and Magnetometer sensor
		// readings and applies a Complementary Filter to fuse them into a
		// single
		// composite sensor which provides accurate and stable rotation
		// indications
		// (Pitch, Roll, and Yaw). This sensor fusion approach effectively
		// combines the individual sensor's best respective properties while
		// mitigating their shortfalls.
		//
		// Design:
		// The Complementary Filter is an algorithm that allows a pair of
		// sensors
		// to contribute differently to a common, composite measurement result.
		// It effectively applies a low pass filter to one sensor, and a high
		// pass
		// filter to the other, then proportionally recombines them in such a
		// way
		// to maintain the original unit of measurement. It is computationally
		// inexpensive when compared to alternative estimation techniques such
		// as
		// the Kalman filter. The algorithm is given by:
		//
		// angle(n) = (alpha)*(angle(n-1) + gyrorate * dt) + (1-alpha)*(accel or
		// mag);
		//
		// where :
		//
		// alpha = tau / (tau + dt)
		//
		// This implementation uses the average Gyro rate across the dt period,
		// so
		// above gyrorate = [(gyrorate(n)-gyrorate(n-1)]/2
		//
		// Essentially, for Pitch and Roll, the slow moving (lower frequency)
		// part
		// of the rotation estimate is taken from the Accelerometer - ignoring
		// the
		// high noise level, and the faster moving (higher frequency) part is
		// taken
		// from the Rate Gyro - ignoring the slow Gyro drift. Same for Yaw,
		// except
		// that the Magnetometer replaces the Accelerometer to source the slower
		// moving component. This is because Pitch and Roll can be referenced to
		// the Accelerometer's sense of the Earth's gravity vector. Yaw cannot
		// be
		// referenced to this vector since this rotation does not cause any
		// relative angular change, but it can be referenced to magnetic North.
		// The parameter 'tau' is the time constant that defines the boundary
		// between the low and high pass filters. Both tau and the sample time,
		// dt, affect the parameter 'alpha', which sets the balance point for
		// how
		// much of which sensor is 'trusted' to contribute to the rotation
		// estimate.
		//
		// The Complementary Filter algorithm is applied to each X/Y/Z rotation
		// axis to compute R/P/Y outputs, respectively.
		//
		// Magnetometer readings are tilt-compensated when Tilt-Comp-(Yaw) is
		// asserted (True), by the IMU TILT subVI. This creates what is known as
		// a
		// tilt-compensated compass, which allows Yaw to be insensitive to the
		// effects of a non-level sensor, but generates error in Yaw during
		// movement (coordinate acceleration).
		//
		// The Yaw "South" crossing detector is necessary to allow a smooth
		// transition across the +/- 180 deg discontinuity (inherent in the ATAN
		// function). Since -180 deg is congruent with +180 deg, Yaw needs to
		// jump
		// between these values when crossing South (North is 0 deg). The design
		// depends upon comparison of successive Yaw readings to detect a
		// cross-over event. The cross-over detector monitors the current
		// reading
		// and evaluates how far it is from the previous reading. If it is
		// greater
		// than the previous reading by the Discriminant (= 180 deg), then Yaw
		// just
		// crossed South.
		//
		// By choosing 180 as the Discriminant, the only way the detector can
		// produce a false positive, assuming a loop iteration of 70 msec, is
		// for
		// it to rotate >2,571 dps ... (2,571=180/.07). This is faster than the
		// ST
		// L3GD20 Gyro can register. The detector produces a Boolean True upon
		// detecting a South crossing. This is used to alter the (n-1) Yaw which
		// was previously stored, either adding or subtracting 360 degrees as
		// required to place the previous Yaw in the correct quadrant whenever
		// crossing occurs. The Modulus function cannot be used here as the
		// Complementary Filter algorithm has 'state' (needs to remember
		// previous
		// Yaw).
		//
		// We are in effect stitching together two ends of a ruler for 'modular
		// arithmetic' (clock math).
		//
		// Inputs:
		// GYRO - Gyro rate and sample time measurements.
		// ACCEL - Acceleration measurements.
		// MAG - Magnetic measurements.
		// TAU ACC - tau parameter used to set sensor balance between Accel and
		// Gyro for Roll and Pitch.
		// TAU MAG - tau parameter used to set sensor balance between Mag and
		// Gyro
		// for Yaw.
		// TILT COMP (Yaw) - Enables Yaw tilt-compensation if True.
		//
		// Outputs:
		// ROLL - Filtered Roll about sensor X-axis.
		// PITCH - Filtered Pitch about sensor Y-axis.
		// YAW - Filtered Yaw about sensor Z-axis.
		//
		// Implementation:
		// It's best to establish the optimum loop sample time first. See IMU
		// READ
		// implementation notes for guidance. Each tau parameter should then be
		// adjusted to achieve optimum sensor fusion. tau acc affects Roll and
		// Pitch, tau mag affects Yaw. Start at value 1 or 2 and decrease by
		// half
		// each time until the result doesn't drift, but not so far that the
		// result
		// gets noisy. An optimum tau for this IMU is likely in the range of 1.0
		// to 0.01, for a loop sample time between 10 and 100 ms.
		//
		// Note that both sample timing (dt) and tau both affect the balance
		// parameter, 'alpha'. Adjusting either dt or tau will require the other
		// to be readjusted to maintain a particular filter performance.
		//
		// It is likely best to set Yaw tilt-compensation to off (False) if the
		// Yaw
		// value is to be used as feedback in a closed loop control application.
		// The tradeoff is that Yaw will only be accurate while the robot is
		// level.
		//
		// Since a Yaw of -180 degrees is congruent with +180 degrees (they
		// represent the same direction), it is possible that the Yaw output
		// will
		// oscillate between these two values when the sensor happens to be
		// pointing due South, as sensor noise causes slight variation. You will
		// need to account for this possibility if you are using the Yaw value
		// for
		// decision-making in code.
		//
		// ----- The RoboBees FRC Team 836! -----
		// Complement your passion to solve problems with a STEM Education!

		// Compensate for PCB-Up Mounting Config.
		sample.gyro_y = -sample.gyro_y;
		sample.gyro_z = -sample.gyro_z;
		sample.accel_y = -sample.accel_y;
		sample.accel_z = -sample.accel_z;
		sample.mag_y = -sample.mag_y;
		sample.mag_z = -sample.mag_z;

		// Swap axis as appropriate for yaw axis selection
		sample.adjustYawAxis(m_yaw_axis);

		final double tau_acc = 0.95;
		final double tau_mag = 0.04;

		double roll, pitch, yaw;
		boolean tilt_comp_yaw;
		synchronized (this) {
			roll = m_roll;
			pitch = m_pitch;
			yaw = m_yaw;
			tilt_comp_yaw = m_tilt_comp_yaw;
		}

		// Calculate mag angle in degrees
		double mag_angle = Math.atan2(sample.mag_y, sample.mag_x) / Math.PI * 180.0;

		// Tilt compensation:
		// see http://www.freescale.com/files/sensors/doc/app_note/AN3461.pdf
		// for derivation of Pitch and Roll equations. Used eqs 37 & 38 as Rxyz.
		// Eqs 42 & 43, as Ryxz, produce same values within Pitch & Roll
		// constraints.
		//
		// Freescale's Pitch/Roll derivation is preferred over ST's as it does
		// not
		// degrade due to the Sine function linearity assumption.
		//
		// Pitch is accurate over +/- 90 degree range, and Roll is accurate
		// within
		// +/- 180 degree range - as long as accelerometer is only sensing
		// acceleration due to gravity. Movement (coordinate acceleration) will
		// add error to Pitch and Roll indications.
		//
		// Yaw is not obtainable from an accelerometer due to its geometric
		// relationship with the Earth's gravity vector. (Would have same
		// problem
		// on Mars.)
		//
		// see http://www.pololu.com/file/0J434/LSM303DLH-compass-app-note.pdf
		// for derivation of Yaw equation. Used eq 12 in Appendix A (eq 13 is
		// replaced by ATAN2 function). Yaw is obtainable from the magnetometer,
		// but is sensitive to any tilt from horizontal. This uses Pitch and
		// Roll
		// values from above for tilt compensation of Yaw, resulting in a
		// tilt-compensated compass.
		//
		// As with Pitch/Roll, movement (coordinate acceleration) will add error
		// to
		// Yaw indication.

		// Accel
		double tilt_pitch_rad = Math.atan2(-sample.accel_x,
				Math.sqrt(sample.accel_y * sample.accel_y + sample.accel_z * sample.accel_z));
		double tilt_pitch = tilt_pitch_rad / Math.PI * 180.0;

		double tilt_roll_rad = Math.atan2(sample.accel_y,
				Math.sqrt(sample.accel_x * sample.accel_x * 0.01 + sample.accel_z * sample.accel_z)
						* Math.signum(sample.accel_z));
		double tilt_roll = tilt_roll_rad / Math.PI * 180.0;

		// Mag
		double tilt_yaw;
		if (tilt_comp_yaw) {
			double sin_pitch = Math.sin(tilt_pitch_rad);
			double cos_pitch = Math.cos(tilt_pitch_rad);
			double sin_roll = Math.sin(tilt_roll_rad);
			double cos_roll = Math.cos(tilt_roll_rad);
			double mx2 = sample.mag_x * cos_pitch + sample.mag_z * sin_pitch;
			double my2 = sample.mag_x * sin_roll * sin_pitch + sample.mag_y * cos_roll
					- sample.mag_z * sin_roll * cos_pitch;
			// double mz2 = -sample.mag_x * cos_roll * sin_pitch + sample.mag_y
			// * sin_roll + sample.mag_z * cos_roll * cos_pitch;
			tilt_yaw = Math.atan2(my2, mx2) / Math.PI * 180.0;
		} else {
			tilt_yaw = mag_angle;
		}

		// Positive rotation of Magnetometer is clockwise when looking in + Z
		// direction. This is subtracted from 0 deg to reverse rotation
		// direction, as it needs to be aligned with the definition of positive
		// Gyroscope rotation, (which is CCW looking in + Z direction), to
		// enable
		// sensor fusion.
		//
		// 0 degrees is due magnetic North.
		tilt_yaw = -tilt_yaw;

		// "South" crossing Detector
		if (Math.abs(mag_angle - m_mag_angle_prev) >= 180) {
			if (m_mag_angle_prev < 0) {
				yaw += -360;
			} else if (m_mag_angle_prev > 0) {
				yaw += 360;
			}
		}
		m_mag_angle_prev = mag_angle;

		// alpha = tau / (tau + dt)
		double alpha_acc = tau_acc / (tau_acc + sample.dt);
		double alpha_mag = tau_mag / (tau_mag + sample.dt);

		// gyrorate = [(gyrorate(n)-gyrorate(n-1)]/2
		// angle(n) = (alpha)*(angle(n-1) + gyrorate * dt) + (1-alpha)*(accel or
		// mag);
		if (m_first) {
			m_gyro_x_prev = sample.gyro_x;
			m_gyro_y_prev = sample.gyro_y;
			m_gyro_z_prev = sample.gyro_z;
			m_first = false;
		}
		roll = alpha_acc * (roll + sample.dt * (sample.gyro_x + m_gyro_x_prev) / 2.0) + (1 - alpha_acc) * tilt_roll;
		pitch = alpha_acc * (pitch + sample.dt * (sample.gyro_y + m_gyro_y_prev) / 2.0) + (1 - alpha_acc) * tilt_pitch;
		yaw = alpha_mag * (yaw + sample.dt * (sample.gyro_z + m_gyro_z_prev) / 2.0) + (1 - alpha_mag) * tilt_yaw;
		m_gyro_x_prev = sample.gyro_x;
		m_gyro_y_prev = sample.gyro_y;
		m_gyro_z_prev = sample.gyro_z;

		// Update global state
		synchronized (this) {
			m_roll = roll;
			m_pitch = pitch;
			m_yaw = yaw;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public double getAngle() {
		return getYaw();
	}

	/**
	 * {@inheritDoc}
	 */
	public double getRate() {
		return getRateZ();
	}

	public synchronized double getAngleX() {
		return m_integ_gyro_x;
	}

	public synchronized double getAngleY() {
		return m_integ_gyro_y;
	}

	public synchronized double getAngleZ() {
		return m_integ_gyro_z;
	}

	public synchronized double getRateX() {
		return m_gyro_x;
	}

	public synchronized double getRateY() {
		return m_gyro_y;
	}

	public synchronized double getRateZ() {
		return m_gyro_z;
	}

	public synchronized double getAccelX() {
		return m_accel_x;
	}

	public synchronized double getAccelY() {
		return m_accel_y;
	}

	public synchronized double getAccelZ() {
		return m_accel_z;
	}

	public synchronized double getMagX() {
		return m_mag_x;
	}

	public synchronized double getMagY() {
		return m_mag_y;
	}

	public synchronized double getMagZ() {
		return m_mag_z;
	}

	public synchronized double getPitch() {
		return m_pitch;
	}

	public synchronized double getRoll() {
		return m_roll;
	}

	public synchronized double getYaw() {
		return m_yaw;
	}

	public synchronized double getLastSampleTime() {
		return m_last_sample_time;
	}

	public synchronized double getTemperature() {
		return m_temp;
	}

	// Get quaternion W for the Kalman AHRS.
	// Always returns 0 for the Complementary AHRS.
	public synchronized double getQuaternionW() {
		return m_ahrs_q1;
	}

	// Get quaternion X for the Kalman AHRS.
	// Always returns 0 for the Complementary AHRS.
	public synchronized double getQuaternionX() {
		return m_ahrs_q2;
	}

	// Get quaternion Y for the Kalman AHRS.
	// Always returns 0 for the Complementary AHRS.
	public synchronized double getQuaternionY() {
		return m_ahrs_q3;
	}

	// Get quaternion Z for the Kalman AHRS.
	// Always returns 0 for the Complementary AHRS.
	public synchronized double getQuaternionZ() {
		return m_ahrs_q4;
	}

	// Enable or disable yaw tilt-compensation for the Complementary AHRS.
	// Has no effect on the Kalman AHRS.
	//
	// It is likely best to set Yaw tilt-compensation to off (False) if the Yaw
	// value is to be used as feedback in a closed loop control application.
	// The tradeoff is that Yaw will only be accurate while the robot is level.
	public synchronized void setTiltCompYaw(boolean enabled) {
		m_tilt_comp_yaw = enabled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateTable() {
		ITable table = getTable();
		if (table != null) {
			table.putNumber("Value", getAngle());
			table.putNumber("Pitch", getPitch());
			table.putNumber("Roll", getRoll());
			table.putNumber("Yaw", getYaw());
			table.putNumber("AccelX", getAccelX());
			table.putNumber("AccelY", getAccelY());
			table.putNumber("AccelZ", getAccelZ());
			table.putNumber("AngleX", getAngleX());
			table.putNumber("AngleY", getAngleY());
			table.putNumber("AngleZ", getAngleZ());
		}
	}

	public double getMeasuresPerDegree() {
		return MEASURES_PER_DEGREE;
	}

	@Override
	public double getBarometricPressure() {
		return 0;
	}

}
