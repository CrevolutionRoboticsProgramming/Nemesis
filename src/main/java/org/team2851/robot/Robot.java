package org.team2851.robot;
import org.team2851.util.*;
import org.team2851.util.motion.Point2D;
import org.team2851.util.subsystem.Subsystem;

public class Robot extends CrevoRobot
{
    public static Controller pilot, copilot;
    private static Point2D currentPoint;
    private static double currentAngle; // Field-Centric

    public Robot()
    {
        registerSubsystem(DriveTrain.getInstance());
        try {
            pilot = ConfigFile.getController("pilot.xml");
            copilot = ConfigFile.getController("copilot.xml");
        } catch (ElementNotFoundException e) {
            Logger.printerr("Could not parse pilot controller");
            Subsystem.teleopEnabled = false;
        }
    }

    public static Point2D getCurrentPoint() { return currentPoint; }
    public static void setCurrentPoint(Point2D point) { currentPoint = point; }

    public static double getCurrentAngle() { return currentAngle; }
    public static void setCurrentAngle(double currentAngle) { Robot.currentAngle = currentAngle; }
}
