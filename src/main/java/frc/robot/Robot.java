/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Ultrasonic;
import frc.robot.Pneumatic;

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

  // creates ultra sonic sensor
  Ultrasonic ultra = new Ultrasonic(ULTRASONICPORT1, ULTRASONICPORT2);
  // Ultrasonic value is the distance between the ultra sonic sensor and the plate
  double ultraValue = 10;

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
  private Victor climbDrive = new Victor(CLIMBDRIVEPORT);
  private Servo climbLockServo = new Servo(0);

  // Controllers
  private Joystick joystickLeft = new Joystick(0);
  private Joystick joystickRight = new Joystick(1);
  private ToggleableChooser triggerToggle = new ToggleableChooser(joystickRight, 1);
  

  // Drive motor speed varibles
  double leftSpeed = 0;
  double rightSpeed = 0;

  // Arm motor speed variables
  double armSpeed = 0;

  //Climb speeds
  double climbDriveSpeed = 0;

  // wew
  private DoubleSolenoid ejectSolenoid = new DoubleSolenoid(0, 1);
  private DoubleSolenoid clampSolenoid = new DoubleSolenoid(2, 3);
  private DoubleSolenoid climbSolenoid = new DoubleSolenoid(4, 5);
  private Pneumatic ejectPneumatic = new Pneumatic(ejectSolenoid);
  private Pneumatic clampPneumatic = new Pneumatic(clampSolenoid);
  private Pneumatic climbPneumatic = new Pneumatic(climbSolenoid);


  private boolean isArmOpen = false;



  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  Vision vision = new Vision(driveTrain, joystickLeft);

  int test = -67;
  @Override
  public void robotInit() {
    Utility.robotPrefs = Preferences.getInstance();
    //vision.StartBallVisionThread();
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    SmartDashboard.putNumber("Test", 68);
    armLSBottom = new DigitalInput(BOTTOMLSPORT);
    armLSTop = new DigitalInput(TOPLSPORT);
    ultraAuto();
    updateUltraDistance();
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
    SmartDashboard.putNumber("LiveUltraRange",ultra.getRangeMM());
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
    vision.TrackBall();
    switch (m_autoSelected) {
    case kCustomAuto:
      vision.TrackBall();
    case kDefaultAuto:
      vision.TrackBall();
    default:
      // Put default auto code here
      break;
    }
  }

  @Override
  public void teleopInit() {
    updateUltraDistance();
  }

  /**
   * This function is called periodically during operator control.
   */
  boolean armOverride = false;
  @Override
  public void teleopPeriodic() {
    updateOverride();
    if (jsDeadband(joystickRight.getY()) == 0 && !armOverride) {
      armToUltra();
    } else {
      ArmController();
      updateUltraDistance();      
    }
    if (joystickLeft.getRawButton(5)) {
      vision.TrackBall();
    } else {
       DriveWithController();
       SendMotorSpeeds();
    }
    climbInput();
    UpdateServo();
    pneumaticInput();
  }

  public void climbInput(){
    if(joystickRight.getRawButton(11)){
      climbDriveSpeed = -1;
    }else{
      climbDriveSpeed = 0;
    }
  }

  //true is extended false is retracted
  private boolean clampState = false;
  private boolean ClimbPistonState = false; 
  //takes input from controller and updates pnematics
  public void pneumaticInput(){
    if(triggerToggle.ButtonDown()){
      clampState = !clampState;
    }
    if(joystickLeft.getX() >= .5){
      clampPneumatic.extendPneumatics();
    }else if(joystickLeft.getX() <= -.5){
      clampPneumatic.retractPneumatics();
    }
    

    if(joystickRight.getX() >= .5){
      ejectPneumatic.extendPneumatics();
    }else if(joystickRight.getX() <= -.5){
      ejectPneumatic.retractPneumatics();
    }

    if(joystickRight.getRawButtonPressed(10)){
      ClimbPistonState = !ClimbPistonState;
    }
    if(ClimbPistonState){
      climbPneumatic.extendPneumatics();
    }else{
      climbPneumatic.retractPneumatics();
    }
  }

  private Double ballHeight = 200.0;
  private Double discHeight = 300.0;
  //directly sets the target ultra value for easily seting the height
  public void setUltraHeight(){
    if(joystickRight.getRawButton(6)){
      ultraValue = ballHeight;
    }
    if(joystickRight.getRawButton(7)){
      ultraValue = discHeight;
    }
  }

  public void UpdateServo(){
    if(joystick.getRawButton(4)){
      climbLockServo.setAngle(0);
    }else if (joystick.getRawButton(5)){
      climbLockServo.setAngle(45);
    }
  }

  public void updateOverride(){
    //overrides the ultra sonic sensor if something goes wrong. back up just in case
    if(joystickRight.getRawButton(8)){
      armOverride = true;
    }else if(joystickRight.getRawButton(9)){
      armOverride = false;
    }
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

  // Ultra Sonic Sensor
  public void ultraAuto() {
    ultra.setAutomaticMode(true);
  }

  public void updateUltraDistance() {
    ultraValue = ultra.getRangeMM();
    SmartDashboard.putNumber("Target ultra ", ultraValue);
  }

  // sets arm motor speeds to match the position of ultraValue;
  public void armToUltra() {
    armSpeed = Utility.Sigmoid(ultra.getRangeMM() / 10, 0.5, ultraValue / 10);
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

  double DriveSpeedMultiplier = 1;
  public void DriveWithController() {
    //this sets two speeds for the robot drive train
    DriveSpeedMultiplier = HIGHSPEED;
    leftSpeed = deadband(jsDeadband(joystickLeft.getY()), DRIVEDB);
    rightSpeed = deadband(jsDeadband(joystickRight.getY()), DRIVEDB);

    //increases the max speed when turning to allow more control at lower speeds
    double maxDiff = (1 - DriveSpeedMultiplier) * DriveSpeedMultiplier; //distance of current max speed from 1
    double speedDiff = Math.abs(leftSpeed - rightSpeed); //difference in each sides speed
    double speedBuff = Math.pow(speedDiff/2, SPEEDEXPONENT) * (maxDiff * SPEEDMULTIPLER);
    DriveSpeedMultiplier += speedBuff;

  }
  
  private Double minArmSpeed = -0.1;
  public void ArmController() {
    armSpeed = deadband(jsDeadband(joystickRight.getY()), ARMDB);
    //this sets the minimum arm speed. This is set because when moving the arm 
    //up we fight against gravity but when we move it down we work with gravity
    //however when we need to climb the arm is used to press the robot up so this button lets us do that
    if(joystickRight.getRawButton(3)){
      minArmSpeed = -1.0;
    }else{
      minArmSpeed = .0;
    }
  }

  // sets the motor speeds of all motors after the code has been run.
  public void SendMotorSpeeds() {

    driveTrain.tankDrive(leftSpeed * DriveSpeedMultiplier, rightSpeed * DriveSpeedMultiplier);
    climbDrive.set(climbDriveSpeed);

    if (!armLSTop.get()) {
      armSpeed = Limit(armSpeed, -1, 0);
    } else if (!armLSBottom.get()) {
      armSpeed = Limit(armSpeed, 0, 1);
    }
    SmartDashboard.putNumber("arm speed", armSpeed);
    armMotors.set(Limit(armSpeed * ARMSPEEDMULTIPLIER, -1, 1));
  }

}

  
    
  

  