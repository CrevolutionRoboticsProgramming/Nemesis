package org.team2851.util.subsystem;

import org.team2851.util.Logger;

public abstract class Subsystem extends Thread
{
    // Fields
    private Thread mThread;
    private String mName = "Unknown Subsystem";
    protected boolean isAlive = false, hasInit = false, isEnabled = true; // Include is alive in any nested while loops
    public static boolean teleopEnabled = true; // Set to false if the controllers were not properly configured
    private Command command = getDefaultCommand();

    public String toString()
    {
        return mName;
    }

    // Abstract Functions
    public abstract void init(); // Runs when the subsystem starts
    public abstract Command getDefaultCommand();
    public abstract Command getTeleopCommand();

    // Functions
    public Subsystem(String name)
    {
        mName = name;
    }

    public synchronized void setCommand(Command command)
    {
        if (!this.command.isFinished())
            this.command.interrupt();

        hasInit = false;
        this.command = command;
        logMessage("Setting command to " + command.getName());
    }

    private synchronized void runCommand()
    {
        if (!teleopEnabled && command.getName().equals("Teleop"))
        {
            logError("Teleop is disabled");
            return;
        }

        if (isEnabled)
        {
            if (!hasInit) {
                command.start();
                hasInit = true;
            }

            if (!command.isFinished()) {
                command.update();
            } else {
                command.done();
            }
        }
    }

    @Override
    public void run()
    {
        while (isAlive)
        {
            runCommand();
            try {
                Thread.sleep(0,1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start()
    {
        if (mThread == null)
        {
            mThread = new Thread(this, mName);
            init();
            isAlive = true;
            mThread.start();
            logMessage("Starting...");
        }
    }

    public boolean isSubsystemActive()
    {
        return !command.isFinished();
    }

    public void halt()
    {
        if (!command.isFinished())
            command.interrupt();
        isAlive = false;
        Logger.println("Halting Subsystem: " + mName);
    }

    protected void logError(String message) { Logger.printerr("Subsystem [" + mName + "]: " + message); }
    protected void logMessage(String message) { Logger.println("Subsystem [" + mName + "]: " + message); }

    public class DefaultCommand implements Command
    {
        @Override
        public boolean isFinished() { return false; }
        @Override
        public void start() { }
        @Override
        public void update() { }
        @Override
        public void done() { }
        @Override
        public void interrupt() { }
        @Override
        public String getName() { return "Default"; }
    }
}
