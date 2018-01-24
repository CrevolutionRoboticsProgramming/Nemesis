package org.team2851.util.motion;

import org.team2851.util.motion.Point2D;

public abstract class Path
{
    private Point2D startPoint, endPoint;

    public Path(Point2D startPoint, Point2D endPoint)
    {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }
    public abstract double[][] getMotionProfile();
    public abstract boolean isMotionProfile();

    public double getAngle() { return startPoint.getAngle(endPoint); }
    public double getDistance() { return startPoint.getDistance(endPoint); }

    public Point2D getEndPoint() { return endPoint; }
    public Point2D getStartPoint() { return startPoint; }

    @Override
    public String toString() { return "Path { " + startPoint.toString() + "--> " + endPoint.toString() + " }"; }
}
