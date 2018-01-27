package org.team2851.util.motion;

import org.team2851.util.motion.Point2D;

import java.util.*;

public class Path
{
    private List<Point2D> points = new ArrayList<>();
    private int currentIndex = 0;

    public void addPoint(Point2D point)
    {
        points.add(point);
    }

    public Point2D getNextPoint() throws PathCompleteException
    {
        if (currentIndex >= points.size())
        {
            throw new PathCompleteException();
        } else {
            return points.get(currentIndex++);
        }
    }

    public void reset()
    {
        currentIndex = 0;
    }

    class PathCompleteException extends Exception {}
}
