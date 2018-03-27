package org.team2851.robot.auton.action;

import org.team2851.robot.DriveTrain;
import org.team2851.util.auton.Action;

import java.io.File;

public class RunMotionProfile implements Action
{
    private DriveTrain _driveTrain = DriveTrain.getInstance();

    @Override
    public boolean isFinished() { return !_driveTrain.isSubsystemActive(); }

    @Override
    public void update() { }
    @Override
    public void done() { }

    // TODO: Motion Profile Arc vs Motion Profile Mode (Heading Compensation)
    @Override
    public void start() { _driveTrain.setCommand(_driveTrain.runMotionProfile(new File("/home/lvuser/motion/Left_Switch_right_detailed.csv"), new File("/home/lvuser/motion/Left_Switch_left_detailed.csv"))); }
}
