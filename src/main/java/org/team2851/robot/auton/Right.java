package org.team2851.robot.auton;

import openrio.powerup.MatchData;
import org.team2851.robot.auton.action.*;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

public class Right extends Auton
{
    public Right() { super("Right + Switch"); }

    @Override
    protected void routine() throws AutonEndedException
    {
        MatchData.OwnedSide side = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);

        runAction(new MoveLift(1, false));
        runAction(new Wait(0.5));
        if (side == MatchData.OwnedSide.RIGHT)
        {
            runAction(new TimedDrive(1.8, 0.5, 0.5));
            runAction(new Wait(1));
            runAction(new OuttakeCube(0.7));
        } else {
            runAction(new TimedDrive(3, 0.5));
        }
    }
}
