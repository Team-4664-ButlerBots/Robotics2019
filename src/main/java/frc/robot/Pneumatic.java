package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Pneumatic {
	private DoubleSolenoid solenoidyboi;
	//true is extended false is retracted.
	private Boolean state = false;
	//private DoubleSolenoid solenoidyboi = new DoubleSolenoid(1, 2);
	
	public Pneumatic(DoubleSolenoid solenoidyboi){
		this.solenoidyboi = solenoidyboi;
	}
	
	//true state value
	public void extendPneumatics(){
		if(!state){
			solenoidyboi.set(DoubleSolenoid.Value.kOff);
		}
		solenoidyboi.set(DoubleSolenoid.Value.kForward);
		state = true;
	}
	public void retractPneumatics(){
		if(state){
			solenoidyboi.set(DoubleSolenoid.Value.kOff);
		}		
		solenoidyboi.set(DoubleSolenoid.Value.kReverse);
		state = false;
	}
}
