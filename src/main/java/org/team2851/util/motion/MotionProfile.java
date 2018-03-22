package org.team2851.util.motion;

/*
 * Point Format:
 * [Point Number] { position (rotations), velocity(rpm), duration(ms) }
 */

public interface MotionProfile
{
    int getNumberOfPoints();
    double[][] getPoints();
}
