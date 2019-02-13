package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Pneumatic {
	private DoubleSolenoid solenoidyboi;
	private Compressor compressyboi;
	//private DoubleSolenoid solenoidyboi = new DoubleSolenoid(1, 2);
	
	public Pneumatic(DoubleSolenoid solenoidyboi, Compressor compressyboi){
		this.solenoidyboi = solenoidyboi;
		this.compressyboi = compressyboi;
		solenoidyboi.set(DoubleSolenoid.Value.kOff);
		compressyboi.clearAllPCMStickyFaults();
		compressyboi.start();
		compressyboi.setClosedLoopControl(true);
	}
	
	public void testPneumatics(){
		solenoidyboi.set(DoubleSolenoid.Value.kForward);
	}

	public void testPneumatics2(){
		solenoidyboi.set(DoubleSolenoid.Value.kReverse);
	}
	public void stopPneumatics(){
		solenoidyboi.set(DoubleSolenoid.Value.kOff);
	}
}
