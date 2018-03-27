package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import org.team2851.util.ConfigFile;
import org.team2851.util.Controller;
import org.team2851.util.ElementNotFoundException;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

public class Intake extends Subsystem
{
    private static Intake instance = new Intake();
    private Intake() { super("Intake"); }
    public static Intake getInstance() { return instance; }
    private TalonSRX talonLeft, talonRight;
    private Controller controller;

    public enum IntakeDirection { INTAKE, OUTTAKE }

    @Override
    public void init() {
        Preferences.getInstance().putBoolean("Single Controller", false);
        try {
            talonLeft = ConfigFile.getTalonSRX("IntakeA");
            talonRight = ConfigFile.getTalonSRX("IntakeB");
        } catch (ElementNotFoundException e) {
            isEnabled = false;
            logError("Intake could not initialize talons. Disabling...");
            return;
        }
        controller = Robot.copilot;
        talonLeft.configContinuousCurrentLimit(10, 0);
        talonRight.configContinuousCurrentLimit(10, 0);
    }

    @Override
    public Command getDefaultCommand() { return new DefaultCommand(); }

    @Override
    public Command getTeleopCommand()
    {
        return new Command()
        {
            double output;
            boolean singleController;

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start()
            {
                singleController = Preferences.getInstance().getBoolean("Single Controller", false);
                if (singleController) controller = Robot.pilot;
                else controller = Robot.copilot;
                talonLeft.set(ControlMode.PercentOutput, 0);
                talonRight.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void update()
            {
                if (controller.leftTrigger.getValue() < -0.3) output = 1;
                else if (controller.rightTrigger.getValue() < -0.3) output = -1;
                else if (controller.leftBumper.getState()) output = 0.5;
                else output = 0;

                talonLeft.set(ControlMode.PercentOutput, output);
                talonRight.set(ControlMode.PercentOutput, output);
            }

            @Override
            public void done() {
                talonLeft.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return "Teleop";
            }
        };
    }

    public Command manipulateCube(IntakeDirection direction, double power)
    {
        return new Command() {
            Timer t = new Timer();
            double powerA = 1 * power;

            @Override
            public boolean isFinished() {
                return t.get() > 2;
            }

            @Override

            public void start() {
                t.start();
                if (direction != IntakeDirection.INTAKE) powerA *= -1;
            }

            @Override
            public void update() {
                talonLeft.set(ControlMode.PercentOutput, powerA);
                talonRight.set(ControlMode.PercentOutput, powerA);
            }

            @Override
            public void done() {
                talonLeft.set(ControlMode.PercentOutput, 0);
                talonRight.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void interrupt() {
                done();
            }

            @Override
            public String getName() {
                return "Outtake Cube";
            }
        };
    }
}
