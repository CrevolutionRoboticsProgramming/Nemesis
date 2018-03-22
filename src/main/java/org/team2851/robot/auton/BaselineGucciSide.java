package org.team2851.robot.auton;

import org.team2851.robot.auton.action.MoveLift;
import org.team2851.robot.auton.action.TimedDrive;
import org.team2851.robot.auton.action.Wait;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

public class BaselineGucciSide extends Auton
{
    public BaselineGucciSide() { super("BaselineGucciSide"); }

    @Override
    protected void routine() throws AutonEndedException
    {
        runAction(new MoveLift(1, false));
        runAction(new Wait(1));
        runAction(new TimedDrive(4, 0.5));
    }
}
