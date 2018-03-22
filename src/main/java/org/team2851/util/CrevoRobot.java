package org.team2851.util;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team2851.robot.auton.TestAuton;
import org.team2851.util.auton.Auton;
import org.team2851.util.auton.AutonExecutor;
import org.team2851.util.subsystem.Subsystem;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import java.util.Vector;

public class CrevoRobot extends IterativeRobot
{
    private AutonExecutor mAutonExecutor = new AutonExecutor();
    private Vector<Subsystem> mSubsystems = new Vector<>();
    private SendableChooser autonomousChooser;
    private Subsystem testSubsystem;

    public static boolean nearOnly = false;

    protected final void registerAuton(Auton auton)
    {
        Logger.println("Registering Auton: " + auton.getName());
        autonomousChooser.addObject(auton.getName(), auton);
    }

    protected CrevoRobot()
    {
        Logger.start();
        autonomousChooser = new SendableChooser();
    }

    protected final void registerSubsystem(Subsystem subsystem)
    {
        Logger.println("Registering Subsystem: " + subsystem.toString());
        mSubsystems.add(subsystem);
    }

    @Override
    public final void robotInit()
    {
        Logger.println("|-----[ROBOT INIT]-----|");
        for (Subsystem s : mSubsystems) s.start();
        SmartDashboard.putData("Autonomous Mode", autonomousChooser);
    }

    // TODO: Autonomous Selection Issue! NullPtrException
    @Override
    public final void autonomousInit()
    {
        Auton autonSelected = new TestAuton();
        nearOnly = Preferences.getInstance().getBoolean("Near Only", true);
        Logger.println("|-----[AUTON]-----|\nAuton Selected: " + autonSelected.getName());
        mAutonExecutor.setAuton(autonSelected);
        mAutonExecutor.start();
    }

    @Override
    public void teleopInit()
    {
        Logger.println("|-----[TELEOP]-----|");
        for (Subsystem subsystem : mSubsystems) subsystem.setCommand(subsystem.getTeleopCommand());
        Logger.println("Teleop Init: Subsystem commands switched to teleop");
    }

    @Override
    public void disabledInit()
    {
        Logger.println("|-----[DISABLED]-----|\nAll subsystems being set to default command");
        for (Subsystem s : mSubsystems) { s.setCommand(s.getDefaultCommand()); }
        mAutonExecutor.stop();
    }

    // Eliminated Functions
    @Override
    public final void autonomousPeriodic() {}

    @Override
    public void teleopPeriodic() {}
}
