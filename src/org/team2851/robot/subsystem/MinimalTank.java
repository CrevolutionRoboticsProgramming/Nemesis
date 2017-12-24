package org.team2851.robot.subsystem;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.RobotDrive;
import org.jdom2.DataConversionException;
import org.team2851.util.ConfigFile;
import org.team2851.util.ElementNotFoundException;
import org.team2851.util.PID;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

public class MinimalTank extends Subsystem
{
    CANTalon leftA, leftB, rightA, rightB;
    RobotDrive robotDrive;
    public MinimalTank() { super("Minimal Tank"); }

    @Override
    public void init()
    {
        ConfigFile cf = ConfigFile.getInstance();
        try {
            leftA = cf.getCANTalon("leftA");
            leftB = cf.getCANTalon("leftB");
            rightA = cf.getCANTalon("rightA");
            rightB = cf.getCANTalon("rightB");

            leftA.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            leftA.setPosition(0);
            leftB.setControlMode(CANTalon.TalonControlMode.Follower.getValue());
            leftB.set(leftA.getDeviceID());
            rightA.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            rightA.setPosition(0);
            rightB.setControlMode(CANTalon.TalonControlMode.Follower.getValue());
            rightB.set(rightA.getDeviceID());
        } catch (ElementNotFoundException e) {
            System.out.println("[ERROR]: Talons could not be properly configured. Shutting down minimal tank.");
            isEnabled = false;
        }
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
                System.out.println("[Talon Position]: " + leftA.getPosition());
                System.out.println("[Talon Position]: " + rightA.getPosition());
            }

            @Override
            public void done() {

            }

            @Override
            public void interrupt() {

            }

            @Override
            public String getName() {
                return "[MINIMAL_TANK]: IDLE";
            }
        };
    }

    @Override
    public Command getTeleopCommand() {
        return new Command() {

            void configurePID(CANTalon talon, PID pid)
            {
                switch (pid.getControlType())
                {
                    case PID:
                        talon.setPID(pid.getP(), pid.getI(), pid.getD());
                        break;
                    case PI:
                        talon.setP(pid.getP());
                        talon.setI(pid.getI());
                        break;
                    case P:
                        talon.setP(pid.getP());
                        break;
                }
            }

            boolean isPID = false;
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start()
            {
                PID leftPID, rightPID;
                try {
                    leftPID = ConfigFile.getInstance().getPid("leftA");
                    rightPID = ConfigFile.getInstance().getPid("rightA");

                    configurePID(leftA, leftPID);
                    configurePID(rightA, rightPID);
                    isPID = true;
                } catch (ElementNotFoundException e) {
                    System.out.println("PID not properly configured");
                } catch (DataConversionException e) {
                    System.out.println("PID not properly configured");
                }
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
}
