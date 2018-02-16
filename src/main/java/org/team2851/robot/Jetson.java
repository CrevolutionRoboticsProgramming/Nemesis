package org.team2851.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Jetson
{
    private NetworkTableInstance networkTableInstance;
    private NetworkTable networkTable;

    public Jetson()
    {
        networkTableInstance = NetworkTableInstance.getDefault();
        networkTable = networkTableInstance.getTable("Jetson");
    }

    double getDeltaAngle()
    {
        return networkTable.getEntry("deltaAngle").getDouble(0);
    }

    boolean isOnline()
    {
        return networkTable.getEntry("isOnline").getBoolean(false);
    }
}
