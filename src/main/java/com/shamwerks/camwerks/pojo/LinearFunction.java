package com.shamwerks.camwerks.pojo;

/**
 * @author ShamWerks.com
 * LinearFunction
 *     linear function type :  y = ax + b
 *     a = Slope
 *     b = yIntercept 
 *     Perpendiculaire : slopeA * slopeB = -1
 *
 *     y = ax + b
 *     y = cx + d 
 *     coordonnées intersection :
 *     x = (d - b) / (a - c)
 *     y = (ad - bc) / (a - c)
 */
public class LinearFunction {

	public double a;
	public double b;

	public double angle; //optional, not sure I actually need this one.

	// constructors
	public LinearFunction (double a, double b){
		this.a=a;
		this.b=b;
	}
	
	public LinearFunction (Coord a, Coord b){
		this.a = (b.y - a.y) / (b.x - a.x);
		this.b = a.y - (this.a * a.x);
	}

	//returns the coordinates of the intersection between 2 linears function
	public Coord getIntersection(LinearFunction f2){
		return new Coord(
								(f2.b - b) / (a - f2.a)  ,
								((a * f2.b) - (b * f2.a)) / (a - f2.a)
							  );
	}

	//calculates y as in y=ax+b
	public double y(double x){
		return (a * x) + b;
	}

	@Override
	public String toString(){
		return "a="+ a + " / b=" + b;
	}
}
