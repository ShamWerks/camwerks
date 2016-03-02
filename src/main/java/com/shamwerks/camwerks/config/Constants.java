package com.shamwerks.camwerks.config;

public class Constants {
	public static final String ACK = "ACK";
	public static final String DATA = "DATA";
	public static final String SEPARATOR = "|";
	
	public static final String ANALOG = "ANALOG";
	public static final String LED = "LED";
	
	public enum CamType { INTAKE, EXHAUST };
	public enum CamDirection { CLOCKWISE, COUNTERCLOCKWISE };
	public enum ValveOpenClose { OPEN, CLOSE };
	
	public static final String camFileName    = "Name";
	public static final String camFileNbSteps = "nbSteps";
	public static final String camFileNbCylinders              = "nbCylinders";
	public static final String camFileNbIntakeCamsPerCylinder  = "nbIntakeCamsPerCylinder";
	public static final String camFileNbExhaustCamsPerCylinder = "nbExhaustCamsPerCylinder";
	public static final String camFileIntPrefix   = "INT";
	public static final String camFileExhPrefix   = "EXH";
	public static final String camFileCylPrefix   = "CYL";
}
