/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.*;

/**
 * 
 */
public class Drive extends RobotDrive
{
    // Single instance of this class
    private static Drive instance = null;

    // Steering objects
    public Steering[] steering;

    // Data storage object
    private DataStorage data;

    // Angle to turn at when rotating in place - initialized in constructor
    // takes the arctan of width over length in radians
    // Length is the wide side
    private static final double TURN_IN_PLACE_ANGLE = Math.atan(RobotMap.LENGTH / RobotMap.WIDTH);

    // Invert the drive motors to allow for wiring.
    private static final boolean FRONT_LEFT_DRIVE_INVERT = false;
    private static final boolean FRONT_RIGHT_DRIVE_INVERT = true;
    private static final boolean BACK_LEFT_DRIVE_INVERT = false;
    private static final boolean BACK_RIGHT_DRIVE_INVERT = true;

    // Speed modifier constants
    private static final double SPEED_SLOW_MODIFIER = 0.5;
    private static final double SPEED_TURBO_MODIFIER = 2.0;
    private static final double SPEED_MAX_MODIFIER = 0.8;
    private static final double SPEED_MAX_CHANGE = 0.15;

    // Speed to use for Strafe and Revolve Drive
    private static final double SPEED_STRAFE = 0.6;
// TODO: some stuff

    // Private constructor
    private Drive(CANTalon frontLeftMotor, CANTalon backLeftMotor, CANTalon frontRightMotor, CANTalon backRightMotor)
    {
        super(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);

        // Make objects
        data = DataStorage.getInstance();

        // Make steering array
        steering = new Steering[4];

        // Make all steering objects
        for (int i = 0; i < steering.length; i++)
        {
            // Read all steering values from saved robot data(Format = (<data key>, <backup value>))
            double steeringCenter = data.getDouble(RobotMap.STEERING_KEYS[i], RobotMap.STEERING_RANGE / 2);

            // Create Steering Object
            steering[i] = new Steering(RobotMap.PIDvalues[i], RobotMap.STEERING_MOTOR_CHANNELS[i],
                    RobotMap.STEERING_SENSOR_CHANNELS[i], steeringCenter);
        }
    }

    /**
     * Gets the single instance of this class.
     * 
     * @return The single instance.
     */
    public static Drive getInstance()
    {
        if (instance == null)
        {
            // First usage - create Drive object
            CANTalon frontleft = new CANTalon(RobotMap.FRONT_LEFT_MOTOR_CHANNEL);
            CANTalon backleft = new CANTalon(RobotMap.BACK_LEFT_MOTOR_CHANNEL);
            CANTalon frontright = new CANTalon(RobotMap.FRONT_RIGHT_MOTOR_CHANNEL);
            CANTalon backright = new CANTalon(RobotMap.BACK_RIGHT_MOTOR_CHANNEL);
            instance = new Drive(frontleft, backleft, frontright, backright);
        }
        return instance;
    }

    /**
     * Turns on the PID for all wheels.
     */
    public void enableSteeringPID()
    {
        for (int i = 0; i < steering.length; i++)
        {
            steering[i].enablePID();
        }
    }

    /**
     * Turns off the PID for all wheels.
     */
    public void disableSteeringPID()
    {
        for (int i = 0; i < steering.length; i++)
        {
            steering[i].disablePID();
        }
    }

    /**
     * Drives each of the four wheels at different speeds using invert constants
     * to account for wiring.
     *
     * @param frontLeftSpeed
     * @param frontRightSpeed
     * @param backLeftSpeed
     * @param backRightSpeed
     */
    private void fourWheelDrive(double frontLeftSpeed, double frontRightSpeed, double backLeftSpeed, double backRightSpeed)
    {
        // If any of the motors doesn't exist then exit
        if (m_rearLeftMotor == null || m_rearRightMotor == null || m_frontLeftMotor == null || m_frontRightMotor == null)
        {
            throw new NullPointerException("Null motor provided");
        }
        
        final double MAX_DRIVE_ANGLE = Math.PI / 25;
        
        // Don't drive until wheels are close to the commanded steering angle
        if (steering[RobotMap.FRONT_LEFT] .getAngleDelta() < MAX_DRIVE_ANGLE ||
            steering[RobotMap.FRONT_RIGHT].getAngleDelta() < MAX_DRIVE_ANGLE ||
            steering[RobotMap.BACK_LEFT]  .getAngleDelta() < MAX_DRIVE_ANGLE ||
            steering[RobotMap.BACK_RIGHT] .getAngleDelta() < MAX_DRIVE_ANGLE)
        {
            m_frontLeftMotor.set((FRONT_LEFT_DRIVE_INVERT ? -1 : 1) * limitSpeed((frontLeftSpeed), RobotMap.FRONT_LEFT));
            m_frontRightMotor.set((FRONT_RIGHT_DRIVE_INVERT ? -1 : 1) * limitSpeed(frontRightSpeed, RobotMap.FRONT_RIGHT));
            m_rearLeftMotor.set((BACK_LEFT_DRIVE_INVERT ? -1 : 1) * limitSpeed(backLeftSpeed, RobotMap.BACK_LEFT));
            m_rearRightMotor.set((BACK_RIGHT_DRIVE_INVERT ? -1 : 1) * limitSpeed(backRightSpeed, RobotMap.BACK_RIGHT));
        } 
        else
        {
            m_frontLeftMotor.set(0);
            m_frontRightMotor.set(0);
            m_rearLeftMotor.set(0);
            m_rearRightMotor.set(0);
        }

        if (m_safetyHelper != null)
        {
            m_safetyHelper.feed();
        }
    }

