package failures;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Bobby Palmer
 *
 */
public class Feature1 {
	
	private List<Point> points;
	private double meanX, meanY, averageDistance, stDev, regressionSlope;
	private int count, dimX, dimY;
	private Point intercept;
	private Point meanPoint;

	public Feature1(int[][] matrix) {
		dimX = matrix.length;
		dimY = matrix[0].length;
		points = new ArrayList<Point>();
		this.setPoints(matrix);
		
		if (count == 0) {
			averageDistance = 0;
			return;
		}
		
		this.calculateMeans();
	}
	
	private void setPoints(int[][] matrix) {
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] > 0) {
					meanX += (i+1);
					meanY += (j+1);
					count++;
					points.add(new Point(i+1, j+1));
				}
			}
		}
		
	}
	
	private void calculateMeans() {
		meanX /= count;
		meanY /= count;
		
		meanPoint = new Point(meanX, meanY);
		
		averageDistance = 0;
		
		for (Point p : points) {
			p.setMeanDistance(meanX, meanY);
			averageDistance += p.getMeanDistance();
		}
		
		averageDistance /= count;
		
		stDev = 0;
		
		for (Point p : points) {
			stDev += Math.pow((p.getMeanDistance() - averageDistance), 2);
		}
		
		stDev = Math.sqrt(stDev / (count-1));
	}
	
	public double correlationCoefficient() {
		double xMean = 0;
		double yMean = 0;
		double xDev = 0;
		double yDev = 0;
		
		for (Point p : points) {
			xMean += p.getX();
			yMean += p.getY();
		}
		
		xMean /= points.size();
		yMean /= points.size();
		
		for (Point p : points) {
			xDev += Math.pow((p.getX()-xMean), 2);
			yDev += Math.pow((p.getY()-yMean), 2);
		}
		
		xDev = Math.sqrt(xDev / (points.size()-1));
		yDev = Math.sqrt(yDev / (points.size()-1));
		
		double r = 0;
		
		for (Point p : points) {
			r += (((p.getX()-xMean)/xDev) * ((p.getY()-yMean)/yDev));
		}
		
		regressionSlope = (r / points.size()-1) * yDev / xDev;
		
		double yIntercept = yMean - (xMean * regressionSlope);
		intercept = new Point(0, yIntercept);
		
		double lSide = 0;
		double rSide = 0;
		
//		for (Point p : points) {
//			if (Utils.sideOfLine(p, intercept, meanPoint) >= 1) {
//				rSide += 1;
//			} else {
//				lSide += 1;
//			}
//		}
		
		System.out.println(rSide + "\t" + lSide);
		
		return yIntercept;
	}
	
	public void bobbycize() {
		
	}
	
	public List<Point> oneStDev() {
		List<Point> pts = new ArrayList<Point>();
		
		for (Point p : points) {
			if (p.getMeanDistance() <= stDev) {
				pts.add(p);
			}
		}

		return pts;
	}
	
	public void normalize(double newDistanceMean, double newStDev) {
		for (Point p : points) {
			p.normalizeMeanDistance(newStDev, stDev, newDistanceMean, averageDistance);
		}
	}
	
	
	public double compare(Feature1 input) {
		double similarity = 0;
		
		
		return similarity;
	}
	
	public String notNormalized() {
		Collections.sort(points, new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				if (o1.getMeanDistance() == o2.getMeanDistance()) {
					return 0;
				} else if (o1.getMeanDistance() < o2.getMeanDistance()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		int[] matrix = new int[(int)Math.round(points.get(points.size()-1).getMeanDistance()) + 1];
		String data = count + "\t|\t";
		
		for (Point p: points) {
			matrix[(int)Math.round(p.getMeanDistance())] += 1;
		}
		
		for (int i = 0; i < matrix.length; ++i) {
			data += matrix[i] + "\t";
		}
		
		return data + "|";
	}
	
	public String toString() {
		Collections.sort(points, new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				if (o1.getNormalizedMeanDistance() == o2.getNormalizedMeanDistance()) {
					return 0;
				} else if (o1.getNormalizedMeanDistance() < o2.getNormalizedMeanDistance()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		int[] matrix = new int[(int)Math.round(points.get(points.size()-1).getNormalizedMeanDistance()) + 1];

		
		String data = count + "\t|\t";
		
		for (Point p: points) {
			matrix[(int)Math.round(p.getNormalizedMeanDistance())] += 1;
		}
		
		for (int i = 0; i < matrix.length; ++i) {
			data += matrix[i] + "\t";
		}
		
		return data + "|";
	}
	
	
	
	public double getAverageDistance() {
		return averageDistance;
	}
	
	public double getStandardDeviation() {
		return stDev;
	}
	
	public int getDimX() {
		return dimX;
	}
	
	public int getDimY() {
		return dimY;
	}
	
}
