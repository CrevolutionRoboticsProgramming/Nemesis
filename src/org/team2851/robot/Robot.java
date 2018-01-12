package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import org.jdom2.DataConversionException;
import org.team2851.robot.subsystem.Intake;
import org.team2851.util.*;
import org.team2851.util.subsystem.Subsystem;

public class Robot extends CrevoRobot
{
    /*
     *  Current Configuration: Talos (2017 Season/Steamworks)
     *  kfdkjsglkkghviaejriauerahdkvcjashfljhbvasbcpsehbrfqwvgawegriugaigdkjcbvlewgiebflk
     */
    public static Controller pilot, copilot;
    TalonSRX talon;

    public Robot()
    {
        registerSubsystem(Intake.getInstance());
    }
}
