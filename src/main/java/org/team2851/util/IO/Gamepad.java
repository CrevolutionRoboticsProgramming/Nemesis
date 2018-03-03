package org.team2851.util.IO;

import edu.wpi.first.wpilibj.Joystick;

public class Gamepad
{
    private Joystick j;

    /** @param id The joystick ID for the driver station */
    public Gamepad(int id)
    {
        j = new Joystick(id);
    }

    /**
     * Will return the state of the given input. If input is an axis (and is not the left or right triggers), it will return false always.
     * @param input The button/axis that you wish to get the state of
     * @return Varies depending on mode of input
     */
    public boolean getBoolState(Input input)
    {
        Input.Type in = input.type;
        Input.Type out = input.out;
        Input.Mode mode = input.mode;

        switch (in)
        {
            case AXIS:
                if (!(out == Input.Type.BUTTON)) return false;
            case BUTTON:
                if (mode == Input.Mode.RAW) return j.getRawButton(input.id);
                else if (mode == Input.Mode.INVERTED) return !j.getRawButton(input.id);
                else if (mode == Input.Mode.TOGGLE) return input.currentState.getValue(j.getRawButton(input.id));
                break;
            case POV_UP:
                if (mode == Input.Mode.RAW) return j.getPOV(input.id) == 0;
                else if (mode == Input.Mode.INVERTED) return !(j.getPOV(input.id) == 0);
                else if (mode == Input.Mode.TOGGLE) return input.currentState.getValue(j.getPOV(input.id) == 0);
                break;
            case POV_DOWN:
                if (mode == Input.Mode.RAW) return j.getPOV(input.id) == 180;
                else if (mode == Input.Mode.INVERTED) return !(j.getPOV(input.id) == 180);
                else if (mode == Input.Mode.TOGGLE) return input.currentState.getValue(j.getPOV(input.id) == 180);
                break;
            case POV_LEFT:
                if (mode == Input.Mode.RAW) return j.getPOV(input.id) == 90;
                else if (mode == Input.Mode.INVERTED) return !(j.getPOV(input.id) == 90);
                else if (mode == Input.Mode.TOGGLE) return input.currentState.getValue(j.getPOV(input.id) == 90);
                break;
            case POV_RIGHT:
                if (mode == Input.Mode.RAW) return j.getPOV(input.id) == 270;
                else if (mode == Input.Mode.INVERTED) return !(j.getPOV(input.id) == 270);
                else if (mode == Input.Mode.TOGGLE) return input.currentState.getValue(j.getPOV(input.id) == 270);
                break;
        }
        return false;
    }

    /**
     * Will return an analog state of a given input. If the input is a button, it will return 0.0 if not pressed and 1.0 if it is.
     * @param input The button/axis that you wish to get the value of.
     * @return Varies on the mode of the input
     */

    public double getDoubleValue(Input input)
    {
        Input.Type in = input.type;
        Input.Type out = input.out;
        Input.Mode mode = input.mode;

        switch (in)
        {
            case AXIS:
                if (mode == Input.Mode.INVERTED) return -j.getRawAxis(input.id);
                else if (out != Input.Type.BUTTON) return j.getRawAxis(input.id);
            case BUTTON:
                if (in == Input.Type.AXIS) return (j.getRawAxis(input.id) > input.thresh) ? 1 : 0;
                else if (mode == Input.Mode.RAW) return (j.getRawButton(input.id)) ? 1 : 0;
                else if (mode == Input.Mode.INVERTED) return (!j.getRawButton(input.id)) ? 1 : 0;
                else if (mode == Input.Mode.TOGGLE) return (input.currentState.getValue(j.getRawButton(input.id))) ? 1 : 0;
                break;
            case POV_UP:
                if (mode == Input.Mode.RAW) return (j.getPOV(input.id) == 0) ? 1 : 0;
                else if (mode == Input.Mode.INVERTED) return (!(j.getPOV(input.id) == 0)) ? 1 : 0;
                else if (mode == Input.Mode.TOGGLE) return (input.currentState.getValue(j.getPOV(input.id) == 0)) ? 1 : 0;
                break;
            case POV_DOWN:
                if (mode == Input.Mode.RAW) return (j.getPOV(input.id) == 180) ? 1 : 0;
                else if (mode == Input.Mode.INVERTED) return (!(j.getPOV(input.id) == 180)) ? 1 : 0;
                else if (mode == Input.Mode.TOGGLE) return (input.currentState.getValue(j.getPOV(input.id) == 180)) ? 1 : 0;
                break;
            case POV_LEFT:
                if (mode == Input.Mode.RAW) return (j.getPOV(input.id) == 90) ? 1 : 0;
                else if (mode == Input.Mode.INVERTED) return (!(j.getPOV(input.id) == 90)) ? 1 : 0;
                else if (mode == Input.Mode.TOGGLE) return (input.currentState.getValue(j.getPOV(input.id) == 90)) ? 1 : 0;
                break;
            case POV_RIGHT:
                if (mode == Input.Mode.RAW) return (j.getPOV(input.id) == 270) ? 1 : 0;
                else if (mode == Input.Mode.INVERTED) return (!(j.getPOV(input.id) == 270)) ? 1 : 0;
                else if (mode == Input.Mode.TOGGLE) return (input.currentState.getValue(j.getPOV(input.id) == 270)) ? 1 : 0;
                break;
        }
        return 0;
    }

    /**
     * Will change the mode of the given input. Options: RAW, INVERTED, TOGGLE
     * @param input The input you wish to modify
     * @param mode The mode you wish to set the input to
     */
    public void setInputMode(Input input, Input.Mode mode) { input.setMode(mode); }
}
