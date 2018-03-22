package org.team2851.robot.auton.action;

import edu.wpi.first.wpilibj.DriverStation;
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

    @Override
    public void start() { _driveTrain.setCommand(_driveTrain.runMotionProfile(new File("/home/lvuser/motion/test.csv"), new File("/home/lvuser/motion/test.csv"))); }
}
