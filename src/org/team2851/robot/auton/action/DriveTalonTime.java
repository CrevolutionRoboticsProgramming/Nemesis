package org.team2851.robot.auton.action;

import org.team2851.robot.subsystem.LoneTalon;
import org.team2851.util.auton.Action;

public class DriveTalonTime implements Action
{
    private LoneTalon talon = LoneTalon.getInstance();
    private boolean isFinished = false, hasSet = false;
    @Override
    public boolean isFinished() {
        return isFinished;
    }

    // Note: Make sure you only set the command once. This is a loop!
    @Override
    public void update() {
        if (!hasSet) {
            talon.setCommand(talon.driveTime(5, 1));
            hasSet = true;
        }
        if (!talon.isSubsystemActive()) isFinished = true;
    }

    @Override
    public void done() {
        System.out.println("DriveTalonTime complete. Setting Command Idle.");
        talon.setCommand(talon.getDefaultCommand());
    }

    @Override
    public void start() {
        System.out.println("DriveTalonTime Starting. Time: 5");
    }
}
