package org.team2851.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
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

    @Override
    public void init() {
        try {
            talonA = ConfigFile.getTalonSRX("IntakeA");
            talonB = ConfigFile.getTalonSRX("IntakeB");
        } catch (ElementNotFoundException e) {
            isEnabled = false;
            logError("Intake could not initialize talons. Disabling...");
            return;
        }

        controller = Robot.pilot;
    }

    @Override
    public Command getDefaultCommand() {
        return new DefaultCommand();
    }

    @Override
    public Command getTeleopCommand() {
        return new Command()
        {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {
                talonA.set(ControlMode.PercentOutput, 0);
                talonA.set(ControlMode.PercentOutput, 0);
            }

            @Override
            public void update() {
                if (controller.leftTrigger.getValue() < -0.3)
                {
                    talonA.set(ControlMode.PercentOutput, 1);
                    talonB.set(ControlMode.PercentOutput, -1);
                } else if (controller.rightTrigger.getValue() < -0.3)
                {
                    talonA.set(ControlMode.PercentOutput, -1);
                    talonB.set(ControlMode.PercentOutput, 1);
                } else {
                    talonA.set(ControlMode.PercentOutput, 0);
                    talonB.set(ControlMode.PercentOutput, 0);
                }
            }

            @Override
            public void done() {
                talonA.set(ControlMode.PercentOutput, 0);
                talonB.set(ControlMode.PercentOutput, 0);
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
}
