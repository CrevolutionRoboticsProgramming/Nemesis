package org.team2851.robot.auton.action;

import org.team2851.robot.Intake;
import org.team2851.util.Logger;
import org.team2851.util.auton.Action;

public class OuttakeCube implements Action
{
    @Override
    public boolean isFinished() { return true; }

    @Override
    public void update()
    {

    }

    @Override
    public void done()
    {
        Logger.println("Finished Outtaking Cube");
    }

    @Override
    public void start()
    {
        Logger.println("Outtaking Cube");
//        Intake.getInstance().setCommand(Intake.getInstance().outtakeCube());
    }
}
