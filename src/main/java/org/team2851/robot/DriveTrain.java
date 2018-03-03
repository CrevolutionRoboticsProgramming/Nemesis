package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import org.jdom2.DataConversionException;
import org.team2851.robot.auton.action.DetermineStep;
import org.team2851.util.*;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DriveTrain extends Subsystem
{
    private static DriveTrain instance = new DriveTrain();
    private DriveTrain() { super("Subsystem [DriveTrain]"); }
    public static DriveTrain getInstance() { return instance; }

    // Controllers/Actuators
    private WPI_TalonSRX leftA, leftB, rightA, rightB;
    private ADXRS450_Gyro gyro;

    // Trajectory
    private double timeStep = DetermineStep.timeStep;
    private double maxVel = 1.7, maxAccel = 2.0, maxJerk = 60; // (m/s, m/s/s, m/s/s/s)
    Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.003, 1.5, 2.0, 60.0);
    Trajectory trajectory;
    TankModifier modifier;
    EncoderFollower left, right;

    // Values
    private double driveMult;
    private double driveWidth = 0.75;

    private File csv;
    private FileOutputStream fos;
    private boolean useFile = false;

    @Override
    public void init()
    {
        csv = new File("/home/lvuser/logs/PositionVelocity.csv");
        try {
            fos = new FileOutputStream(csv);
            useFile = true;
        } catch (FileNotFoundException e) { }
        Preferences.getInstance().putBoolean("IsTank", false);

//        try {
//
//            leftA = ConfigFile.getWPI_TalonSRX("talonLeftA");
//            leftB = ConfigFile.getWPI_TalonSRX("talonLeftB");
//            rightA = ConfigFile.getWPI_TalonSRX("talonRightA");
//            rightB = ConfigFile.getWPI_TalonSRX("talonRightB");
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
//            leftA.setSelectedSensorPosition(0, 0, 0);
//            rightA.setSelectedSensorPosition(0 ,0, 0);
//
//            leftA.setSensorPhase(true);
//
////            rightA.setInverted(true);
////            rightB.setInverted(true);
//
//            driveMult = ConfigFile.getDouble("driveMult");
//        } catch (ElementNotFoundException e) {
//            Logger.printerr("Subsystem [DriveTrain]: Could not instantiate motor controllers!");
//            isEnabled = false;
//        } catch (DataConversionException ee) {
//            logError("Could not convert driveMult");
//            driveMult = 1;
//        }
        leftA = new WPI_TalonSRX(28);
        leftB = new WPI_TalonSRX(23);
        rightA = new WPI_TalonSRX(19);
        rightB = new WPI_TalonSRX(2);

        leftB.set(ControlMode.Follower, leftA.getDeviceID());
        rightB.set(ControlMode.Follower, rightA.getDeviceID());

//        gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS2);
//        gyro.calibrate();
//        gyro.reset();
    }

    @Override
    public Command getDefaultCommand() {
        return new DefaultCommand();
    }

    @Override
    public Command getTeleopCommand() {
        return new Command() {
            DifferentialDrive drive = new DifferentialDrive(leftA, rightA);
            Controller c = Robot.pilot;
            Timer t = new Timer();
            boolean isTank = false;
            double lastTime = 0;

            @Override
            public boolean isFinished() { return false; }

            @Override
            public void start()
            {
                t.start();
                leftA.set(ControlMode.PercentOutput, 0);
                rightA.set(ControlMode.PercentOutput, 0);
                isTank = Preferences.getInstance().getBoolean("IsTank", false);

                leftA.configOpenloopRamp(0, 0);
                rightA.configOpenloopRamp(0, 0);
            }

            @Override
            public void update()
            {
                leftA.setSafetyEnabled(false);
                rightA.setSafetyEnabled(false);
                leftB.set(ControlMode.Follower, leftA.getDeviceID());
                rightB.set(ControlMode.Follower, rightA.getDeviceID());
                if (!isTank) drive.curvatureDrive(c.leftY.getValue(), c.rightX.getValue() * -0.7, true);
                else drive.tankDrive(c.leftY.getValue() * driveMult, c.rightY.getValue());
                if (lastTime > 0)
                {
                    updateCSV(Timer.getFPGATimestamp() - lastTime, leftA.getSelectedSensorPosition(0), leftA.getSelectedSensorVelocity(0));
                    lastTime = Timer.getFPGATimestamp();
                } else {
                    lastTime = Timer.getFPGATimestamp();
                }
            }

            @Override
            public void done() {
                t.stop();
                drive.stopMotor();
            }

            @Override
            public void interrupt() { done(); }
            @Override
            public String getName() { return "Teleop"; }
        };
    }

    /*
     *  distance -> Distance in meters
     */
    public Command driveDistance(double distance)
    {
        return new Command() {
            boolean isFinished = false;
            @Override
            public boolean isFinished() { return isFinished; }

            @Override
            public void start()
            {
                reset();
                leftA.config_kP(0, Preferences.getInstance().getDouble("P", 0), 0);
//                leftA.config_kI(0, Preferences.getInstance().getDouble("I", 0), 0);
                leftA.configClosedloopRamp(1, 0);
                leftA.configOpenloopRamp(1, 0);

                rightA.config_kP(0, Preferences.getInstance().getDouble("p", 0), 0);
//                rightA.config_kI(0, Preferences.getInstance().getDouble("I", 0), 0);
                rightA.configClosedloopRamp(1, 0);
                rightA.configOpenloopRamp(1, 0);

                logMessage("Initial Encoder Pos: " + leftA.getSelectedSensorPosition(0));
                logMessage("Target Position: " + (distance * 1440) / (0.15 * Math.PI));
            }

            @Override
            public void update()
            {
                leftA.set(ControlMode.Position, (distance * 1440) / (0.15 * Math.PI));
                rightA.set(ControlMode.PercentOutput, -leftA.getMotorOutputPercent());
                logMessage("Error: " + leftA.getClosedLoopError(0));
                Preferences.getInstance().putDouble("Error", leftA.getClosedLoopError(0));
                isFinished = Math.abs(leftA.getClosedLoopError(0)) < 200;
            }

            @Override
            public void done() {
                leftA.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return "DriveDistance(" + Double.toString(distance) + ")";
            }
        };
    }

    public Command turnToAngle(double angle)
    {
        return new Command() {
            boolean isFinished;
            DifferentialDrive drive = new DifferentialDrive(leftA, rightA);
            PID pid;
            double error, turnMult;

            @Override
            public boolean isFinished() {
                return isFinished;
            }
            Timer t;

            @Override
            public void start()
            {
                t = new Timer();
                gyro.reset();
                leftA.set(ControlMode.PercentOutput, 0);
                rightA.set(ControlMode.PercentOutput, 0);

//                pid = new PID(Preferences.getInstance().getDouble("P", 0), Preferences.getInstance().getDouble("I", 0));
                try {
                    pid = ConfigFile.getPid("Turn PID");
                } catch (ElementNotFoundException e) {
                    e.printStackTrace();
                } catch (DataConversionException e) {
                    e.printStackTrace();
                }
                try {
                    turnMult = ConfigFile.getDouble("turnMult");
                } catch (ElementNotFoundException e) {
                    logError("Could not instantiate PID and/or turnMult. Ending turn.");
                    isFinished = true;
                } catch (DataConversionException e) {
                    logError("Could not instantiate PID and/or turnMult. Ending turn.");
                    isFinished = true;
                }
                t.start();
            }

            int i = 0, x = 0;
            double timeStamp = 0, lastTime = 0;

            @Override
            public void update()
            {
                timeStamp += t.get() - lastTime;
                lastTime = t.get();
                error = (Math.abs(gyro.getAngle()) > 360) ? gyro.getAngle() % 360 : gyro.getAngle() - angle;
                double out = pid.getOutput(error);
                out *= turnMult;
                drive.tankDrive(-out, out);
                if (Math.abs(error) < 5)
                {
                    i++;
                } else {
                    i = 0;
                }

                if (i > 100) isFinished = true;
                x++;
                Preferences.getInstance().putDouble("Error", error);
            }
            boolean hasDone = false;
            @Override
            public void done()
            {
                if (!hasDone) timeStamp /= x; hasDone = true;
                System.out.println(timeStamp);
                logMessage("Done");
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

    // TODO: Figure htf this works!?

    public Command followTrajectory(Waypoint[] waypoints)
    {
        return new Command() {
            @Override
            public boolean isFinished() {
                if (left != null && right != null) {
                    //System.out.println(left.isFinished() && right.isFinished());
                    return left.isFinished() && right.isFinished();
                }
                else
                    return false;
            }

            @Override
            public void start() {
                PID pid = new PID(Preferences.getInstance().getDouble("P", 0), Preferences.getInstance().getDouble("I", 0));
                trajectory = Pathfinder.generate(waypoints, config);
                modifier = new TankModifier(trajectory);
                modifier.modify(driveWidth);
                left = new EncoderFollower(modifier.getLeftTrajectory());
                right = new EncoderFollower(modifier.getRightTrajectory());
                left.configureEncoder(leftA.getSelectedSensorPosition(0), 360, 0.13);
                left.configurePIDVA(pid.getP(), 0, 0, 1 / maxVel, 0);
                right.configureEncoder(rightA.getSelectedSensorPosition(0), 360, 0.13);
                right.configurePIDVA(pid.getP(), 0, 0, 1 / maxVel, 0);
                gyro.reset();
            }

            @Override
            public void update()
            {
                double l = left.calculate(leftA.getSelectedSensorPosition(0));
                double r = right.calculate(rightA.getSelectedSensorPosition(0));

                double gyro_head = gyro.getAngle();
                double head = Pathfinder.r2d(left.getHeading());

                double diff = Pathfinder.boundHalfDegrees(head - gyro_head);
                double turn = 0.75 * (-1/80) * diff;

                System.out.println("Left: " + l + "Right: " + rightA.getSelectedSensorPosition(0));

                leftA.set(ControlMode.PercentOutput, l + turn);
                rightA.set(ControlMode.PercentOutput, -(r + turn));
            }

            @Override
            public void done() {
                leftA.set(0);
                rightA.set(0);
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return "Follow Path";
            }
        };
    }

    private void reset()
    {
        leftA.setSelectedSensorPosition(0, 0, 0);
        rightA.setSelectedSensorPosition(0, 0, 0);

        leftA.set(ControlMode.PercentOutput, 0);
        rightA.set(ControlMode.PercentOutput, 0);
    }

    private void updateCSV(double dt, double pos, double vel)
    {
        if (!useFile) return;
        String str = dt + "," + pos + "," + vel + ",\n";
        char[] chars = str.toCharArray();
        byte[] out = new byte[chars.length];
        for (int i = 0; i < out.length; i++) out[i] = (byte)chars[i];
        try {
            fos.write(out);
        } catch (IOException e) {
            logError("Could not write to csv.");
        }
    }
}
