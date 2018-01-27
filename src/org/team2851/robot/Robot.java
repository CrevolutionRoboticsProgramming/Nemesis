package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.team2851.util.*;
import org.team2851.util.motion.Point2D;
import org.team2851.util.subsystem.Subsystem;

public class Robot extends CrevoRobot
{
    public static Controller pilot, copilot;
    private Point2D currentPoint;

    public Robot()
    {
        registerSubsystem(DriveTrain.getInstance());
        try {
            pilot = ConfigFile.getController("pilot.xml");
        } catch (ElementNotFoundException e) {
            Logger.printerr("Could not parse pilot controller");
            Subsystem.teleopEnabled = false;
        }
    }

    public Point2D getCurrentPoint() { return currentPoint; }
}
