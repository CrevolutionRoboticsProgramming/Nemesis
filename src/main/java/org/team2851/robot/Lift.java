package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
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
        } catch (ElementNotFoundException e) {
            isEnabled = false;
            logError("Could not find talon element. Disabling subsystem.");
        }
    }

    @Override
    public Command getDefaultCommand() {
        return new DefaultCommand();
    }

    @Override
    public Command getTeleopCommand() {
        return new Command()
        {
            // a : up
            // b : down
            // temp (time) : 0.5 seconds
            Controller c = Robot.copilot;
            LatchedBoolean a = new LatchedBoolean();
            LatchedBoolean b = new LatchedBoolean();
            Timer t = new Timer();

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {

            }

            @Override
            public void update()
            {
                if (a.getValue(c.a.getState()))
                {
                    t.start();
                    while (t.get() < 0.5) talon.set(ControlMode.PercentOutput, 0.5);
                    t.stop();
                    t.reset();
                } else if (b.getValue(c.b.getState()))
                {
                    t.start();
                    while (t.get() < 0.5) talon.set(ControlMode.PercentOutput, -0.5);
                    t.stop();
                    t.reset();
                }
            }

            @Override
            public void done() {
                talon.set(ControlMode.PercentOutput, 0);
                t.stop();
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return "Teleop";
            }
        };
    }
}