    /**
     * @param frontLeft
     * @param frontRight
     * @param backLeft
     * @param backRight
     */
    private void fourWheelSteer(double frontLeft, double frontRight, double backLeft, double backRight)
    {
        // set the angles to steer
        steering[RobotMap.FRONT_LEFT].setAngle(frontLeft);
        steering[RobotMap.FRONT_RIGHT].setAngle(frontRight);
        steering[RobotMap.BACK_LEFT].setAngle(backLeft);
        steering[RobotMap.BACK_RIGHT].setAngle(backRight);
    }

    /**
     * Set angles in "turn in place" position
     * Wrap around will check whether the closest angle is facing forward or backward
     * 
     * Front Left- / \ - Front Right<br>
     * Back Left - \ / - Back Right
     * 
     * @param speed
     */
    public void turnDrive(double speed)
    {
        WheelCorrection frontLeft = wrapAroundCorrect(RobotMap.FRONT_LEFT, TURN_IN_PLACE_ANGLE, -speed);
        WheelCorrection frontRight = wrapAroundCorrect(RobotMap.FRONT_RIGHT, -TURN_IN_PLACE_ANGLE, speed);
        WheelCorrection backLeft = wrapAroundCorrect(RobotMap.BACK_LEFT, -TURN_IN_PLACE_ANGLE, -speed);
        WheelCorrection backRight = wrapAroundCorrect(RobotMap.BACK_RIGHT, TURN_IN_PLACE_ANGLE, speed);               
        
        this.fourWheelSteer(frontLeft.angle, frontRight.angle, backLeft.angle, backRight.angle);
        this.fourWheelDrive(frontLeft.speed, frontRight.speed, backLeft.speed, backRight.speed);
    }
    
    // Previous speeds for the four wheels
    private double lastSpeed[] = new double[]{0.0,0.0,0.0,0.0};

    /**
     * Limit the rate at which the robot can change speed once driving fast.
     * This is to prevent causing mechanical damage - or tipping the robot
     * through stopping too quickly.
     *
     * @param speed
     *            desired speed for robot
     * @return returns rate-limited speed
     */
    private double limitSpeed(double speed, int wheelID)
    {
        // Apply speed modifiers first

        if (DriverStation2015.getInstance().getSlow())
        {
            speed *= SPEED_SLOW_MODIFIER;
        }
        else if (DriverStation2015.getInstance().getTurbo())
        {
            speed *= SPEED_TURBO_MODIFIER;
        }
        else
        {
            // Limit maximum regular speed to specified Maximum.
            speed *= SPEED_MAX_MODIFIER;
        }

        // Limit the rate at which robot can change speed once driving over 0.6
        if (Math.abs(speed - lastSpeed[wheelID]) > SPEED_MAX_CHANGE && Math.abs(lastSpeed[wheelID]) > 0.6)
        {
            if (speed > lastSpeed[wheelID])
            {
                speed = lastSpeed[wheelID] + SPEED_MAX_CHANGE;
            }
            else
            {
                speed = lastSpeed[wheelID] - SPEED_MAX_CHANGE;
            }
        }
        lastSpeed[wheelID] = speed;
        return (speed);
    }

    /**
     * Crab Drive
     *
     * @param angle
     *            value corresponding to the field direction to move in
     * @param speed
     *            Speed to drive at
     */
    public void crabDrive(double angle, double speed)
    {
        WheelCorrection corrected = wrapAroundCorrect(RobotMap.BACK_RIGHT, angle, speed);
        fourWheelSteer(corrected.angle, corrected.angle, corrected.angle, corrected.angle);
        fourWheelDrive(corrected.speed, corrected.speed, corrected.speed, corrected.speed);
    }

