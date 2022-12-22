
package com.hamosad1657.lib.motors;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.hamosad1657.lib.HaUnitConvertor;
import com.hamosad1657.lib.HaUnits.PIDGains;

import com.hamosad1657.lib.HaUnits.Position;
import com.hamosad1657.lib.HaUnits.Velocity;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.util.sendable.SendableBuilder;

public class HaTalonSRX extends HaBaseTalon {
	public final static double kWheelRadNone = -1;


/** Add your docs here. */
public class HaTalonSRX extends HaBaseTalon {


	public WPI_TalonSRX motor;

	private double wheelRadiusMeters;
	private double encoderTicksPerRev;
	private double percentOutput;

	/**
	 * This class currently only supports the integrated encoder and CANCoder/other
	 * CTRE magnetic encoder as feedback devices for the Talon. Add support yourself
	 * if you want.
	 */
	public HaTalonSRX(WPI_TalonSRX motor, PIDGains PIDGains, double wheelRadiusMeters, FeedbackDevice feedbackDevice) {
		this.motor = motor;
		this.motor.configSelectedFeedbackSensor(feedbackDevice);
		this.wheelRadiusMeters = wheelRadiusMeters;
		this.configPID(PIDGains);
		switch (feedbackDevice) {
			case CTRE_MagEncoder_Absolute:
				this.encoderTicksPerRev = 4096;
				break;
			case IntegratedSensor:

				this.encoderTicksPerRev = 2048;
				break;
			default:
				break;
		}
	}

	public HaTalonSRX(WPI_TalonSRX motor, PIDGains PIDGains, double wheelRadiusMeters) {
		this(motor, PIDGains, wheelRadiusMeters, FeedbackDevice.None);
	}

	public HaTalonSRX(WPI_TalonSRX motor, PIDGains PIDGains, FeedbackDevice feedbackDevice) {
		this(motor, PIDGains, kWheelRadNone, feedbackDevice);
	}

	public HaTalonSRX(WPI_TalonSRX motor, PIDGains PIDGains) {
		this(motor, PIDGains, kWheelRadNone, FeedbackDevice.None);
	}

	public HaTalonSRX(WPI_TalonSRX motor, double wheelRadiusMeters, FeedbackDevice feedbackDevice) {
		this(motor, PIDGains.zeros(), wheelRadiusMeters, feedbackDevice);
	}

	public HaTalonSRX(WPI_TalonSRX motor, double wheelRadiusMeters) {
		this(motor, PIDGains.zeros(), wheelRadiusMeters, FeedbackDevice.None);
	}

	public HaTalonSRX(WPI_TalonSRX motor, FeedbackDevice feedbackDevice) {
		this(motor, PIDGains.zeros(), -1, feedbackDevice);
	}

	public HaTalonSRX(WPI_TalonSRX motor) {
		this(motor, PIDGains.zeros(), -1, FeedbackDevice.None);
	}

	// TalonSRX takes encoder ticks per 100 ms as velocity setpoint
	@Override
	public void set(double value, Velocities type) {
		switch (type) {
			case kMPS:
				value = (HaUnitConvertor.MPSToRPM(value, this.wheelRadiusMeters) * 600 * this.encoderTicksPerRev);
				this.motor.set(ControlMode.Velocity, value);
				break;
			case kRPM:
				value = value / 600 * this.encoderTicksPerRev;
				break;
			case kDegPS:
				value = (HaUnitConvertor.degPSToRPM(value)) * 600 * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Velocity, value);
				break;
			case kRadPS:
				value = (HaUnitConvertor.radPSToRPM(value)) * 600 * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Velocity, value);
				break;
		}
	}

	// TODO: check math
	@Override
	public double get(Velocity type) {
		switch (type) {
			case kMPS:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * this.wheelRadiusMeters;
			case kRPM:
				return this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev;
			case kDegPS:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * 360;
			case kRadPS:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * (Math.PI * 2);
		}
		return 0;
	}

	// TalonSRX takes encoder ticks as position setpoint
	@Override
	public void set(double value, Position type) {
		switch (type) {
			case kDegrees:
				value = (value / 360) * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Position, value);
				break;
			case kRad:
				value = (value / (Math.PI * 2)) * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Position, value);
				break;
			case kRotations:
				value = value * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Position, value);
				break;
			default:
				break;
		}

	}

	@Override
	public double get(Position type) {
		switch (type) {
			case kDegrees:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * 360;
			case kRad:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * (Math.PI * 2);
			case kRotations:
				return this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev;
			default:
				break;
		}

		return 0;
	}

	@Override
	public void set(double value) {
		this.motor.set(ControlMode.PercentOutput, value);
		this.percentOutput = value;
	}

	@Override
	public double get() {
		return this.percentOutput;
	}

	@Override
	public void setCurrent(double value) {
		this.motor.set(ControlMode.Current, value);

	}

	@Override
	public double getCurrent() {
		return this.motor.getSupplyCurrent();
	}

	@Override
	public void configPID(PIDGains PIDGains) {
		this.motor.config_kP(0, PIDGains.p);
		this.motor.config_kI(0, PIDGains.i);
		this.motor.config_kD(0, PIDGains.d);
		this.motor.config_IntegralZone(0, PIDGains.iZone);
	}

	@Override
	public void setEncoderPosition(double value, Position type) {
		switch (type) {
			case kDegrees:
				value = (value / 360) * this.encoderTicksPerRev;
				this.motor.setSelectedSensorPosition(value);
				break;
			case kRad:
				value = (value / (Math.PI * 2)) * this.encoderTicksPerRev;
				this.motor.setSelectedSensorPosition(value);
				break;
			case kRotations:
				value = value * this.encoderTicksPerRev;
				this.motor.setSelectedSensorPosition(value);
				break;
			default:
				break;
		}
	}

	@Override
	public void setIdleMode(IdleMode idleMode) {
		switch (idleMode) {
			case kBrake:
				this.motor.setNeutralMode(NeutralMode.Brake);
				break;
			case kCoast:
				this.motor.setNeutralMode(NeutralMode.Coast);
		}
	}

	@Override
	public void initSendable(SendableBuilder builder) {

	}

	@Override
	public void initShuffleboard() {

	}
}
