package org.team2851.robot.auton.action;

import edu.wpi.first.wpilibj.Timer;
import org.team2851.robot.DriveTrain;
import org.team2851.util.auton.Action;


public class DriveDistance implements Action
{
    private DriveTrain driveTrain = DriveTrain.getInstance();
    private double distance;
    private boolean isFinished = false;

    public DriveDistance(double distance) { this.distance = distance; }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void update()
    {
        if (!driveTrain.isSubsystemActive()) isFinished = false;
    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        driveTrain.setCommand(driveTrain.driveDistance(distance));
        Timer t = new Timer();
        t.start();
        while (t.get() < 0.05);
    }
}
