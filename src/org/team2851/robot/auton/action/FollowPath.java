package org.team2851.robot.auton.action;

import org.team2851.util.auton.Action;
import org.team2851.util.motion.Path;
import org.team2851.util.motion.Point2D;

public class FollowPath implements Action
{
    private Path path;
    private Point2D currentPoint;

    public FollowPath(Path path) { this.path = path; }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {

    }

    @Override
    public void start() {

    }
}
