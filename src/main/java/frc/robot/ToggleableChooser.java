/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Takes in a button and controller as a input. This will detect when a button
 * is initialy pressed and not return true until the button is let up before
 * being pressed again. 
 */
public class ToggleableChooser {

    private Joystick joyRef;
    private int ButtonNum = 0;
    public ToggleableChooser(Joystick _joystick, int _buttonNumber){
        joyRef = _joystick;
        ButtonNum = _buttonNumber;
    }

    private boolean pressed = false;
    public boolean ButtonDown(){
        if(!pressed && joyRef.getRawButton(ButtonNum)){
            pressed = true;
            return true;
        }else if(joyRef.getRawButton(ButtonNum)){
            return false;
        }else{
            pressed = false;
            return false;
        }
    }

}
