package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Pneumatic {
	private DoubleSolenoid solenoidyboi;
	//private DoubleSolenoid solenoidyboi = new DoubleSolenoid(1, 2);
	
	public Pneumatic(DoubleSolenoid solenoidyboi){
		this.solenoidyboi = solenoidyboi;
	}
	
	public void testPneumatics(){
		solenoidyboi.set(DoubleSolenoid.Value.kForward);
	}
	public void stopPneumatics(){
		solenoidyboi.set(DoubleSolenoid.Value.kOff);
	}
}
