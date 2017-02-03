/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

/**
 * @author Bryan Duerk
 *
 */
public class PID
{

    public static int NUMBER_PID_CONTROLERS = 4;
    public static PID[] controller = new PID[NUMBER_PID_CONTROLERS+1]; // Controller 0 is reserverd.

    public final double p;
    public final double i;
    public final double d;
    public final double f;
    public final double maxSpeed;

    /**
     *
     */
    public PID(int id, double p, double i, double d, double f, double maxSpeed)
    {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
        this.maxSpeed = maxSpeed;
        PID.controller[id] = this;
    }

    public static void init() {
        new PID(0, 0.0, 0.0, 0.0, 0.0, 0.0); // Dummy value, there should never be a talon 0
        new PID(1, 2.16, 0.00864, 135.0, 3.0, 400);
        new PID(2, 2.16, 0.00864, 135.0, 3.0, 400);
        new PID(3, 2.16, 0.00864, 135.0, 3.0, 400);
        new PID(4, 2.16, 0.00864, 135.0, 3.0, 400);
    }

}
