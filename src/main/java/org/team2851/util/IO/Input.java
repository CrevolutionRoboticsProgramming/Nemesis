package org.team2851.util.IO;

import org.team2851.util.LatchedBoolean;

public enum Input
{
    // Buttons
    A(1, Type.BUTTON), B(2, Type.BUTTON), X(3, Type.BUTTON), Y(4, Type.BUTTON), LEFT_TRIGGER(2, Type.AXIS, Type.BUTTON),
    RIGHT_TRIGGER(3, Type.AXIS, Type.BUTTON), LEFT_BUMPER(5, Type.BUTTON), RIGHT_BUMPER(6, Type.BUTTON), START(8, Type.BUTTON),
    SELECT(7, Type.BUTTON), LEFT_JOYSTICK(9, Type.BUTTON), RIGHT_JOYSTICK(10, Type.BUTTON), D_UP(0, Type.POV_UP),
    D_DOWN(0, Type.POV_DOWN), D_LEFT(0, Type.POV_LEFT), D_RIGHT(0, Type.POV_RIGHT),
    // Axis
    LEFT_X(0, Type.AXIS), LEFT_Y(1, Type.AXIS), RIGHT_X(4, Type.AXIS), RIGHT_Y(5, Type.AXIS);

    final Type type, out;
    final int id;
    final double thresh = -0.3;
    Mode mode = Mode.RAW;
    LatchedBoolean currentState = new LatchedBoolean();

    Input(int id, Type type)
    {
        this.id = id;
        this.type = type;
        this.out = type;
    }

    Input(int id, Type type, Type out)
    {
        this.id = id;
        this.type = type;
        this.out = out;
    }

    void setMode(Input.Mode mode) { this.mode = mode; }

    public enum Type
    {
        BUTTON, AXIS, POV_UP, POV_DOWN, POV_LEFT, POV_RIGHT
    }

    public enum Mode
    {
        RAW, TOGGLE, INVERTED
    }
}
