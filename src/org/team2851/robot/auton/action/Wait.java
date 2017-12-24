package org.team2851.robot.auton.action;

import edu.wpi.first.wpilibj.Timer;
import org.team2851.util.auton.Action;

public class Wait implements Action
{
    boolean isFinished = false;
    double endTime, time;

    public Wait(double time) { this.time = time; }

    @Override
    public boolean isFinished() { return isFinished; }

    @Override
    public void update() { if (endTime < Timer.getFPGATimestamp()) isFinished = true; }

    @Override
    public void done() { }

    @Override
    public void start() { endTime = Timer.getFPGATimestamp() + time; }
}
