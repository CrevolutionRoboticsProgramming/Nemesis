package org.team2851.util.motion;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import java.io.File;
import java.util.TimerTask;

public class MotionProfileExecutor
{
    public enum State { DISABLED, LOADING, RUNNING, COMPLETE }

    private MotionProfile _profile;
    private TalonSRX _talon;
    private boolean _start = false;
    private java.util.Timer _timer = new java.util.Timer();

    private SetValueMotionProfile _setValue = SetValueMotionProfile.Disable;
    private MotionProfileStatus _status = new MotionProfileStatus();
    private State _state = State.DISABLED;

    public MotionProfileExecutor(MotionProfile profile, TalonSRX talon)
    {
        _profile = profile;
        _talon = talon;
        _timer.scheduleAtFixedRate(new PeriodicUpdate(), 0, 5);
    }

    public MotionProfileExecutor(File file, TalonSRX talon)
    {
        System.out.println("Starting to create profile");
        _profile = new MotionProfile()
        {
            Trajectory trajectory = Pathfinder.readFromCSV(file);

            @Override
            public int getNumberOfPoints()
            {
                return trajectory.segments.length;
            }

            @Override
            public double[][] getPoints()
            {
                double[][] points = new double[trajectory.segments.length][4];
                for (int i = 0; i < getNumberOfPoints(); i++)
                {
                    points[i][0] = trajectory.segments[i].position;
                    points[i][1] = trajectory.segments[i].velocity * (1 / (0.5 * Math.PI)) * 60;
                    points[i][2] = trajectory.segments[i].dt;
//                    points[i][4] = trajectory.segments[i].heading;
                }
                return points;
            }
        };
        System.out.println("Finished");

        _talon = talon;
        _timer.scheduleAtFixedRate(new PeriodicUpdate(), 0, 5);
    }

    private class PeriodicUpdate extends TimerTask
    {
        @Override
        public void run() {
            _talon.processMotionProfileBuffer(); }
    }

    public void update()
    {
        switch (_state)
        {
            case DISABLED:
                if (_start)
                {
                    _state = State.LOADING;
                    _start = false;
                    break;
                }
                reset();
                break;
            case LOADING:
                _setValue = SetValueMotionProfile.Disable;
                _state = State.RUNNING;
                try { fillBuffer(); } catch (InvalidMotionProfileException e) { _state = State.DISABLED; }
                break;
            case RUNNING:
                if (_status.btmBufferCnt > 5)
                {
                    _setValue = SetValueMotionProfile.Enable;
                    _state = State.COMPLETE;
                }
                break;
            case COMPLETE:
                if (_status.isLast && _status.activePointValid)
                {
                    _setValue = SetValueMotionProfile.Hold;
                    _state = State.DISABLED;
                }
                break;
        }

        _talon.getMotionProfileStatus(_status);
    }

    private void fillBuffer() throws InvalidMotionProfileException
    {
        TrajectoryPoint point = new TrajectoryPoint();
        double[][] _points = _profile.getPoints();

        if (_profile.getNumberOfPoints() > 2048)
        {
            DriverStation.reportError("Could not load Motion Profile - Point Quantity Exceeds 2048", true);
            throw new InvalidMotionProfileException();
        }

        for (int i = 0; i < _profile.getNumberOfPoints(); i++)
        {
            double position = _points[i][0];
            double velocity = _points[i][1];
            int time = (int)_points[i][2];

            point.position = position * 4096;
            point.velocity = velocity * 4096 / 600;
            point.timeDur = getTrajectoryDuration(time);
            point.profileSlotSelect0 = 0;
            point.profileSlotSelect1 = 0;
            point.headingDeg = 0;
            point.zeroPos = i == 0;
            point.isLastPoint = (i + 1) == _profile.getNumberOfPoints();

            _talon.pushMotionProfileTrajectory(point);
        }
    }

    private TrajectoryPoint.TrajectoryDuration getTrajectoryDuration(int durationMs)
    {
        TrajectoryPoint.TrajectoryDuration duration = TrajectoryPoint.TrajectoryDuration.Trajectory_Duration_0ms;
        duration = duration.valueOf(durationMs);
        if (duration.value != durationMs) DriverStation.reportError("Trajectory Duration not supported", false);
        return duration;
    }

    public void reset()
    {
        _talon.clearMotionProfileTrajectories();
        _setValue = SetValueMotionProfile.Disable;
        _state = State.DISABLED;
        _start = false;
    }

    public void start()
    {
        _start = true;
    }

    public SetValueMotionProfile getSetValue() { return _setValue; }

    private class InvalidMotionProfileException extends Exception {}
}
