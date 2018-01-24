package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.team2851.util.*;

public class Robot extends CrevoRobot
{
    public static Controller pilot, copilot;
    TalonSRX talon;

    public Robot()
    {
        registerSubsystem(Intake.getInstance());
    }
}
