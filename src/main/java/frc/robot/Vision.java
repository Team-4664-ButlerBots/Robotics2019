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
    VisionPipeline cvPipeline;

    public Vision(DifferentialDrive driveTrain) {
        this.driveTrain = driveTrain;
        cvPipeline = new GripPipeline();
    }

    // this is for creating vision pipelines with different pipelines. This would
    // for example allow us to have a vision object for
    // the reflective tape and a vision object for the ball
    public Vision(DifferentialDrive driveTrain, VisionPipeline cvPipeline) {
        this.driveTrain = driveTrain;
        this.cvPipeline = cvPipeline;
    }

    // ROBOT VISION
    private static final int IMG_WIDTH = 160;
    private static final int IMG_HEIGHT = 120;
    private final Object imgLock = new Object();
    private VisionThread VisionThread;
    private double CenterX = 0.0;
    private double CenterY = 0.0;
    // used for safely accesing data managed by multiple threads
    private final Object ImgLock = new Object();

    public void StartBallVisionThread() {

        UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);

        // draws a rect around the found contours and sets
        VisionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
            if (!pipeline.filterContoursOutput().isEmpty()) {
                Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
                synchronized (imgLock) {
                    CenterX = r.x + (r.width / 2);
                    CenterY = r.y + (r.height / 2);
                }
            } else {
                //if there are no contours found then 
                synchronized (imgLock) {
                    CenterX = -1;
                    CenterY = -1;
                }
            }

        });
        VisionThread.start();
    }

    public void TrackBall() {

        double centerX = IMG_WIDTH / 2;
        synchronized (imgLock) {
            centerX = this.CenterX;
        }
        if (CenterX != -1 && CenterY != -1) {

            // this turn variable should be a percent of the pixel width ranging from 0 to 1
            double turn = (centerX - (IMG_WIDTH / 2)) / IMG_WIDTH;
            double sigmoidTurn = Utility.Sigmoid(turn, 1) * 2.5;
            /*
             * old math double turn = centerX - (IMG_WIDTH / 2); double sigmoidTurn =
             * Utility.Sigmoid(turn, 0.018)*3.5;
             */
            SmartDashboard.putNumber("turn", turn);
            SmartDashboard.putNumber("Sigmoid", sigmoidTurn);
            driveTrain.arcadeDrive(0.2, sigmoidTurn);
        }else{
            driveTrain.arcadeDrive(0, 0);
        }
    }

}
