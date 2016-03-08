package com.shamwerks.camwerks.config;

import java.math.BigDecimal;

public class Toolbox {

	public static double round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	
	public static double stepsToCrankAngle(double inStep, int nbSteps){
		return Toolbox.round( inStep*(360.0F/nbSteps) * 2   ,2); //x2 because crankshaft does 2 turns for 1 turn of camshaft
	}
}
