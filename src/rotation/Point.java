package rotation;

/**
 * @author Bobby Palmer
 * Class serves as representation of Cartesian point data.
 */
public class Point {
	private double x, y;
	private double rotateX, rotateY, distanceToMean;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void rotate(Point mean, double angleDeg) {
		double angleRad = (angleDeg / 180) * Math.PI;
	    double cosAngle = Math.cos(angleRad);
	    double sinAngle = Math.sin(angleRad);
	    double dx = (x - mean.x);
	    double dy = (y - mean.y);

	    rotateX = mean.x + (dx*cosAngle-dy*sinAngle);
	    rotateY = mean.y + (dx*sinAngle+dy*cosAngle);
	}
	
	public void setRotate(Point mean, double angleDeg) {
		double angleRad = (angleDeg / 180) * Math.PI;
	    double cosAngle = Math.cos(angleRad);
	    double sinAngle = Math.sin(angleRad);
	    double dx = (x - mean.x);
	    double dy = (y - mean.y);

	    x = mean.x + (dx*cosAngle-dy*sinAngle);
	    y = mean.y + (dx*sinAngle+dy*cosAngle);
	}
	
	public double getRotateX() {
		return rotateX;
	}
	
	public double getRotateY() {
		return rotateY;
	}
	
	public void translate(Point mean) {
		x = x - mean.x;
		y = y - mean.y;
	}
	
	public void scale(Point mean, double s) {
		x = (x - mean.x) / s;
		y = (y - mean.y) / s;
	}
	
	public void setDistanceToMean(Point mean) {
		distanceToMean = this.getDistance(mean);
	}
	
	public double getDistanceToMean() {
		return distanceToMean;
	}
	
	public String toString() {
		return  "x = " + x + ", y = " + y;
	}
	
	public double getDistance(Point p) {
		return Utils.distance(x, y, p.x, p.y);
	}
}
