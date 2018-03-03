package org.team2851.robot.auton.action;

import org.team2851.robot.DriveTrain;
import org.team2851.util.auton.Action;

public class TurnToAngle implements Action
{
    private double angle;
    private DriveTrain driveTrain = DriveTrain.getInstance();
    private boolean isFinished = false;

    public TurnToAngle(double angle) { this.angle = angle; }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void update() {
        if (!driveTrain.isSubsystemActive()) isFinished = true;
    }

    @Override
    public void done() {
    }

    @Override
    public void start() {
        driveTrain.setCommand(driveTrain.turnToAngle(angle));
    }
}
