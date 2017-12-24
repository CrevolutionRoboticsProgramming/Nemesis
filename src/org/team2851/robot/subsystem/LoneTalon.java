package org.team2851.robot.subsystem;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import org.team2851.robot.Robot;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

public class LoneTalon extends Subsystem
{
    private CANTalon talon;
    private static LoneTalon sInstance = new LoneTalon();

    private LoneTalon() { super ("Lone Talon"); }

    public static LoneTalon getInstance() {
        return sInstance;
    }

    @Override
    public void init() {
        talon = new CANTalon(27);
        talon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
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
                talon.set(0);
                System.out.println("Idle");
            }

            @Override
            public void update() {
                talon.set(0);
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
        Joystick joy = new Joystick(0);
        return new Command() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {
                talon.set(0);
                talon.setControlMode(CANTalon.TalonControlMode.PercentVbus.getValue());

                talon.setPosition(0);
            }

            @Override
            public void update() {
                System.out.println(talon.getPosition());
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

    public Command driveTime(double time, double power)
    {
        return new Command() {
            private boolean isFinished = false;
            private double endTime;
            @Override
            public boolean isFinished() {
                return isFinished;
            }

            @Override
            public void start() {
                endTime = Timer.getFPGATimestamp() + time;
                System.out.println(endTime);
            }

            @Override
            public void update() {
                if (Timer.getFPGATimestamp() < endTime) talon.set(power);
                else isFinished = true;
            }

            @Override
            public void done() {
                System.out.println("Drive Command Complete");
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
}
