package org.team2851.util.auton;

/*
 *  Taken from team 251's 2017 robot
 */

public interface Action {
    boolean isFinished(); // Returns true if the action has completed
    void update(); // Runs iteratively until action is complete
    void done(); // Runs when the action is complete
    void start(); // Initialization method
}
