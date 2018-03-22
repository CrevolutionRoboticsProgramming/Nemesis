package org.team2851.robot;

import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import jaci.pathfinder.Trajectory;
import org.jdom2.DataConversionException;
import org.team2851.util.*;
import org.team2851.util.motion.MotionProfile;
import org.team2851.util.motion.MotionProfileExecutor;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class DriveTrain extends Subsystem
{
    private static DriveTrain instance = new DriveTrain();
    private DriveTrain() { super("Subsystem [DriveTrain]"); }
    public static DriveTrain getInstance() { return instance; }

    // Controllers/Actuators
    private WPI_TalonSRX leftA, leftB, rightA, rightB;
    private ADXRS450_Gyro gyro;

    // Values
    private double driveWidth = 0.75;

    private File csv;
    private FileOutputStream fos;
    private boolean useFile = true;

    private static final double wheelDiameter = 0.5;

    @Override
    public void init()
    {
        csv = new File("/home/lvuser/logs/PositionVelocity.csv");
        try {
            fos = new FileOutputStream(csv);
            useFile = true;
        } catch (FileNotFoundException e) { }
        Preferences.getInstance().putBoolean("IsTank", false);
        Preferences.getInstance().putDouble("TurnMultA", 1);

        try {

            leftA = ConfigFile.getWPI_TalonSRX("talonLeftA");
            leftB = ConfigFile.getWPI_TalonSRX("talonLeftB");
            rightA = ConfigFile.getWPI_TalonSRX("talonRightA");
            rightB = ConfigFile.getWPI_TalonSRX("talonRightB");

            leftA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
            rightA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
            leftB.set(ControlMode.Follower, leftA.getDeviceID());
            rightB.set(ControlMode.Follower, rightA.getDeviceID());

            leftA.configNominalOutputForward(0, 0);
            leftA.configNominalOutputReverse(0, 0);
            leftA.configPeakOutputForward(1, 0);
            leftA.configPeakOutputReverse(-1, 0);
            leftA.setSelectedSensorPosition(0, 0, 0);

            rightA.configNominalOutputForward(0, 0);
            rightA.configNominalOutputReverse(0, 0);
            rightA.configPeakOutputForward(1, 0);
            rightA.configPeakOutputReverse(-1, 0);

            leftA.setSelectedSensorPosition(0, 0, 0);
            rightA.setSelectedSensorPosition(0 ,0, 0);

            leftA.setSensorPhase(true);
        } catch (ElementNotFoundException e) {
            Logger.printerr("Subsystem [DriveTrain]: Could not instantiate motor controllers!");
            isEnabled = false;
        }

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
            double turnMult, mult, deadband = 0.15;

            @Override
            public boolean isFinished() { return false; }

            @Override
            public void start()
            {
                reset();
                leftA.set(ControlMode.PercentOutput, 0);
                rightA.set(ControlMode.PercentOutput, 0);
                isTank = Preferences.getInstance().getBoolean("IsTank", false);
                turnMult = Preferences.getInstance().getDouble("TurnMultA", 0.75);

                drive.setDeadband(deadband);
            }

            @Override
            public void update()
            {
                leftA.setSafetyEnabled(false);
                leftB.setSafetyEnabled(false);
                rightA.setSafetyEnabled(false);
                rightB.setSafetyEnabled(false);
                drive.setSafetyEnabled(false);
                mult = (c.leftBumper.getState()) ? 0.5 : 1;
                turnMult = (c.leftBumper.getState()) ? 0.4 : 0.75;

                if (isTank) drive.tankDrive(c.leftY.getValue(), c.rightY.getValue());
                // Quickturn is enabled if the throttle value is below the set deadband (Allows for zero point turns)
                else drive.curvatureDrive(c.leftY.getValue() * mult, c.rightX.getValue() * -turnMult, true);
            }

            @Override
            public void done() {
                reset();
                drive.stopMotor();
            }

            @Override
            public void interrupt() { done(); }
            @Override
            public String getName() { return "Teleop"; }
        };
    }

    public Command driveTime(double time, double leftPower, double rightPower)
    {
        return new Command() {
            Timer t = new Timer();
            @Override
            public boolean isFinished() {
                return t.get() > time;
            }

            @Override
            public void start() {
                t.start();
            }

            @Override
            public void update() {
                leftA.set(ControlMode.PercentOutput, leftPower);
                rightA.set(ControlMode.PercentOutput, rightPower);
            }

            @Override
            public void done() {
                reset();
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return "DriveTime[" + time + "]";
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

    public Command runMotionProfile(File left, File right)
    {
        return new Command()
        {
            MotionProfileExecutor leftExecutor, rightExecutor;
            @Override
            public boolean isFinished()
            {
                SetValueMotionProfile setValue;
                try
                {
                    setValue = leftExecutor.getSetValue();
                } catch (NullPointerException e) {
                    DriverStation.reportWarning("LeftExecutor setValue is null", false);
                    return false;
                }
               return setValue == SetValueMotionProfile.Hold;
            }

            @Override
            public void start()
            {
                reset();
                leftExecutor = new MotionProfileExecutor(left, leftA);
                rightExecutor = new MotionProfileExecutor(right, rightA);

                leftExecutor.reset();
                rightExecutor.reset();

                leftExecutor.start();
                rightExecutor.start();
            }

            @Override
            public void update()
            {
                leftExecutor.update();
                rightExecutor.update();

                leftA.set(ControlMode.MotionProfile, leftExecutor.getSetValue().value);
                rightA.set(ControlMode.MotionProfile, rightExecutor.getSetValue().value);
            }

            @Override
            public void done()
            {
                leftExecutor.reset();
                rightExecutor.reset();
                reset();
            }

            @Override
            public void interrupt()
            {
                DriverStation.reportWarning("Motion Profile was ended prematurely", false);
                done();
            }

            @Override
            public String getName()
            {
                return "Run Motion Profile [left: " + left.getAbsolutePath() + " | right: " + right.getAbsolutePath() + "]";
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
}
