package org.team2851.robot.auton;

import org.team2851.robot.auton.action.RunMotionProfile;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

public class TestAuton extends Auton
{
    public TestAuton() { super("test"); }

    @Override
    protected void routine() throws AutonEndedException
    {
        runAction(new RunMotionProfile());
    }
}
