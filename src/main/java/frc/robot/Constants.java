/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * This is the constants interface. This is used for easy changing of ports, controller buttons, and other things
 * this file should be well commented and documented for ease of use by non-programmers
 */
public interface Constants {
    //Drive motor ports 
    final int LSMOTOR = 8;
    final int RSMOTOR = 9;

    //Arm Motor ports
    final int ElevationMotorPort = 7;
    final double ARMSPEEDMULTIPLIER = 0.3;

    //Deadband Variables: For Joystick
    final double JOYDB 		= 0.05;
    final double DRIVEDB    = 0.2;
    final double ARMDB      = 0.1;

    //Sensor ports
    final int TOPLSPORT = 0;
    final int BOTTOMLSPORT = 1;
    final int ULTRASONICPORT1 = 2;
    final int ULTRASONICPORT2 = 3;
}
