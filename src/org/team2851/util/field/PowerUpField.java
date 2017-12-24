package org.team2851.util.field;

import java.util.Vector;

public class PowerUpField
{
    private Vector<Point2D> points = new Vector<>();
    private static PowerUpField instance = new PowerUpField();

    private PowerUpField()
    {
        // Initialize points here
    }

    public Point2D getPoint(String name)
    {
        for (int i = 0; i < points.size(); i++)
            if (points.elementAt(i).getName().equals(name)) return points.elementAt(i);
        return new Point2D("NULL", 0, 0);
    }
}
