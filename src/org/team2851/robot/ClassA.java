package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;

public class ClassA extends IterativeRobot
{
    TalonSRX talon;
    public void robotInit()
    {
        talon = new TalonSRX(0);
    }
    Timer t;
    public void autonomousInit()
    {
        t = new Timer();
        t.start();
    }

    public void autonomousPeriodic()
    {
        if (t.get() < 2) talon.set(ControlMode.PercentOutput, 1);
    }
}
