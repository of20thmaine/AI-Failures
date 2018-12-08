package failures;

public class Point {
	
	private final double x, y;
	private double meanDistance, normalizedMeanDistance;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setMeanDistance(double meanX, double meanY) {
		meanDistance = Utils.distance(x, y, meanX, meanY);
	}
	
	public void normalizeMeanDistance(double newStDev, double oldStDev, double newMean, double oldMean) {
		normalizedMeanDistance = (newMean + (meanDistance - oldMean) * (newStDev / oldStDev));
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
	
	public double getNormalizedMeanDistance() {
		return normalizedMeanDistance;
	}
	
}
