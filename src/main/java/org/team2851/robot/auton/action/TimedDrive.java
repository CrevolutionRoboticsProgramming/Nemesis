package org.team2851.robot.auton.action;

import org.team2851.robot.DriveTrain;
import org.team2851.util.auton.Action;

public class TimedDrive implements Action
{
    DriveTrain dt = DriveTrain.getInstance();
    public final double time, leftPower, rightPower;

    public TimedDrive(double time, double power)
    {
        this.time = time;
        this.leftPower = power;
        this.rightPower = power;
    }

    public TimedDrive(double time, double leftPower, double rightPower)
    {
        this.time = time;
        this.leftPower = leftPower;
        this.rightPower = rightPower;
    }

    @Override
    public boolean isFinished() {
        return !dt.isSubsystemActive();
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        dt.setCommand(dt.driveTime(time, leftPower, -rightPower));
    }
}
