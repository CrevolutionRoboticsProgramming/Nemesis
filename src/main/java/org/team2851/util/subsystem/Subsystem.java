package org.team2851.util.subsystem;

import org.team2851.util.Logger;

public abstract class Subsystem extends Thread
{
    // Fields
    private Thread mThread;
    private String mName;
    protected boolean isAlive = false, hasInit = false, isEnabled = true; // Include is alive in any nested while loops
    public static boolean teleopEnabled = true; // Set to false if the controllers were not properly configured
    private Command command = getDefaultCommand();
    protected boolean useDefaultAlways = false;

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

    /**
     * Sets the current Command of the subsystem. Will interrupt current Command.
     * @param command The new command
     */
    public synchronized void setCommand(Command command)
    {
        if (this.command != null && !this.command.isFinished()) this.command.interrupt();

        hasInit = false;
        this.command = command;
        logMessage("Setting command to " + command.getName());
    }

    private synchronized void runCommand()
    {
        if (command != null) {
            if (!teleopEnabled && command.getName().equals("Teleop")) {
                logError("Teleop is disabled");
                return;
            }

            if (isEnabled) {
                if (!hasInit) {
                    command.start();
                    hasInit = true;
                    try {
                        Thread.sleep(5, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!command.isFinished()) {
                    command.update();
                } else {
                    command.done();
                    command = null;
                }
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
                Thread.sleep(5,0);
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
        return !(command == null || command.isFinished());
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
