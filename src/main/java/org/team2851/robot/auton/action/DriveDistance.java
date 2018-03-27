package org.team2851.robot.auton.action;

import edu.wpi.first.wpilibj.DriverStation;
import org.team2851.robot.DriveTrain;
import org.team2851.util.auton.Action;

public class DriveDistance implements Action
{
    private final double distance;
    public DriveDistance(double distance) { this.distance = distance; }

    @Override
    public boolean isFinished()
    {
        return !DriveTrain.getInstance().isSubsystemActive();
    }

    @Override
    public void update()
    {

    }

    @Override
    public void done()
    {

    }

    @Override
    public void start()
    {
        DriveTrain dt = DriveTrain.getInstance();
        dt.setCommand(dt.driveDistance(distance));
    }
}
