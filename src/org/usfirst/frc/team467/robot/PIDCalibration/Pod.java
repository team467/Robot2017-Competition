/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

/**
 * Specifies the wheel pod names and position.
 * It also specifies if the wheel pod is mounted in a reverse direction,
 * generally on the right side of the robot.
 */
public enum Pod
{
    FRONT_RIGHT(1, "FrontRight", "FR", true),
    FRONT_LEFT(2, "FrontLeft", "FL", false),
    REAR_LEFT(3, "BackLeft", "RL", false),
    REAR_RIGHT(4, "BackRight", "RR", true);

    /**
     * The Talon device identifier
     */
    public final int id;

    /**
     * The full wheel pod name
     */
    public final String name;

    /**
     * The abbreviated wheel pod name
     */
    public final String abr;

    /**
     * true if the wheel pod is mounted in the reversed direction
     */
    public final boolean isReversed;

    /**
     * Creates a wheel pod.
     *
     * @param id
     *            the Talon device id
     * @param name
     *            the long name for the wheel pod
     * @param abr
     *            the abbreviated name of the wheel pod, generally used for the SmartDashboard
     * @param isReversed
     *            true if the wheel pod is mounted in the reverse direction
     */
    Pod(int id, String name, String abr, boolean isReversed)
    {
        this.id = id;
        this.name = name;
        this.abr = abr;
        this.isReversed = isReversed;
    }

    /**
     * Gets the wheel pod given the Talon device identifier.
     *
     * @param id
     *            the Talon device identifier
     * @return the wheel pod
     */
    public static Pod idToPod(int id)
    {
        switch (id)
        {
            case 1:
                return FRONT_RIGHT;
            case 2:
                return FRONT_LEFT;
            case 3:
                return REAR_LEFT;
            case 4:
                return REAR_RIGHT;
            default:
                return Pod.FRONT_RIGHT;
        }
    }
}
