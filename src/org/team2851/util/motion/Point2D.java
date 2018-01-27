package org.team2851.util.motion;

public class Point2D // Represents a point on the field
{
    private double x, y;
    public Point2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getDistance(Point2D point)
    {
        return Math.sqrt(Math.pow(point.x - x, 2) + Math.sqrt(point.y - y));
    }

    public double getAngle(Point2D point)
    {
        return Math.atan2(point.x - x, point.y - y);
    }
}
