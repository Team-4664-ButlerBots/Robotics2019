/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionPipeline;
import edu.wpi.first.wpilibj.vision.VisionRunner;
import edu.wpi.first.wpilibj.vision.VisionThread;

/*
*todo create a generic vision class to be used for tracking ball and vision targets
Class will take in a superclass object of The pipeline so that different grip pipelines can be fed into the 
constructor.
*/

/**
 * Add your docs here.
 */
public class Vision {
    DifferentialDrive driveTrain;
    Joystick gamepad;

    //NETWORKING
    
    NetworkTableEntry xCenter, yCenter, RectSize, AngleMultiplier, SpeedMultiplier;
    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    NetworkTable table = ntinst.getTable("vision");

    public Vision(DifferentialDrive driveTrain, Joystick _joy) {
        this.driveTrain = driveTrain;
        this.gamepad = _joy;
        xCenter = table.getEntry("Xposition");
        yCenter = table.getEntry("Yposition");
        RectSize = table.getEntry("Size");
        AngleMultiplier = table.getEntry("AngleMult");
        SpeedMultiplier = table.getEntry("SpeedMult");
        AngleMultiplier.setDouble(1);
        SpeedMultiplier.setDouble(1);
    }

    // ROBOT VISION
    private static final int IMG_WIDTH = 208;
    private static final int IMG_HEIGHT = 120;
    // used for safely accesing data managed by multiple threads

    public void TrackBall() {
            double centerX = xCenter.getDouble(IMG_WIDTH / 2);
            double centerY = yCenter.getDouble(IMG_HEIGHT / 2);
            
            double multiplier = Utility.robotPrefs.getDouble("angle Multiplier", 2);
            double xPercent = ((centerX - (IMG_WIDTH / 2)) / IMG_WIDTH) * 2;
            double yPercent = ((centerY - (IMG_HEIGHT / 2)) / IMG_HEIGHT) * 2;
            double sigmoidTurn = Utility.Sigmoid(xPercent, 
            Math.abs(xPercent * AngleMultiplier.getDouble(5))) * SpeedMultiplier.getDouble(1);

            //==========EXPERIMENTAL===========
            if(Math.abs(sigmoidTurn) < 0.2 && Math.abs(sigmoidTurn) > 0.01){
                if(sigmoidTurn < 0){
                    sigmoidTurn = -0.2;
                }else{
                    sigmoidTurn = 0.2;
                }
            }
            //============END================

            /*
             * old math double turn = centerX - (IMG_WIDTH / 2); double sigmoidTurn =
             * Utility.Sigmoid(turn, 0.018)*3.5;
             */
            SmartDashboard.putNumber("xPercent", xPercent);
            SmartDashboard.putNumber("yPercent", yPercent);
            SmartDashboard.putNumber("Sigmoid", sigmoidTurn);
            driveTrain.arcadeDrive(gamepad.getY(), sigmoidTurn);
    }

}
