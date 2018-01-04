package org.team2851.robot.auton.action;

import edu.wpi.first.wpilibj.Timer;
import org.team2851.util.auton.Action;
import org.team2851.util.field.Path;

// abstract is temp (note: code still good but deleted dependencies for clean slate)
public abstract class FollowPath implements Action
{
    /*
    private Path path;
    private double distance, angle;
    private TankDrive tankDrive = TankDrive.getInstance();
    private boolean isRunning = true;
    private Timer timer = new Timer();

    public FollowPath(Path path) { this.path = path; }

    @Override
    public boolean isFinished() {
        return !isRunning;
    }

    @Override
    public void update()
    {
        if (!path.isMotionProfile())
        {
            tankDrive.setCommand(tankDrive.turnAngle(angle));
            while (tankDrive.isSubsystemActive());
            double _t = timer.get() + 0.5;
            while (timer.get() < _t); // Adds a half second delay
            tankDrive.setCommand(tankDrive.driveByDistance(distance));
            while (tankDrive.isSubsystemActive());
            isRunning = false;
        } else {
            tankDrive.setCommand(tankDrive.runMotionProfile(path.getMotionProfile()));
            while (tankDrive.isSubsystemActive());
            isRunning = false;
        }
    }

    @Override
    public void done() {
        System.out.println("Completed Path: \n  " + path.toString() + "\n   Time Elapsed: " + timer.get());
        timer.stop();
    }

    @Override
    public void start()
    {
        distance = path.getDistance();
        angle = path.getAngle();
        timer.start();
    }
    */
}
