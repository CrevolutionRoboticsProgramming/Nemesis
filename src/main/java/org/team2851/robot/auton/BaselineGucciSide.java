package org.team2851.robot.auton;

import org.team2851.robot.auton.action.DriveDistance;
import org.team2851.robot.auton.action.Wait;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;
import org.team2851.util.auton.Side;

public class BaselineGucciSide extends Auton
{
    public BaselineGucciSide() { super("BaselineGucciSide"); }

    @Override
    protected void routine() throws AutonEndedException
    {
        runAction(new Wait(0));
        runAction(new DriveDistance(8));
    }
}
