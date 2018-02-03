package org.team2851.robot.auton.action;

import org.team2851.robot.DriveTrain;
import org.team2851.robot.Robot;
import org.team2851.util.auton.Action;
import org.team2851.util.motion.*;

public class FollowPath implements Action
{
    private Path path;
    private DriveTrain driveTrain = DriveTrain.getInstance();
    private boolean isFinished = false;

    public FollowPath(Path path) { this.path = path; }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void update()
    {
        try {
            goToPoint(path.getNextPoint());
        } catch (PathCompleteException e) {
            isFinished = true;
            path.reset();
        }
    }

    @Override
    public void done() { }

    @Override
    public void start() { }

    private void goToPoint(Point2D point)
    {
        Point2D startPoint = Robot.getCurrentPoint();
        double distance = startPoint.getDistance(point);
        double angle = startPoint.getAngle(point);

        driveTrain.setCommand(driveTrain.turnToAngle(angle));
        while (driveTrain.isSubsystemActive());
        driveTrain.setCommand(driveTrain.driveDistance(distance));
        while (driveTrain.isSubsystemActive());
        Robot.setCurrentAngle(angle);
        Robot.setCurrentPoint(point);
    }
}
