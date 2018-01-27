package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import org.jdom2.DataConversionException;
import org.team2851.util.*;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

public class DriveTrain extends Subsystem
{
    private static DriveTrain instance = new DriveTrain();
    private DriveTrain() { super("Subsystem [DriveTrain]"); }
    public static DriveTrain getInstance() { return instance; }

    // Controllers/Actuators
    private WPI_TalonSRX leftA, leftB, rightA, rightB;
    private ADXRS450_Gyro gyro;

    // Values
    private double driveMult;

    @Override
    public void init()
    {
        Timer t = new Timer();
//        ConfigFile cf = ConfigFile.getInstance();
//        try {
//
//            leftA = cf.getWPI_TalonSRX("talonLeftA");
//            leftB = cf.getWPI_TalonSRX("talonLeftB");
//            rightA = cf.getWPI_TalonSRX("talonRightA");
//            rightB = cf.getWPI_TalonSRX("talonRightB");
//
//            leftA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
//            rightA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
//            leftB.set(ControlMode.Follower, leftA.getDeviceID());
//            rightB.set(ControlMode.Follower, rightA.getDeviceID());
//
//            leftA.configNominalOutputForward(0, 0);
//            leftA.configNominalOutputReverse(0, 0);
//            leftA.configPeakOutputForward(1, 0);
//            leftA.configPeakOutputReverse(-1, 0);
//            leftA.setSelectedSensorPosition(0, 0, 0);
//
//            rightA.configNominalOutputForward(0, 0);
//            rightA.configNominalOutputReverse(0, 0);
//            rightA.configPeakOutputForward(1, 0);
//            rightA.configPeakOutputReverse(-1, 0);
//
//            driveMult = cf.getDouble("driveMult");
//        } catch (ElementNotFoundException e) {
//            Logger.printerr("Subsystem [DriveTrain]: Could not instantiate motor controllers!");
//            isEnabled = false;
//        } catch (DataConversionException ee) {
//            logError("Could not convert driveMult");
//            driveMult = 1;
//        }

        gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS2);
        t.start();
        gyro.calibrate();
        gyro.reset();
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
            public void interrupt() { }
            @Override
            public String getName() { return "Default"; }
        };
    }

    @Override
    public Command getTeleopCommand() {
        return new Command() {
            //DifferentialDrive drive = new DifferentialDrive(leftA, rightA);
            Controller c = Robot.pilot;
            Timer t = new Timer();
            @Override
            public boolean isFinished() { return false; }

            @Override
            public void start() {
                //System.out.println(leftA.getDeviceID());
                t.start();
//                leftA.set(ControlMode.PercentOutput, 0);
//                rightA.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void update()
            {
//                drive.curvatureDrive(c.leftY.getValue() * driveMult, c.rightX.getValue() * driveMult, c.leftBumper.getState());
//                if (t.get() % .2 == 0) System.out.print("Left -> Out: " + leftA.getMotorOutputPercent() + "   Vel: " +
//                        leftA.getSelectedSensorVelocity(0) + "\nRight -> Out: " + rightA.getMotorOutputPercent() +
//                        "   Vel: " + rightA.getSelectedSensorVelocity(0));
                logMessage("Gyro Angle: " + gyro.getAngle());
            }

            @Override
            public void done() {
                t.stop();
                //drive.stopMotor();
            }

            @Override
            public void interrupt() { done(); }
            @Override
            public String getName() { return "Teleop"; }
        };
    }

    public Command driveDistance(double distance)
    {
        return new Command() {
            private int cruiseVel = velToCTREUnits(450), maxAcceleration = velToCTREUnits(450);
            private boolean isFinished = false;
            @Override
            public boolean isFinished() { return isFinished; }

            @Override
            public void start()
            {
                rightA.set(ControlMode.Follower, leftA.getDeviceID()); // Sets the right motors to follow the left
                leftA.configMotionCruiseVelocity(cruiseVel, 0);
                leftA.configMotionAcceleration(maxAcceleration, 0);
                leftA.setSelectedSensorPosition(0, 0, 0);
            }

            @Override
            public void update() {
                leftA.set(ControlMode.MotionMagic, distance * 4096);
                if (Math.abs(leftA.getClosedLoopError(0)) < 8192) isFinished = true;
            }

            @Override
            public void done() {
                leftA.set(0);
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return "DriveDistance(" + Double.toString(distance) + ")";
            }

            private int velToCTREUnits(int vel) { return vel / 600 * 4096; }
        };
    }

    public Command turnToAngle(double angle)
    {
        return new Command() {
            DifferentialDrive drive = new DifferentialDrive(leftA, rightA);
            PID pid;
            double error;

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start()
            {
                Preferences pref = Preferences.getInstance();
                gyro.reset();
                leftA.set(ControlMode.PercentOutput, 0);
                rightA.set(ControlMode.PercentOutput, 0);

                pid = new PID(pref.getDouble("TurnP", 0), pref.getDouble("TurnI", 0));
            }

            @Override
            public void update()
            {
                error = (Math.abs(gyro.getAngle()) > 360) ? gyro.getAngle() % 360 : gyro.getAngle();
                double out = pid.getOutput(error);
                drive.tankDrive(out, -out);
            }

            @Override
            public void done()
            {
                drive.stopMotor();
            }

            @Override
            public void interrupt()
            {
                logError("Could not finish turning to angle");
                done();
            }

            @Override
            public String getName() { return "TurnToAngle[" + angle + "]"; }
        };
    }
}
