package org.team2851.robot.auton.action;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import org.team2851.robot.DriveTrain;
import org.team2851.robot.Robot;
import org.team2851.util.auton.Action;
import org.team2851.util.motion.*;

public class FollowPath implements Action
{
    Waypoint[] waypoints;

    @Override
    public boolean isFinished() {
        return !DriveTrain.getInstance().isSubsystemActive();
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        DriveTrain.getInstance().setCommand(DriveTrain.getInstance().followTrajectory(waypoints));
    }

    public FollowPath(Waypoint[] waypoints)
    {
        this.waypoints = waypoints;
    }
}
