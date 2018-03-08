package org.team2851.robot.auton.action;

import org.team2851.robot.Lift;
import org.team2851.util.auton.Action;

public class MoveLift implements Action
{
    double time;
    boolean goingDown;
    Lift lift = Lift.getInstance();

    public MoveLift(double time, boolean goingDown)
    {
        this.time = time;
        this.goingDown = false;
    }

    @Override
    public boolean isFinished() {
        return !lift.isSubsystemActive();
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        lift.setCommand(lift.liftForTime(time, goingDown));
    }
}
