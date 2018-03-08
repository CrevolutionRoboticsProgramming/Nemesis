package org.team2851.robot.auton;

import openrio.powerup.MatchData;
import org.team2851.robot.auton.action.*;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

public class Left extends Auton
{
    public Left() { super("LeftAuton"); }

    @Override
    protected void routine() throws AutonEndedException
    {
        MatchData.OwnedSide switchSide = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
        MatchData.OwnedSide scaleSide = MatchData.getOwnedSide(MatchData.GameFeature.SCALE);

        if (switchSide == MatchData.OwnedSide.LEFT)
        {
            runAction(new DriveDistance(8));
            runAction(new Wait(1));
            runAction(new TurnToAngle(-90));
            runAction(new MoveLift(0.8, false));
            runAction(new OuttakeCube());
            runAction(new MoveLift(0.2, true));
        }
        else if (scaleSide == MatchData.OwnedSide.LEFT)
        {
            runAction(new DriveDistance(15));
            runAction(new Wait(1));
            runAction(new TurnToAngle(-45));
            runAction(new MoveLift(4, false));
            runAction(new OuttakeCube());
            runAction(new MoveLift(2, true));
        }
        else
        {
            runAction(new DriveDistance(8));
        }
    }
}
