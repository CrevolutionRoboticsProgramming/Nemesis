package org.team2851.robot.auton;

import openrio.powerup.MatchData;
import org.team2851.robot.auton.action.*;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

/**
 * Auton goes first for the scale, then the switch, and if neither are available, goes baseline
 */
public class Left extends Auton
{
    public Left() { super("Left"); }

    @Override
    protected void routine() throws AutonEndedException
    {
        MatchData.OwnedSide switchSide = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
        MatchData.OwnedSide scaleSide = MatchData.getOwnedSide(MatchData.GameFeature.SCALE);

        runAction(new MoveLift(0.8, false));
        runAction(new Wait(0.1));
        if (scaleSide == MatchData.OwnedSide.LEFT) // Scale
        {
            runAction(new MoveLift(0.5, true));
//          20 and patial
//            runAction(new DriveDistance(20));
//            runAction(new Wait(0.1));
//            runAction(new TurnToAngle(-45, TurnToAngle.Mode.ENC));
//            runAction(new MoveLift(3.5, false));
//            runAction(new OuttakeCube(1));
            runAction(new DriveDistance(23));
            runAction(new Wait(0.1));
            runAction(new TurnToAngle(-45, TurnToAngle.Mode.ENC));
            runAction(new MoveLift(3.5, false));
            runAction(new TimedDrive(0.5, 0.2, 0.2));
            runAction(new OuttakeCube(1));
        } else if (switchSide == MatchData.OwnedSide.LEFT) // Switch
        {
            runAction(new DriveDistance(12));
            runAction(new TurnToAngle(-90, TurnToAngle.Mode.ENC));
            runAction(new DriveDistance(3));
            runAction(new OuttakeCube(1));
        } else // Baseline
        {
            runAction(new DriveDistance(12));
        }
    }
}
