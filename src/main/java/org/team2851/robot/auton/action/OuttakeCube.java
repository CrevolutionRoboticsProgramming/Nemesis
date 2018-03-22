package org.team2851.robot.auton.action;

import edu.wpi.first.wpilibj.Timer;
import org.team2851.robot.Intake;
import org.team2851.util.Logger;
import org.team2851.util.auton.Action;

public class OuttakeCube implements Action
{
    Intake intake = Intake.getInstance();
    double power;

    public OuttakeCube(double power) { this.power = power; }
    @Override
    public boolean isFinished() { return intake.isSubsystemActive(); }

    @Override
    public void update()
    {

    }

    @Override
    public void done()
    {

    }

    @Override
    public void start()
    {
        Logger.println("Outtaking Cube");
        intake.setCommand(intake.manipulateCube(Intake.IntakeDirection.OUTTAKE, power));
    }
}
