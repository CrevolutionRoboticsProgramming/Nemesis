package org.team2851.robot;
import org.team2851.robot.auton.BaselineGucciSide;
import org.team2851.robot.auton.Left;
import org.team2851.util.*;
import org.team2851.util.subsystem.Subsystem;

public class Robot extends CrevoRobot
{
    public static Controller pilot, copilot;

    public Robot() {
        ConfigFile.readFile();
        registerSubsystem(DriveTrain.getInstance());
        registerSubsystem(Lift.getInstance());
        registerSubsystem(Intake.getInstance());

        registerAuton(new Left());
        registerAuton(new BaselineGucciSide());

        try {
            pilot = ConfigFile.getController("pilot.xml");
            copilot = ConfigFile.getController("copilot.xml");
        } catch (ElementNotFoundException e) {
            Logger.printerr("Could not parse pilot controller");
            Subsystem.teleopEnabled = false;
        }
    }
}
