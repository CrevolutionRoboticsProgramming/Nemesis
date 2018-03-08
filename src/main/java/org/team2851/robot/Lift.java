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

    public enum LiftPosition
    {
        INTAKE(0.0, 0),
        MOVING(10.5, 1),
        Switch(20.0, 2),
        SCALE_LOW(40.0, 3),
        SCALE_HIGH(60.0, 4),
        CLIMB(45.0, 5);

        public final double height;
        public final int index;
        LiftPosition(double height, int index) { this.height = height; this.index = index; }
    }

    private TalonSRX talon;
    private final double circumference = 2.5;
    private LiftPosition endAutonPos;

    private int heightToCounts(double height) { return (int)(height * circumference * 4096); }

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

        useDefaultAlways = true;
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
            // a : up
            // b : down
            // temp (time) : 0.5 seconds
            private Controller c = Robot.copilot;
            private LatchedBoolean a = new LatchedBoolean();
            private LatchedBoolean b = new LatchedBoolean();
            private Timer t = new Timer();
            private int pos = 0;
            private double power;

            @Override
            public boolean isFinished() { return false; }

            @Override
            public void start()
            {
                if (endAutonPos != null) pos = endAutonPos.index;
                power = Preferences.getInstance().getDouble("Power", 0);
            }

            @Override
            public void update()
            {
                if (c.a.getState()) talon.set(ControlMode.PercentOutput, .9);
                else if (c.b.getState()) talon.set(ControlMode.PercentOutput, -.5);
                else if (c.x.getState()) talon.set(ControlMode.PercentOutput, 0.15); // Stalls motor (Keeps lift up)
                else talon.set(ControlMode.PercentOutput, 0);
//                if (c.leftTrigger.getValue() > 0.3)
//                {
//                    if (c.a.getState()) talon.set(ControlMode.PercentOutput, 0.1);
//                    else if (c.b.getState()) talon.set(ControlMode.PercentOutput, -0.1);
//                    return;
//                } else if (a.getValue(c.a.getState()))
//                {
//                    if (pos == 0)
//                    {
//                        logError("Cannot lower lift. Lift@Pos:0");
//                        return;
//                    }
//                    pos--;
//                } else if (b.getValue(c.b.getState()))
//                {
//                    if (pos == 5)
//                    {
//                        logError("Cannot raise lift. Lift@Pos:MAX");
//                        return;
//                    }
//                    pos++;
//                }
//
//                switch (pos)
//                {
//                    case 0:
//                        talon.set(ControlMode.Position, heightToCounts(LiftPosition.INTAKE.height));
//                        break;
//                    case 1:
//                        talon.set(ControlMode.Position, heightToCounts(LiftPosition.MOVING.height));
//                        break;
//                    case 2:
//                        talon.set(ControlMode.Position, heightToCounts(LiftPosition.Switch.height));
//                        break;
//                    case 3:
//                        talon.set(ControlMode.Position, heightToCounts(LiftPosition.SCALE_LOW.height));
//                        break;
//                    case 4:
//                        talon.set(ControlMode.Position, heightToCounts(LiftPosition.SCALE_HIGH.height));
//                        break;
//                    case 5:
//                        talon.set(ControlMode.Position, heightToCounts(LiftPosition.CLIMB.height));
//                        break;
//                    default:
//                        logError("Set to invalid height.");
//                }
            }

            @Override
            public void done()
            {
//                talon.set(ControlMode.PercentOutput, 0);
//                t.stop();
                currentPower = 0;
            }

            @Override
            public void interrupt() { done(); }

            @Override
            public String getName() { return "Teleop"; }
        };
    }

    public Command setPosition(LiftPosition position)
    {
        return new Command()
        {
            boolean isFinished = false;
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {

            }

            @Override
            public void update() {
                talon.set(ControlMode.Position, position.height);
            }

            @Override
            public void done() {
                talon.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return "SetPosition[" + position.toString() + "]";
            }
        };
    }

    public Command liftForTime(double time, boolean goingDown)
    {
        return new Command() {
            Timer t = new Timer();
            double power;
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start()
            {
                t.start();
                power = (goingDown) ? -0.6 : 0.8;
            }

            @Override
            public void update()
            {
                if (t.get() < time) talon.set(ControlMode.PercentOutput, power);
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
