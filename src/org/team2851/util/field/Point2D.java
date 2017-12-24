package org.team2851.util.field;

public class Point2D // Represents a point on the field
{
    private double x, y;
    private String name;
    private static int pnt = 0;

    public Point2D(String name, double x, double y)
    {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public Point2D(double x, double y) { this("Point_" + pnt++, x, y); }

    public double getDistance(Point2D point)
    {
        return Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
    }

    public double getAngle(Point2D point)
    {
        return Math.toDegrees(Math.atan2(point.x - x, point.y - y));
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public String getName() { return name; }

    @Override
    public String toString() { return "Point2D(" + x + ", " + y + ")"; }
}
