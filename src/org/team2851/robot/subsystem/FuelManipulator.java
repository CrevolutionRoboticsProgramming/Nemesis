package org.team2851.robot.subsystem;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import org.jdom2.DataConversionException;
import org.team2851.robot.Robot;
import org.team2851.util.*;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

// Three 'sub' subsystems (Shooter, Agitator, Intake
public class FuelManipulator extends Subsystem
{
    public FuelManipulator() { super("FuelManipulator"); }

    ConfigFile configFile = ConfigFile.getInstance();
    CANTalon talonShooter, talonAgitator, talonIntake;
    Controller copilot;

    @Override
    public void init()
    {
        copilot = Robot.copilot;
        try {
            PID pid = configFile.getPid("Shooter PID");
            talonShooter = configFile.getCANTalon("shooter");
            talonAgitator = configFile.getCANTalon("agitator");
            talonIntake = configFile.getCANTalon("intake");

            talonShooter.setControlMode(CANTalon.TalonControlMode.Speed.getValue());
            talonShooter.setPID(pid.getP(), pid.getI(), pid.getD());
            talonShooter.configNominalOutputVoltage(0, 0);
            talonShooter.configPeakOutputVoltage(12, -12);
        } catch (ElementNotFoundException e) {
            Logger.printerr("[FuelManipulator] Talons not properly configured\n[FuelManipulator] Status: Disabled");
            isEnabled = false;
        } catch (DataConversionException e) {
            Logger.printerr("[FuelManipulator] Talons not properly configured\n[FuelManipulator] Status: Disabled");
            isEnabled = false;
        }
    }

    @Override
    public Command getDefaultCommand()
    {
        return new Command() {
            @Override
            public boolean isFinished() { return false; }

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
                return "Idle";
            }
        };
    }

    @Override
    public Command getTeleopCommand() {
        return new Command() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {

            }

            /*
             *  Control Scheme:
             *      LeftTrigger: Agitate (Forward)
             *      RightTrigger: Shooting (Non-analog)
             *      LeftButton: Outtake
             *      RightButton: Intake
             *      Select: Agitate (Reverse)
             */
            @Override
            public void update()
            {
                if (copilot.rightTrigger.getValue() > 0.1) {
                    talonShooter.setControlMode(CANTalon.TalonControlMode.Speed.getValue());
                    talonShooter.set(3000);
                } else {
                    talonShooter.setControlMode(0);
                    talonShooter.set(0);
                }

                if (copilot.leftTrigger.getValue() > 0.1) talonAgitator.set(0.3);
                else talonAgitator.set(0);

                if (copilot.rightBumper.getState()) talonIntake.set(0.8);
                else if (copilot.leftBumper.getState()) talonIntake.set(-0.8);
                else talonIntake.set(0);
            }

            @Override
            public void done()
            {
                talonShooter.setControlMode(CANTalon.TalonControlMode.PercentVbus.getValue());
                talonShooter.set(0);
                talonAgitator.set(0);
                talonIntake.set(0);
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

    public Command shoot(int fuelCount)
    {
        return new Command() {
            boolean isFinished = false;
            double agitatorTime, endTime, timePerFuel = 2;
            int vel = 3500;

            @Override
            public boolean isFinished() {
                return isFinished;
            }

            @Override
            public void start()
            {
                agitatorTime = Timer.getFPGATimestamp() + 0.5;
                endTime = Timer.getFPGATimestamp() + timePerFuel * fuelCount;
            }

            @Override
            public void update() {
                if (Timer.getFPGATimestamp() < agitatorTime) talonShooter.set(vel);
                else if (Timer.getFPGATimestamp() < endTime) {
                    talonAgitator.set(0.3);
                    talonShooter.set(vel);
                } else {
                    isFinished = true;
                }
            }

            @Override
            public void done() {
                talonShooter.setControlMode(CANTalon.TalonControlMode.PercentVbus.getValue());
                talonShooter.set(0);
                talonAgitator.set(0);
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return "[FuelManipulator] Shoot";
            }
        };
    }
}
