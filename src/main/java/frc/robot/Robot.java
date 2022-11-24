// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.RobotContainer.AutoCommand;

public class Robot extends TimedRobot {
	private RobotContainer robotContainer;
	private Command autoCommand;
	private CommandScheduler commandScheduler;

	@Override
	public void robotInit() {
		this.robotContainer = new RobotContainer();
		this.commandScheduler = CommandScheduler.getInstance();
	}

	@Override
	public void robotPeriodic() {
		commandScheduler.run();
	}

	@Override
	public void autonomousInit() {
		this.autoCommand = robotContainer.getAutoCommand(AutoCommand.kFollowPathplannerTrajectory);
		commandScheduler.schedule(this.autoCommand);
	}

	@Override
	public void autonomousPeriodic() {
	}
}
