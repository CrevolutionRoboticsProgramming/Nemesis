package org.team2851.robot.subsystem;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

public class Intake extends Subsystem
{
    private static Intake instance = new Intake();
    private Intake() { super("Intake"); }
    public static Intake getInstance() { return instance; }

    private TalonSRX leftRoller, rightRoller;
    private Solenoid hammerA, hammerB;

    @Override
    public void init()
    {
        hammerA = new Solenoid(0);
        hammerB = new Solenoid(1);

    }

    @Override
    public Command getDefaultCommand() {
        return new Command() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {

            }

            @Override
            public void update() {

            }

            @Override
            public void done() {

            }

            @Override
            public void interrupt() {

            }

            @Override
            public String getName() {
                return null;
            }
        };
    }

    @Override
    public Command getTeleopCommand() {
        Timer t = new Timer();
        return new Command() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {
                hammerA.set(true);
                t.start();
                while (t.get() < 1);
                t.stop();
                t.reset();
                hammerA.set(false);
                hammerB.set(true);
            }

            @Override
            public void update() {

            }

            @Override
            public void done() {
                hammerB.set(true);
                t.start();
                while (t.get() < 1);
                t.stop();
                hammerB.set(false);
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return null;
            }
        };
    }
}
