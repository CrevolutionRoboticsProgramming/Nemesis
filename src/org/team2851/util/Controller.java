package org.team2851.util;

import edu.wpi.first.wpilibj.Joystick;

public class Controller
{
    public Joystick joystick;
    public Button a, b, x, y, leftBumper, rightBumper, start, select, leftJoy, rightJoy;
    public Axis leftX, leftY, rightX, rightY, leftTrigger, rightTrigger;
    public Controller(int id)
    {
        joystick = new Joystick(id);
    }

    public enum ButtonMode { Toggle, Raw }
    public enum ButtonID
    {
        A(1), B(2), X(3), Y(4), LEFT_BUMPER(5), RIGHT_BUMPER(6), START(8), SELECT(7), LEFT_JOY(9), RIGHT_JOY(10);

        int value;
        ButtonID(int value)
        {
            this.value = value;
        }
    }

    public enum AxisMode { Raw, Inverted, Button }
    public enum AxisID
    {
        LEFT_X(0), LEFT_Y(1), RIGHT_X(4), RIGHT_Y(5), LEFT_TRIGGER(2), RIGHT_TRIGGER(3);

        public int value;
        AxisID(int value)
        {
            this.value = value;
        }
    }

    public static double shapeInputSquare(double input)
    {
        return Math.pow(input, 2);
    }

    public static class Button
    {
        private ButtonID buttonID;
        private ButtonMode buttonMode;
        private boolean isToggled, isPressed;
        private Joystick joystick;

        public Button(ButtonID buttonID, ButtonMode buttonMode, Joystick joystick)
        {
            this.buttonID = buttonID;
            this.buttonMode = buttonMode;
            isToggled = false;
            isPressed = false;
            this.joystick = joystick;
        }

        public boolean getState()
        {
            switch (buttonMode)
            {
                case Raw:
                    return joystick.getRawButton(buttonID.value);
                case Toggle:
                    if (joystick.getRawButton(buttonID.value) && !isPressed)
                    {
                        isPressed = true;
                        isToggled ^= true;
                    } else if (!joystick.getRawButton(buttonID.value) && isPressed)
                    {
                        isPressed = false;
                    }
                    return isToggled;
                default:
                    return false;
            }
        }
    }

    public static class Axis
    {
        // Note: In button mode, returns a 1 for true and a 0 for false
        private AxisID axisID;
        private AxisMode axisMode;
        double buttonValue = 0;
        private Joystick joystick;
        private double deadband = 0.11;

        public Axis(AxisID axisID, AxisMode axisMode, Joystick joystick)
        {
            this.axisID = axisID;
            this.axisMode = axisMode;
            this.joystick = joystick;
        }

        public Axis(AxisID axisID, AxisMode axisMode, double buttonValue, Joystick joystick)
        {
            this.axisID = axisID;
            this.axisMode = axisMode;
            this.buttonValue = buttonValue;
            this.joystick = joystick;
        }

        public double getValue()
        {
            switch (axisMode)
            {
                case Raw:
                    return (Math.abs(joystick.getRawAxis(axisID.value)) < deadband) ? 0 : joystick.getRawAxis(axisID.value);
                case Inverted:
                    return (Math.abs(joystick.getRawAxis(axisID.value)) < deadband) ? 0 : -joystick.getRawAxis(axisID.value);
                case Button:
                    if (buttonValue == 0)
                    {
                        System.err.println("Axis Button value not set!");
                        return 0;
                    }

                    if (joystick.getRawAxis(axisID.value) > buttonValue)
                        return 1;
                    else
                        return 0;
                default:
                    System.err.println("Axis mode not selected!");
                    return 0;
            }
        }
    }
}
