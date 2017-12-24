package org.team2851.util;

import org.team2851.util.Controller;

public class RobotConstants
{
    private static RobotConstants sInstance = new RobotConstants();

    private RobotConstants()
    {
        driveController = new Controller(0);
        driveController.leftY = new Controller.Axis(Controller.AxisID.LEFT_Y, Controller.AxisMode.Raw, driveController.joystick);
        driveController.rightY = new Controller.Axis(Controller.AxisID.RIGHT_Y, Controller.AxisMode.Inverted, driveController.joystick);
        driveController.rightX = new Controller.Axis(Controller.AxisID.RIGHT_X, Controller.AxisMode.Inverted, driveController.joystick);
        driveController.b = new Controller.Button(Controller.ButtonID.B, Controller.ButtonMode.Raw, driveController.joystick);
        driveController.a = new Controller.Button(Controller.ButtonID.A, Controller.ButtonMode.Raw, driveController.joystick);

    }

    public static RobotConstants getInstance()
    {
        return sInstance;
    }

    // Controllers (Also instantiate buttons and axis
    public Controller driveController = new Controller(0),
            operatorController = new Controller(1);

    // Dimensions
    public final double ROBOT_WIDTH = 34;
    public final double ROBOT_LENGTH = 40;

    // Robot Specs
    public final int DRIVE_ENC_CPR = 0;

    // PID Constants
    public final int SHOOTER_RPM = 1000;

    // Motion Profile
    public final double MAX_ACCELERATION = 0; // ft / sec / sec
    public final double MAX_VELOCITY = 0; // ft / sec

    // Motor Controller Addresses
    public final int TALON_LEFT_FRONT_PORT = 14;
    public final int TALON_LEFT_REAR_PORT = 1;
    public final int TALON_RIGHT_FRONT_PORT = 27;
    public final int TALON_RIGHT_REAR_PORT = 28;
    public final int TALON_TEST_PORT = 0;
    public final int TALON_SHOOTER = 0;
    public final int TALON_AGITATOR = 0;

    // Sensor Ports
    public final int ENC_LEFT_DRIVE_PORT_A = 0;
    public final int ENC_LEFT_DRIVE_PORT_B = 1;

    // Constants (Misc.)
    public final boolean IS_CHILD_SAFETY_ENGAGED = false;

    public final double AGITATOR_POWER = 0.3;
    public final String configFilePath = "/home/lvuser/config/";

    // This section is NOT for constants
    public double currentFieldAngle = 0;
    public double currentFieldPoint;
}
