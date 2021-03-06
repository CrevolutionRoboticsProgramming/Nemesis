package org.team2851.util.auton;

import org.team2851.util.Logger;

public class AutonExecutor
{
    private Auton mAuton;
    private Thread mThread = null;

    public void setAuton(Auton auton)
    {
        mAuton = auton;
    }

    public void start()
    {
        if (mThread == null)
        {
            mThread = new Thread()
            {
                @Override
                public void run()
                {
                    if (mAuton != null)
                        mAuton.run();
                }
            };
            mThread.start();
            Logger.println("Starting ");
        }
    }

    public void stop()
    {
        if (mAuton != null && mAuton.isAlive)
            mAuton.stop();

        mThread = null;
    }
}
