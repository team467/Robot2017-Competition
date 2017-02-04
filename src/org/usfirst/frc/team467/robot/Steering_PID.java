package org.usfirst.frc.team467.robot;

public class Steering_PID
{
    double p;
    double i;
    double d;

    Steering_PID(double p, double i, double d)
    {
        this.p = p;
        this.i = i;
        this.d = d;
    }
}