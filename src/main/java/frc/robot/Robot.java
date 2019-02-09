/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot implements Constants {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  // initilize limit switch
  DigitalInput armLSTop;
  DigitalInput armLSBottom;

  // Robot Drive Train
  private Victor leftSideMotor = new Victor(LSMOTOR);
  private Victor rightSideMotor = new Victor(RSMOTOR);

  private SpeedControllerGroup leftSideGroup = new SpeedControllerGroup(leftSideMotor); //
  private SpeedControllerGroup rightSideGroup = new SpeedControllerGroup(rightSideMotor); //

  private DifferentialDrive driveTrain = new DifferentialDrive(leftSideGroup, rightSideGroup);

  // Robot Arm
  private Victor armMotors = new Victor(ElevationMotorPort);

  // Controllers
  private Joystick gamepad = new Joystick(0);
  private Joystick joystick = new Joystick(1);

  // Drive motor speed varibles
  double leftSpeed = 0;
  double rightSpeed = 0;

  // Arm motor speed variables
  double armSpeed = 0;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    armLSBottom = new DigitalInput(0);
    armLSTop = new DigitalInput(1);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:
      // Put custom auto code here
      break;
    case kDefaultAuto:
    default:
      // Put default auto code here
      break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    DriveWithController();

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  // Limits a variable to to a given range
  public double Limit(double value, double min, double max) {
    if (value > max)
      return max;
    if (value < min)
      return min;
    return value;
  }

  // used for the deadband on the joystick
  public double jsDeadband(double js) {
    js = Limit(js, -1.0, 1.0);
    if (Math.abs(js) <= JOYDB)
      return 0.0;
    if (js > JOYDB)
      return (js - JOYDB) / (1.0 - JOYDB);
    else
      return (js + JOYDB) / (1.0 - JOYDB);
  }

  // used for the deadband on the motor
  public double deadband(double input, double motorDeadband) {
    input = Limit(input, -1.0, 1.0);
    if (input == 0.0)
      return 0.0;
    else if (input > 0)
      return (1 - motorDeadband) * input + motorDeadband;
    else
      return (1 - motorDeadband) * input - motorDeadband;
  }

  public void DriveWithController() {
    if (gamepad.getRawButton(7)) {
      armSpeed = Limit(armSpeed, -0.5, .5);
    } else if (gamepad.getRawButton(8)) {
      armSpeed = Limit(armSpeed, -1, 1);
    }
    armMotors.set(armSpeed);
    leftSpeed = deadband(jsDeadband(gamepad.getRawAxis(3)), DRIVEDB);
    rightSpeed = deadband(jsDeadband(gamepad.getY()), DRIVEDB);

  }

  public void ArmController() {
    armSpeed = deadband(jsDeadband(joystick.getY()), ARMDB);
  }

  // sets the motor speeds of all motors after the code has been run.
  public void SendMotorSpeeds() {
    driveTrain.tankDrive(leftSpeed * DriveMaxSpeed, rightSpeed * DriveMaxSpeed);

    if (armLSTop.get()) {
      armSpeed = Limit(armSpeed, -1, 0);
    } else if (armLSBottom.get()) {
      armSpeed = Limit(armSpeed, 0, 1);
    }
    armMotors.set(armSpeed);
  }

}
