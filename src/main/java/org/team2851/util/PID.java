package org.team2851.util;

public class PID
{
    private double p, i, d;
    private double iState = 0;
    public enum ControlType { P, PI, PID; }
    private ControlType controlType;

    public PID(double p, double i, double d)
    {
        this.p = p;
        this.i = i;
        this.d = d;
        controlType = ControlType.PID;
    }

    public PID(double p, double i)
    {
        this.p = p;
        this.i = i;
        controlType = ControlType.PI;
    }

    public PID(double p)
    {
        this.p = p;
        controlType = ControlType.P;
    }

    public double getOutput(double error)
    {
        double output = 0;
        switch (controlType)
        {
            case P: {
                output = error * p;
                break;
            }
            case PI: {
//                System.out.println(error);
                iState += error;
                output = (error * p) + (iState * i);
                break;
            }
            case PID: {
                // TODO: Implement PID mode (HIGH)
                break;
            }
        }

        return output;
    }

    public double getP() { return p; }
    public double getI() { return i; }
    public double getD() { return d; }

    public ControlType getControlType() { return controlType; }
}
