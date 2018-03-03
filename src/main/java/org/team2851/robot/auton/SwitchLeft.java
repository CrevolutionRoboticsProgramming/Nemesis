package org.team2851.robot.auton;

import edu.wpi.first.wpilibj.DriverStation;
import openrio.powerup.MatchData;
import org.team2851.robot.auton.action.DriveDistance;
import org.team2851.robot.auton.action.OuttakeCube;
import org.team2851.robot.auton.action.TurnToAngle;
import org.team2851.robot.auton.action.Wait;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

public class SwitchLeft extends Auton
{
    public SwitchLeft() {
        super("Switch Left [" + DriverStation.getInstance().getAlliance().toString() + ", " + MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR).toString() + "]");
    }

    @Override
    protected void routine() throws AutonEndedException
    {
        MatchData.OwnedSide side = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
        switch (side)
        {
            default:
            case UNKNOWN:
            {
                runAction(new DriveDistance(3.5));
                break;
            }

            case LEFT:
            {
                runAction(new DriveDistance(3.5));
                runAction(new Wait(1));
                runAction(new TurnToAngle(-90));
                runAction(new OuttakeCube());
                break;
            }

            case RIGHT:
            {
                return;
            }
        }
    }
}
