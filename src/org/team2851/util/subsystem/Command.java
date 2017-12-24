package org.team2851.util.subsystem;

public interface Command
{
    /*
     *  Returns if the command has finished
     */
    boolean isFinished();

    /*
     *  Runs once before the first update
     */
    void start();

    /*
     *  Runs iteratively until command is finished
     */
    void update();

    /*
     *  Runs at completion of command
     */
    void done();

    /*
     *  Runs when the command is interrupted
     */
    void interrupt();

    String getName();
}
