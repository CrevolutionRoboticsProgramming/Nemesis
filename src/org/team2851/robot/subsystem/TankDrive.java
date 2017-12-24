package org.team2851.robot.subsystem;

import com.ctre.CANTalon;
import com.ctre.PigeonImu;
import edu.wpi.first.wpilibj.*;
import org.team2851.robot.Robot;
import org.team2851.util.*;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

public class TankDrive extends Subsystem
{
    private static TankDrive sInstance = new TankDrive();
    private CANTalon talonLeftA, talonLeftB, talonRightA, talonRightB;

    private TankDrive() { super("Tank Drive"); }

    public static TankDrive getInstance() { return sInstance; }

    @Override
    public void init()
    {
        ConfigFile configFile = ConfigFile.getInstance();
        try {
            talonLeftA = configFile.getCANTalon("leftA");
            talonLeftB = configFile.getCANTalon("leftB");
            talonLeftB.setControlMode(CANTalon.TalonControlMode.Follower.getValue());
            talonLeftB.set(talonLeftA.getDeviceID());

            talonRightA = configFile.getCANTalon("rightA");
            talonRightB = configFile.getCANTalon("rightB");
            talonRightB.setControlMode(CANTalon.TalonControlMode.Follower.getValue());
            talonRightB.set(talonRightA.getDeviceID());
            //talonRightA.setInverted(true);
        } catch (ElementNotFoundException e) {
            System.err.println("[TankDrive] Talons were not properly configured!\n[TankDrive] Status: Disabled");
            Logger.printerr("[TankDrive] Talons were not properly configured!\n[TankDrive] Status: Disabled");
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
            public void start()
            {
                Logger.println("[TankDrive] Entering idle mode...");
                talonLeftA.set(0);
                talonRightA.set(0);
            }

            @Override
            public void update() { }

            @Override
            public void done() { }

            @Override
            public void interrupt() { }

            @Override
            public String getName() { return "[TankDrive] Idle"; }
        };
    }

