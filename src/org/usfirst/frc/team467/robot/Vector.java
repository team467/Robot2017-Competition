package org.usfirst.frc.team467.robot;

/*
 * Vector class almost specifically for vector drive
 * vector class made by Nathan long ago
 * updated to use LookUpTable
 * 
 * */
public class Vector
{
    private double angle;
    private double speed;
    private double x;
    private double y;

    public static Vector makeSpeedAngle(double speed, double angle)
    {
        Vector v = new Vector();
        v.setSpeedAngle(speed, angle);
        return v;
    }
    
    public static Vector makeXY(double x, double y)
    {
        Vector v = new Vector();
        v.setXY(x, y);
        return v;
    }

    public static Vector makeUnit(double angle)
    {
        Vector v = new Vector();
        v.setSpeedAngle(1.0, angle);
        return v;
    }
    
    private void setSpeedAngle(double speed, double angle)
    {
        this.speed = speed;
        this.angle = angle;
        x = LookUpTable.getCos(angle) * speed;
        y = LookUpTable.getSin(angle) * speed;
    }
    
    private void setXY(double x, double y)
    {
        this.x = x;
        this.y = y;
        angle = LookUpTable.getArcTan2hacked(y, x);
        speed = Math.sqrt(x*x + y*y);
    }
    
    public static Vector add(Vector v1, Vector v2)
    {
        return makeXY(v1.getX() + v2.getX(), v1.getY() + v2.getY());
    }
    
    public static Vector average(Vector v1, Vector v2)
    {
        return makeXY(v1.getX()/2 + v2.getX()/2, v1.getY()/2 + v2.getY()/2);
    }
    
    public double getAngle()
    {
        return angle;
    }

    public double getSpeed()
    {
        return speed;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }
    
    /**
     * 
     * @param value
     * @return value expressed as multiple of pi
     */
    static public String r(double value)
    {
        return String.format("%4.2fpi", value / Math.PI);
    }
    
    @Override
    public String toString()
    {
        return "WheelCorrection [angle=" + r(angle) + " speed=" + speed + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(angle);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(speed);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        final double epsilon = 0.000001;
        
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vector other = (Vector) obj;
        if (Math.abs(speed - other.speed) > epsilon)
            return false;
        if (Math.abs(angle - other.angle) > epsilon)
            return false;
        return true;
    }
    
}