package org.team2851.robot;

import com.ctre.CANTalon;
import org.jdom2.DataConversionException;
import org.team2851.util.*;
import org.team2851.util.subsystem.Subsystem;

public class Robot extends CrevoRobot
{
    /*
     *  Current Configuration: Talos (2017 Season/Steamworks)
     */
    public static Controller pilot, copilot;
    CANTalon talon;

    public Robot()
    {
        try {
            pilot = ConfigFile.getController("pilot.xml");
        } catch (ElementNotFoundException e) {
            Logger.printerr("Controller config not found! Disabling teleop!");
            Subsystem.teleopEnabled = false;
        }
    }
}
