package org.team2851.robot.auton;

import org.team2851.robot.auton.action.*;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

public class LoneAuton extends Auton
{
    private double time;

    public LoneAuton(double time)
    {
        super("Lone Auton");
        this.time = time;
    }

    @Override
    protected void routine() throws AutonEndedException {
        runAction(new Wait(time));
        runAction(new DriveTalonTime());
    }
}
