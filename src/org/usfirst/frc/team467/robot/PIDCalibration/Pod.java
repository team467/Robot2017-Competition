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
    FRONT_RIGHT(1, "Front Right Pod"),
    FRONT_LEFT(2, "Front Left Pod"),
    REAR_RIGHT(3, "Rear Right Pod"),
    REAR_LEFT(4, "Rear Left Pod");

    public final int id;
    public final String name;

    Pod(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
