package org.team2851.robot.subsystem;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team2851.util.ConfigFile;
import org.team2851.util.Controller;
import org.team2851.util.ElementNotFoundException;
import org.team2851.util.subsystem.Command;
import org.team2851.util.subsystem.Subsystem;

public class ControllerTest extends Subsystem
{
    Controller controller;

    public ControllerTest() {
        super("Controller");
    }

    @Override
    public void init()
    {
        try {
            controller = ConfigFile.getController("pilot.xml");
        } catch (ElementNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command getDefaultCommand() {
        return new Command() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {

            }

            @Override
            public void update() {

            }

            @Override
            public void done() {

            }

            @Override
            public void interrupt() {

            }

            @Override
            public String getName() {
                return "[Controller] idle";
            }
        };
    }

    @Override
    public Command getTeleopCommand() {
        return new Command() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void start() {

            }

            @Override
            public void update() {
                try {
                    SmartDashboard.putBoolean("A", controller.a.getState());
                    SmartDashboard.putBoolean("B", controller.b.getState());
                    SmartDashboard.putBoolean("X", controller.x.getState());
                    SmartDashboard.putBoolean("Y", controller.y.getState());
                    SmartDashboard.putBoolean("Start", controller.start.getState());
                    SmartDashboard.putBoolean("Select", controller.select.getState());
                    SmartDashboard.putBoolean("LeftJoy", controller.leftJoy.getState());
                    SmartDashboard.putBoolean("RightJoy", controller.rightJoy.getState());
                    SmartDashboard.putBoolean("LeftBumper", controller.leftBumper.getState());
                    SmartDashboard.putBoolean("RightBumper", controller.rightBumper.getState());

                    SmartDashboard.putNumber("LeftX", controller.leftX.getValue());
                    SmartDashboard.putNumber("Lefty", controller.leftY.getValue());
                    SmartDashboard.putNumber("RightX", controller.rightX.getValue());
                    SmartDashboard.putNumber("RightY", controller.rightY.getValue());
                    SmartDashboard.putNumber("LeftTrigger", controller.leftTrigger.getValue());
                    SmartDashboard.putNumber("RightTrigger", controller.rightTrigger.getValue());
                } catch (NullPointerException e) {}

            }

            @Override
            public void done() {

            }

            @Override
            public void interrupt() {

            }

            @Override
            public String getName() {
                return "[Controller] Teleop";
            }
        };
    }
}
