package org.usfirst.frc.team467.robot;

public class PID
{
    double p;
    double i;
    double d;

    PID(double p, double i, double d)
    {
        this.p = p;
        this.i = i;
        this.d = d;
    }
}