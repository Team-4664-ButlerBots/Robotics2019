/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Preferences;

/**
 * Add your docs here.
 * idk what this is
 */
public class Utility {
    public static Preferences robotPrefs;

    public static double Sigmoid(double input, double angle ){
        return Sigmoid(input, angle, 0);
    }

    public static double Sigmoid(double input, double angle, double offset ){
        return(1/(1+Math.pow(Math.E, (-input + offset) * angle)))-0.5f;
    }
}