    /**
     * Individually controls a specific steering motor
     *
     * @param angle
     *            Angle to drive to
     * @param speed
     *            Speed to drive at
     * @param steeringId
     *            Id of steering motor to drive
     */
    public void individualSteeringDrive(double angle, int steeringId)
    {
        // Set steering angle
        steering[steeringId].setAngle(angle);
    }

    /**
     * Does not drive drive motors and keeps steering angle at previous position.
     */
    public void stop()
    {
        this.fourWheelDrive(0, 0, 0, 0);// no drive for you!
    }

    /**
     * Drive left or right at a fixed speed.
     * 
     * @param direction
     * @param speed
     */
    
    
    public void strafeDrive(int POVangle)
    {    	
        double speed = SPEED_STRAFE;
        double angle = POVangle*Math.PI / 180;
        crabDrive(angle, speed);  
    }
    
    
    /**
     * Individually controls a specific driving motor
     *
     * @param speed
     *            Speed to drive at
     * @param steeringId
     *            Id of driving motor to drive
     */
    public void individualWheelDrive(double speed, int steeringId)
    {
        double frontLeftSpeed = 0.0;
        double frontRightSpeed = 0.0;
        double rearLeftSpeed = 0.0;
        double rearRightSpeed = 0.0;

        switch (steeringId)
        {
            case RobotMap.FRONT_LEFT:
                frontLeftSpeed = 1.0;
                break;
            case RobotMap.FRONT_RIGHT:
                frontRightSpeed = speed * 1.0;
                break;
            case RobotMap.BACK_LEFT:
                rearLeftSpeed = speed * 1.0;
                break;
            case RobotMap.BACK_RIGHT:
                rearRightSpeed = speed * 1.0;
                break;
        }

        fourWheelDrive(frontLeftSpeed, frontRightSpeed, rearLeftSpeed, rearRightSpeed);
    }

    /**
     * Function to determine the wrapped around difference from the joystick
     * angle to the steering angle.
     *
     * @param value1
     *            - The first angle to check against
     * @param value2
     *            - The second angle to check against
     * @return The normalized wrap around difference
     */
    static double wrapAroundDifference(double value1, double value2)
    {
        double diff = Math.abs(value1 - value2) % (2 * Math.PI);
        while (diff > Math.PI)
        {
            diff = (2.0 * Math.PI) - diff;
        }
        return diff;
    }

    /**
     * Only used for steering
     * 
     * @param steeringIndex
     *            - which wheel pod
     * @param targetAngle
     *            - in radians
     * @param targetSpeed
     * @return corrected
     */
    private WheelCorrection wrapAroundCorrect(int steeringIndex, double targetAngle, double targetSpeed)
    {
        WheelCorrection corrected = new WheelCorrection(targetAngle, targetSpeed);

        double normalizedSteeringAngle = steering[steeringIndex].getSteeringAngle() % (Math.PI * 2);
        if (wrapAroundDifference(normalizedSteeringAngle, targetAngle) > Math.PI / 2)
        {
            // shortest path to desired angle is to reverse speed and adjust angle -PI
            corrected.speed *= -1;

            corrected.angle -= Math.PI;
        }
        return corrected;
    }

    /**
     * Set the steering center to a new value
     *
     * @param steeringMotor
     *            The id of the steering motor (0 = FL, 1 = FR, 2 = BL, 3 = BR)
     * @param value
     *            The new center value
     */
    public void setSteeringCenter(int steeringMotor, double value)
    {
        steering[steeringMotor].setCenter(value);
    }

    /**
     * Get the steering angle of the corresponding steering motor
     *
     * @param steeringId
     *            The id of the steering motor
     * @return
     */
    public double getSteeringAngle(int steeringMotor)
    {
        return steering[steeringMotor].getSensorValue();
    }

    /**
     * Get the normalized steering angle of the corresponding steering motor
     *
     * @param steeringId
     *            The id of the steering motor
     * @return
     */
    public double getNormalizedSteeringAngle(int steeringMotor)
    {
        return steering[steeringMotor].getSteeringAngle();
    }

}

class WheelCorrection
{
    public double speed;
    public double angle;

    public WheelCorrection(double angleIn, double speedIn)
    {
        angle = angleIn;
        speed = speedIn;
    }

    @Override
    public String toString()
    {
        return "WheelCorrection [speed=" + speed + ", angle=" + angle + "]";
    }
}
