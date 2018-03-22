package org.team2851.robot.auton;

import openrio.powerup.MatchData;
import org.team2851.robot.auton.action.MoveLift;
import org.team2851.robot.auton.action.OuttakeCube;
import org.team2851.robot.auton.action.TimedDrive;
import org.team2851.robot.auton.action.Wait;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

public class Center extends Auton
{
    public Center() { super("Center + Switch"); }
    @Override
    protected void routine() throws AutonEndedException
    {
        MatchData.OwnedSide side = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);

        if (side == MatchData.OwnedSide.LEFT)
        {
            runAction(new MoveLift(1, false));
            runAction(new Wait(0.5));
            runAction(new TimedDrive(0.9, 0.5, 0.7));
            runAction(new TimedDrive(0.7, 0.5, 0.5));
            runAction(new TimedDrive(0.45, 0.8, 0.3));
            runAction(new Wait(0.5));
            runAction(new OuttakeCube(0.9));
//            runAction(new TimedDrive(0.9, 0.7, 0.5));
        } else if (side == MatchData.OwnedSide.RIGHT) {
            runAction(new MoveLift(1, false));
            runAction(new Wait(0.5));
            runAction(new TimedDrive(0.9, 0.8, 0.5));
            runAction(new TimedDrive(0.2, 0.7, 0.5));
            runAction(new TimedDrive(0.5, 0.55, 0.5));
            runAction(new TimedDrive(0.4, 0, 0.5));
            runAction(new Wait(0.5));
            runAction(new OuttakeCube(0.9));
//            runAction(new TimedDrive(0.7, 0.5, 0.5));
//            runAction(new TimedDrive(0.45, 0.8, 0.3));
        }
    }
}
