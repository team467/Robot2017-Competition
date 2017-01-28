/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

import edu.wpi.first.wpilibj.command.Command;

/**
 * @author Bryan Duerk
 *
 */
public class FullSpeedAhead extends Command
{

    /**
     *
     */
    public FullSpeedAhead()
    {
        this("Full speed ahead", 10);
    }

    /**
     * @param name
     */
    public FullSpeedAhead(String name)
    {
        this (name, 10);
    }

    /**
     * @param timeout
     */
    public FullSpeedAhead(double timeout)
    {
        this("Full speed ahead", timeout);
    }

    /**
     * @param name
     * @param timeout
     */
    public FullSpeedAhead(String name, double timeout)
    {
        super(name, timeout);
    }

    protected void initialize() {
    }

    @Override
    protected boolean isFinished()
    {
        return isTimedOut();
    }

}
