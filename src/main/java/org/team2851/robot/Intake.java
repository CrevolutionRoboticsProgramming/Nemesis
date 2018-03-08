package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
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
    private TalonSRX talonA, talonB;
    private Controller controller;

    public enum IntakeDirection { INTAKE, OUTTAKE }

    @Override
    public void init() {
        try {
            talonA = ConfigFile.getTalonSRX("IntakeA");
            talonB = ConfigFile.getTalonSRX("IntakeB");
            talonB.set(ControlMode.Follower, talonA.getDeviceID());
        } catch (ElementNotFoundException e) {
            isEnabled = false;
            logError("Intake could not initialize talons. Disabling...");
            return;
        }

        controller = Robot.copilot;
    }

    @Override
    public Command getDefaultCommand() { return new DefaultCommand(); }

    @Override
    public Command getTeleopCommand()
    {
        return new Command()
        {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {
                talonA.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void update()
            {
                if (controller.leftTrigger.getValue() < -0.3) talonA.set(ControlMode.PercentOutput, 1); // Outtake
                else if (controller.rightTrigger.getValue() < -0.3) talonA.set(ControlMode.PercentOutput, -1); // Intake
                else talonA.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void done() {
                talonA.set(ControlMode.PercentOutput, 0);
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

    public Command manipulateCube(IntakeDirection direction)
    {
        return new Command() {
            Timer t = new Timer();
            double power = 1;

            @Override
            public boolean isFinished() {
                return t.get() > 1;
            }

            @Override
            public void start() {
                t.start();
                if (direction == IntakeDirection.INTAKE) power *= -1;
            }

            @Override
            public void update() {
                talonA.set(ControlMode.PercentOutput, power);
            }

            @Override
            public void done() {
                talonA.set(ControlMode.PercentOutput, 0);
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
