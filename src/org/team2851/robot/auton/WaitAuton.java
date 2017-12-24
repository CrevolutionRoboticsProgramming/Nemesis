package org.team2851.robot.auton;


import org.team2851.robot.auton.action.Wait;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonEndedException;

public class WaitAuton extends Auton
{
    public WaitAuton() { super("Wait Auton"); }
    @Override
    protected void routine() throws AutonEndedException {
        runAction(new Wait(5));
    }
}
