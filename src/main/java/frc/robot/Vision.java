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
import edu.wpi.first.wpilibj.vision.VisionRunner;
import edu.wpi.first.wpilibj.vision.VisionThread;

/**
 * Add your docs here.
 */
public class Vision {
    DifferentialDrive driveTrain;
    public Vision(DifferentialDrive driveTrain){
        this.driveTrain = driveTrain;
    }

      //ROBOT VISION
	private static final int IMG_WIDTH = 160;
	private static final int IMG_HEIGHT = 120;
	private final Object imgLock = new Object();
    private VisionThread ballVisionThread;
    private double ballCenterX = 0.0;
    private double ballCenterY = 0.0;
    private final Object ballImgLock = new Object();

    public void StartBallVisionThread(){
        UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);;
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
        

        
        ballVisionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
        if (!pipeline.filterContoursOutput().isEmpty()) {
            Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
                synchronized (imgLock) {
                    ballCenterX = r.x + (r.width / 2);
                    ballCenterY = r.y + (r.height / 2);
                }
        }/*else{
                synchronized (imgLock) {
                    ballCenterX = IMG_WIDTH / 2;
                    ballCenterY = IMG_HEIGHT / 2;
                }
            }
            */
        });
        ballVisionThread.start();
    }

    public void TrackBall(){

        double centerX = IMG_WIDTH / 2;
        synchronized (imgLock) {
            centerX = this.ballCenterX;
        }
    
        
        double turn = centerX - (IMG_WIDTH / 2);
        
        double sigmoidTurn = Utility.Sigmoid(turn, 0.018)*3.5;    
        SmartDashboard.putNumber("turn", turn);
        SmartDashboard.putNumber("Sigmoid", sigmoidTurn);
        driveTrain.arcadeDrive(0.2, sigmoidTurn) ;
    }

    public void StartTargetVisionThread(){
        
    }

    public void TrackTarget(){

    }


}
