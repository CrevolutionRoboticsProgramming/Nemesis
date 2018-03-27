package org.team2851.robot.auton.action;

import org.team2851.robot.DriveTrain;
import org.team2851.util.auton.Action;

public class TurnToAngle implements Action
{
    private double angle;
    private DriveTrain driveTrain = DriveTrain.getInstance();
    private boolean isFinished = false;
    public enum Mode { GYRO, ENC }
    private Mode mode;

    public TurnToAngle(double angle, Mode mode)
    {
        this.angle = angle;
        this.mode = mode;
    }

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
    public void start()
    {
        if (mode == Mode.GYRO)
            driveTrain.setCommand(driveTrain.turnToAngle(angle));
        else if (mode == Mode.ENC)
            driveTrain.setCommand(driveTrain.turnToAngleEncoder(angle));
    }
}
