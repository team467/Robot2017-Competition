package org.usfirst.frc.team467.robot;

public class PIDMap {
	private static int NUMMoters = 4;
	public static PIDMap[] values = new PIDMap[NUMMoters + 1];
	public final double p, i, d, f;
	private PIDMap(int id, double f, double p, double i, double d){
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		PIDMap.values[id] = this;
	}

	public static void init(){
		//should change 0, 2, 3, 4
		new PIDMap(0, 3.0, 2.16, 0.00864, 135.0);
		new PIDMap(1, 3.0, 2.16, 0.00864, 135.0);
		new PIDMap(2, 3.0, 2.16, 0.00864, 135.0);
		new PIDMap(3, 3.0, 2.16, 0.00864, 135.0);
		new PIDMap(4, 3.0, 2.16, 0.00864, 135.0);
	}
	
}
