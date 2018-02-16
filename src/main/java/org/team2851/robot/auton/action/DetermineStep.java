package org.team2851.robot.auton.action;

import org.team2851.util.Logger;
import org.team2851.util.auton.Action;

public class DetermineStep implements Action
{
    // Averages the time interval between 100 cycles
    private long average;
    int cycles = 0;

    public static long timeStep = 0;
    @Override
    public boolean isFinished()
    {
        return cycles > 100;
    }

    @Override
    public void update()
    {
        average += System.currentTimeMillis();
        cycles++;
    }

    @Override
    public void done()
    {
        timeStep = average / cycles;
        Logger.println("Calculated auton ts = " + timeStep);
    }

    @Override
    public void start()
    {

    }
}