    @Override
    public Command getTeleopCommand()
    {
        return new Command()
        {
            Controller pilot = Robot.pilot;
            int maxRPM = 1500;
            boolean isNorm = true;
            RobotDrive robotDrive = new RobotDrive(talonLeftA, talonRightA);
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start()
            {
                if (!isNorm) {
                    talonLeftA.setControlMode(CANTalon.TalonControlMode.Speed.getValue());
                    talonRightB.setControlMode(CANTalon.TalonControlMode.Speed.getValue());
                    // Quad encoder should be connected directly to the talon using the encoder breakout board
                    talonLeftA.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
                    talonRightA.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);

                    talonLeftA.configNominalOutputVoltage(0.1, -0.1);
                    talonRightA.configNominalOutputVoltage(0.1, -0.1);
                    talonLeftA.configPeakOutputVoltage(11.5, -11.5);
                    talonRightA.configPeakOutputVoltage(11.5, -11.5);

                    talonLeftA.setAllowableClosedLoopErr(100);
                    talonRightA.setAllowableClosedLoopErr(100);
                }
            }

            @Override
            public void update()
            {
                if (!isNorm) {
                    int _leftVel = (int) (Math.pow(pilot.leftY.getValue(), 2) * maxRPM);
                    int _rightVel = (int) (Math.pow(pilot.rightY.getValue(), 2) * maxRPM);

                    talonLeftA.set(_leftVel);
                    talonRightA.set(_rightVel);
                } else {
                    robotDrive.arcadeDrive(-pilot.leftY.getValue(), pilot.rightX.getValue());
                }
            }

            @Override
            public void done() {

            }

            @Override
            public void interrupt() {

            }

            @Override
            public String getName() { return "Teleop"; }
        };
    }

    public Command driveByTime(double left, double right, double time)
    {
        return new Command()
        {
            Timer timer = new Timer();
            @Override
            public boolean isFinished() { return timer.get() > time; }

            @Override
            public void start()
            {
                timer.start();
            }

            @Override
            public void update()
            {
                if (timer.get() < time)
                {
                    talonLeftA.set(left);
                    talonLeftB.set(right);
                }
            }

            @Override
            public void done()
            {
                talonLeftA.set(0);
                talonRightA.set(0);
            }

            @Override
            public void interrupt()
            {
                talonLeftA.set(0);
                talonRightA.set(0);
            }

            @Override
            public String getName() { return "[TankDrive] DriveByTime"; }
        };
    }

    public Command driveByDistance(double distance) // Distance should be provided in meters
    {
        return new Command()
        {
            boolean isFinished = false;
            double kP, kI;
            double targetPos;
            int cpr = 1024;
            PID pid = new PID(kP, kI);

            @Override
            public boolean isFinished() {
                return isFinished;
            }

            @Override
            public void start() {
                talonLeftA.setControlMode(CANTalon.TalonControlMode.Speed.getValue());
                talonRightB.setControlMode(CANTalon.TalonControlMode.Speed.getValue());
                // Quad encoder should be connected directly to the talon using the encoder breakout board
                talonLeftA.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
                talonRightA.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);

                talonLeftA.configNominalOutputVoltage(0.1, -0.1);
                talonRightA.configNominalOutputVoltage(0.1, -0.1);
                talonLeftA.configPeakOutputVoltage(11.5, -11.5);
                talonRightA.configPeakOutputVoltage(11.5, -11.5);

                talonLeftA.setAllowableClosedLoopErr(100);
                talonRightA.setAllowableClosedLoopErr(100);

                talonRightA.setPosition(0);
                talonLeftA.setPosition(0);

                targetPos = distance * cpr;
            }

            // TODO: Investigate heading correction with gyro input and using percentVBus control mode (
            @Override
            public void update()
            {
                double outVel = pid.getOutput((int)(targetPos - talonLeftA.getPosition()));
                talonLeftA.set(outVel);
                talonRightA.set(outVel);
            }

            @Override
            public void done()
            {
                talonLeftA.set(0);
                talonRightA.set(0);
            }

            @Override
            public void interrupt()
            {
                done();
            }

            @Override
            public String getName() {
                return "DriveByDistance";
            }
        };
    }

    // TODO: Implement motion profile stream and execution
    public Command runMotionProfile(double[][] motionProfile)
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

            }

            @Override
            public void done() {

            }

            @Override
            public void interrupt() {

            }

            @Override
            public String getName() { return "runMotionProfile"; }
        };
    }

    public Command turnAngle(double angle) // + angle is a counterclockwise rotation
    {
        // Currently using a proportional controller (Possibly adding integral and derivative if possible
        return new Command()
        {
            double p, i, d;
            PigeonImu imu = new PigeonImu(0);
            double allowedError = 5, currentAngle, pTerm, iTerm; // 5 degrees allowed error
            double[] ypr;
            boolean isFinished = false;
            @Override
            public boolean isFinished() {
                return isFinished;
            }

            @Override
            public void start()
            {
                Preferences prefs = Preferences.getInstance();
                talonLeftA.setControlMode(CANTalon.TalonControlMode.PercentVbus.getValue());
                talonRightA.setControlMode(CANTalon.TalonControlMode.PercentVbus.getValue());
                ypr = new double[3];
                imu.SetYaw(0);
                imu.GetYawPitchRoll(ypr);
                currentAngle = ypr[0];

                p = prefs.getDouble("Turn_P", 0);
                i = prefs.getDouble("Turn_I", 0);
            }

            @Override
            public void update()
            {
                double currentError = angle - currentAngle;
                if (Math.abs(currentError) < allowedError)
                {
                    isFinished = true;
                    return;
                }
                pTerm = currentError * p;
                iTerm += currentError * i;

                if (iTerm > 0.5) iTerm = 0.5;
                else if (iTerm < -0.5) iTerm = -0.5;

                double output = pTerm + iTerm;
                if (output > 1) output = 1;
                else if (output < -1) output = -1;
                // A positive output indicates a counterclockwise rotation
                if (output > 0)
                {
                    talonLeftA.set(-output);
                    talonRightA.set(output);
                } else if (output < 0) {
                    talonLeftA.set(output);
                    talonRightA.set(-output);
                } else {
                    isFinished = true;
                }
            }

            @Override
            public void done() {
                talonLeftA.set(0);
                talonRightA.set(0);
            }

            @Override
            public void interrupt() {
                talonLeftA.set(0);
                talonRightA.set(0);
            }

            @Override
            public String getName() { return "turnAngle"; }
        };
    }
}
