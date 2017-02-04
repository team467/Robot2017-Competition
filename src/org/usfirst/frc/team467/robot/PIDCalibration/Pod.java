/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

/**
 * @author Bryan Duerk
 *
 */
public enum Pod
{
    FRONT_RIGHT(1, "FrontRight", "FR", true),
    FRONT_LEFT(2, "FrontLeft", "FL", false),
    REAR_LEFT(3, "BackLeft", "RL", false),
    REAR_RIGHT(4, "BackRight", "RR", true);

    public final int id;
    public final String name;
    public final String abr;
    public final boolean isReversed;

    Pod(int id, String name, String abr, boolean isReversed) {
        this.id = id;
        this.name = name;
        this.abr = abr;
        this.isReversed = isReversed;
    }

    public static Pod idToPod(int id) {
        switch (id) {
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
