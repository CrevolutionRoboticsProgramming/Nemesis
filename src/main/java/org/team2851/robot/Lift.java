package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
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

    private TalonSRX talon;

    @Override
    public void init()
    {
        try {
            talon = ConfigFile.getInstance().getTalonSRX("talonLift");
            talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
            talon.configPeakOutputForward(1, 0);
            talon.configPeakOutputReverse(-1, 0);
            talon.configNominalOutputForward(0, 0);
            talon.configNominalOutputReverse(0, 0);

            talon.config_kP(0, 1, 0);
            talon.config_kI(0, 0.0001, 0);
        } catch (ElementNotFoundException e) {
            isEnabled = false;
            logError("Could not find talon element. Disabling subsystem.");
        }
    }

    @Override
    public Command getDefaultCommand() { return new DefaultCommand(); }

    @Override
    public Command getTeleopCommand() {
        return new Command()
        {
            // a : up
            // b : down
            // temp (time) : 0.5 seconds
            private Controller c = Robot.copilot;
            private LatchedBoolean a = new LatchedBoolean();
            private LatchedBoolean b = new LatchedBoolean();
            private Timer t = new Timer();

            private final double circumference = 2.5; // cm
            private final double[] setPoints = { 0, 20, 30, 100 };
            private int pos = 0;

            private int heightToCounts(double height) { return (int)(height * circumference * 4096); }

            @Override
            public boolean isFinished() { return false; }

            @Override
            public void start() { }

            @Override
            public void update()
            {
//                if (a.getValue(c.a.getState()))
//                {
//                    t.start();
//                    while (t.get() < 0.5) talon.set(ControlMode.PercentOutput, 0.5);
//                    t.stop();
//                    t.reset();
//                } else if (b.getValue(c.b.getState()))
//                {
//                    t.start();
//                    while (t.get() < 0.5) talon.set(ControlMode.PercentOutput, -0.5);
//                    t.stop();
//                    t.reset();
//                }
                if (c.leftTrigger.getValue() > 0.3)
                {
                    if (c.a.getState()) talon.set(ControlMode.PercentOutput, 0.1);
                    else if (c.b.getState()) talon.set(ControlMode.PercentOutput, -0.1);
                } else if (a.getValue(c.a.getState()))
                {
                    if (pos == 0)
                    {
                        logMessage("Cannot lower lift. Lift@Pos:0");
                        return;
                    }
                    talon.set(ControlMode.Position, heightToCounts(setPoints[--pos]));
                } else if (b.getValue(c.b.getState()))
                {
                    if (pos == (setPoints.length - 1))
                    {
                        logMessage("Cannot raise lift. Lift@Pos:MAX");
                        return;
                    }
                    talon.set(ControlMode.Position, heightToCounts(setPoints[++pos]));
                }
            }

            @Override
            public void done()
            {
                talon.set(ControlMode.PercentOutput, 0);
                t.stop();
            }

            @Override
            public void interrupt() { done(); }

            @Override
            public String getName() { return "Teleop"; }
        };
    }
}
