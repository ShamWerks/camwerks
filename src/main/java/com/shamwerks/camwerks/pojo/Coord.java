package com.shamwerks.camwerks.pojo;

public class Coord {
	public double x;
	public double y;
	
	public Coord(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString(){
		return "Coord : x=" + x + "  /  y=" + y;
		
	}
}
