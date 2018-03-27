package org.team2851.robot.auton;

import org.team2851.robot.auton.action.*;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

public class TestAuton extends Auton
{
    public TestAuton() { super("test"); }

    @Override
    protected void routine() throws AutonEndedException
    {
        runAction(new MoveLift(1, false));
        runAction(new Wait(0.5));
        runAction(new MoveLift(0.7, true));
        runAction(new Wait(0.5));
        runAction(new DriveDistance(20));
        runAction(new TurnToAngle(-45, TurnToAngle.Mode.GYRO));
        runAction(new MoveLift(4, false));
        runAction(new OuttakeCube(1));
    }
}
