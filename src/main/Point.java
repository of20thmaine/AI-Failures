package main;

public class Point {
	
	private double x, y;
	private double meanDistance;;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setMeanDistance(Point mean) {
		meanDistance = Utils.distance(x, y, mean.x, mean.y);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getMeanDistance() {
		return meanDistance;
	}
	
	public void translate(Point mean) {
		x = x - mean.x;
		y = y - mean.y;
	}
	
	public void scale(Point mean, double s) {
		x = (x - mean.x) / s;
		y = (y - mean.y) / s;
	}
	
	public String toString() {
		return  "x = " + x + ", y = " + y;
	}
	
	public double getDistance(Point p) {
		return Utils.distance(x, y, p.x, p.y);
	}
	
}
