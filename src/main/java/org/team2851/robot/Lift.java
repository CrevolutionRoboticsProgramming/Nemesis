package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import org.team2851.util.ConfigFile;
import org.team2851.util.Controller;
import org.team2851.util.ElementNotFoundException;
import org.team2851.util.LatchedBoolean;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

public class Lift extends Subsystem
{
    private static Lift instance = new Lift();
    public static Lift getInstance() { return instance; }
    private Lift() { super("Lift"); }
    private double currentPower = 0.0D;

    private TalonSRX talon;
    private final double circumference = 2.5;

    @Override
    public void init()
    {
        try {
            talon = ConfigFile.getTalonSRX("talonLift");
            talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
            talon.configPeakOutputForward(1, 0);
            talon.configPeakOutputReverse(-1, 0);
            talon.configNominalOutputForward(0, 0);
            talon.configNominalOutputReverse(0, 0);
            talon.getClosedLoopError(0);
            talon.config_kP(0, 1, 0);
            talon.config_kI(0, 0.0001, 0);
        } catch (ElementNotFoundException e) {
            isEnabled = false;
            logError("Could not find talon element. Disabling subsystem.");
        }

        useDefaultAlways = false;
    }

    @Override
    public Command getDefaultCommand()
    {
        return new Command()
        {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {

            }

            @Override
            public void update() {
                talon.set(ControlMode.PercentOutput, currentPower);
            }

            @Override
            public void done() {
                talon.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void interrupt() {

            }

            @Override
            public String getName() {
                return "Default";
            }
        };
    }

    @Override
    public Command getTeleopCommand() {
        return new Command()
        {
            private Controller c = Robot.copilot;

            @Override
            public boolean isFinished() { return false; }

            @Override
            public void start()
            {
                if (Preferences.getInstance().getBoolean("Single Controller", false)) c = Robot.pilot;
            }

            @Override
            public void update()
            {
                if (c.a.getState()) talon.set(ControlMode.PercentOutput, .9);
                else if (c.b.getState()) talon.set(ControlMode.PercentOutput, -.5);
                else if (c.x.getState()) talon.set(ControlMode.PercentOutput, 0.15); // Stalls motor (Keeps lift up)
                else talon.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void done() { currentPower = 0; }

            @Override
            public void interrupt() { done(); }

            @Override
            public String getName() { return "Teleop"; }
        };
    }

    public Command liftForTime(double time, boolean goingDown)
    {
        return new Command() {
            Timer t = new Timer();
            double power;
            @Override
            public boolean isFinished() {
                return t.get() > time;
            }

            @Override
            public void start()
            {
                t.start();
                power = (goingDown) ? -0.3 : 0.8;
            }

            @Override
            public void update()
            {
                System.out.println(power);
                talon.set(ControlMode.PercentOutput, power);
            }

            @Override
            public void done() {
                talon.set(ControlMode.PercentOutput, 0.15);
                currentPower = (goingDown) ? 0 : 0.15;
            }

            @Override
            public void interrupt() {
                currentPower = 0;
            }

            @Override
            public String getName() {
                return "LiftForTime[" + time + "]";
            }
        };
    }
}
