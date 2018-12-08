package rotation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bobby Palmer
 * Class accepts an integer matrix representing a gray-scale image
 * as a parameter, and automatically constructs a rotation normalized
 * representation of the data a an array of doubles.
 */
public class Feature {
	
	private List<Point> points;
	private double meanX, meanY, minI, maxI, minJ, maxJ,
				   distance, stDev, avgDistance;
	private int count;
	private Point meanPoint;
	private double[][] representation;
	
	public Feature(int[][] matrix) {
		this.setPoints(matrix);
		this.rotate();
		this.represent();
	}
	
	private void setPoints(int[][] matrix) {
		points = new ArrayList<Point>();
		maxI = 0; minI = Double.MAX_VALUE;
		
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] > 0) {
					if (i < minI) {
						minI = i;
					}
					if (i > maxI) {
						maxI = i;
					}
					meanX += (i+1);
					meanY += (j+1);
					count++;
					points.add(new Point(i+1, j+1));
				}
			}
		}
		minI++; maxI++;
		meanX /= count;
		meanY /= count;
		meanPoint = new Point(meanX, meanY);
	}
	
	private void rotate() {
		distance = maxI - minI;
		double bestRotation = 0;
		maxI = 0; minI = Double.MAX_VALUE;
		
		for (double i = 1.0; i <= 90.0; i += 1.0) {
			for (Point p : points) {
				p.rotate(meanPoint, i);
				
				if (p.getRotateX() < minI) {
					minI = p.getRotateX();
				}
				if (p.getRotateX() > maxI) {
					maxI = p.getRotateX();
				}
			}
			
			if (maxI-minI < distance) {
				distance = maxI-minI;
				bestRotation = i;
			}

			i *= -1;
			maxI = 0; minI = Double.MAX_VALUE;
			
			for (Point p : points) {
				p.rotate(meanPoint, i);
				
				if (p.getRotateX() < minI) {
					minI = p.getRotateX();
				}
				if (p.getRotateX() > maxI) {
					maxI = p.getRotateX();
				}
			}
			
			if (maxI-minI < distance) {
				distance = maxI-minI;
				bestRotation = i;
			}
			
			maxI = 0; minI = Double.MAX_VALUE;
			i *= -1;
		}
		
		maxJ = 0; minJ = Double.MAX_VALUE;
		
		for (Point p : points) {
			p.setRotate(meanPoint, bestRotation);
			
			if (p.getY() < minJ) {
				minJ = p.getRotateY();
			}
			if (p.getX() > maxI) {
				maxJ = p.getRotateY();
			}
			if (p.getX() < minI) {
				minI = p.getRotateX();
			}
			if (p.getX() > maxI) {
				maxI = p.getX();
			}
			
			p.setDistanceToMean(meanPoint);
			avgDistance += p.getDistanceToMean();
		}
		avgDistance /= count;
		
		stDev = 0;
		for (Point p : points) {
			stDev += Math.pow(p.getDistanceToMean() - avgDistance, 2);
		}
		stDev = Math.sqrt(stDev/count);
	}
	
	private void represent() {
		Point p1 = new Point(meanPoint.getX(), minJ);
		Point p2 = new Point(meanPoint.getX(), meanPoint.getY());
		
		List<List<Point>> b1 = this.bisect(points, p1, p2);
		
		Point p3 = new Point(minI, meanPoint.getY());
		Point p4 = new Point(meanPoint.getX(), meanPoint.getY());
		
		List<List<Point>> b2 = this.bisect(b1.get(0), p3, p4);
		List<List<Point>> b3 = this.bisect(b1.get(1), p3, p4);
		
		Point p5 = new Point(minI, minJ);
		Point p6 = new Point(meanPoint.getX(), meanPoint.getY());
		
		List<List<Point>> b4 = this.bisect(b2.get(1), p5, p6);
		List<List<Point>> b5 = this.bisect(b3.get(0), p5, p6);
		
		Point p7 = new Point(maxI, minJ);
		Point p8 = new Point(meanPoint.getX(), meanPoint.getY());
		
		List<List<Point>> b6 = this.bisect(b2.get(0), p7, p8);
		List<List<Point>> b7 = this.bisect(b3.get(1), p7, p8);
		
		List<List<Point>> finalPoints = new ArrayList<List<Point>>();

		finalPoints.add(b4.get(0));
		finalPoints.add(b4.get(1));
		finalPoints.add(b5.get(0));
		finalPoints.add(b5.get(1));
		finalPoints.add(b6.get(0));
		finalPoints.add(b6.get(1));
		finalPoints.add(b7.get(0));
		finalPoints.add(b7.get(1));

		
		representation = new double[8][4];
		
		for (int i = 0; i < finalPoints.size(); ++i) {
			for (Point p : finalPoints.get(i)) {
				if (p.getDistanceToMean() <= stDev) {
					representation[i][0] += 1.0;
				} else if (p.getDistanceToMean() <= stDev * 2) {
					representation[i][1] += 1.0;
				} else if (p.getDistanceToMean() <= stDev * 3) {
					representation[i][2] += 1.0;
				} else {
					representation[i][3] += 1.0;
				}
			}
			for (int j = 0; j < representation[i].length; ++j) {
				representation[i][j] /= count;
			}
		}
	}
	
	private List<List<Point>> bisect(List<Point> data, Point p1, Point p2) {
		List<List<Point>> newData = new ArrayList<List<Point>>();
		List<Point> b1 = new ArrayList<Point>();
		List<Point> b2 = new ArrayList<Point>();
		
		for (Point p : data) {
			if (Utils.sideOfLine(p, p1, p2) > 0) {
				b1.add(p);
			} else {
				b2.add(p);
			}
		}
		newData.add(b1);
		newData.add(b2);
		
		return newData;
	}
	
//	private void represent2() {
//		double iters = (meanX - minI) / 5.0;
//		double min = Double.MAX_VALUE;
//		double bestX = meanX;
//		double bestY = meanX;
//		
//		for (double i = minI; i <= maxI; i+=iters) {
//			Point pb = new Point(iters, minJ);
//			Point pt = new Point(iters, maxJ);
//			
//			List<List<Point>> b0 = this.bisect(points, pb, pt);
//			
//			double score = Math.abs(b0.get(0).size() - b0.get(1).size());
//			if (min > score) {
//				min = score;
//				bestX = iters;
//			}
//		}
//		
//		iters = (meanY - minJ) / 5.0;
//		min = Double.MAX_VALUE;
//		
//		for (double i = minJ; i <= maxJ; i+=iters) {
//			Point pb = new Point(minI, iters);
//			Point pt = new Point(maxI, iters);
//			
//			List<List<Point>> b0 = this.bisect(points, pb, pt);
//			
//			double score = Math.abs(b0.get(0).size() - b0.get(1).size());
//			if (min > score) {
//				min = score;
//				bestY = iters;
//			}
//		}
//		
//		meanPoint = new Point(bestX, bestY);
//		
//		Point p1 = new Point(meanPoint.getX(), minJ);
//		Point p2 = new Point(meanPoint.getX(), meanPoint.getY());
//		
//		List<List<Point>> b1 = this.bisect(points, p1, p2);
//		
//		Point p3 = new Point(minI, meanPoint.getY());
//		Point p4 = new Point(meanPoint.getX(), meanPoint.getY());
//		
//		List<List<Point>> b2 = this.bisect(b1.get(0), p3, p4);
//		List<List<Point>> b3 = this.bisect(b1.get(1), p3, p4);
//		
//		Point p5 = new Point(minI, minJ);
//		Point p6 = new Point(meanPoint.getX(), meanPoint.getY());
//		
//		List<List<Point>> b4 = this.bisect(b2.get(1), p5, p6);
//		List<List<Point>> b5 = this.bisect(b3.get(0), p5, p6);
//		
//		Point p7 = new Point(maxI, minJ);
//		Point p8 = new Point(meanPoint.getX(), meanPoint.getY());
//		
//		List<List<Point>> b6 = this.bisect(b2.get(0), p7, p8);
//		List<List<Point>> b7 = this.bisect(b3.get(1), p7, p8);
//		
//		List<List<Point>> finalPoints = new ArrayList<List<Point>>();
//
//		finalPoints.add(b4.get(0));
//		finalPoints.add(b4.get(1));
//		finalPoints.add(b5.get(0));
//		finalPoints.add(b5.get(1));
//		finalPoints.add(b6.get(0));
//		finalPoints.add(b6.get(1));
//		finalPoints.add(b7.get(0));
//		finalPoints.add(b7.get(1));
//
//		
//		representation = new double[8][4];
//		
//		for (int i = 0; i < finalPoints.size(); ++i) {
//			for (Point p : finalPoints.get(i)) {
//				if (p.getDistanceToMean() <= stDev) {
//					representation[i][0] += 1.0;
//				} else if (p.getDistanceToMean() <= stDev * 2) {
//					representation[i][1] += 1.0;
//				} else if (p.getDistanceToMean() <= stDev * 3) {
//					representation[i][2] += 1.0;
//				} else {
//					representation[i][3] += 1.0;
//				}
//			}
//			for (int j = 0; j < representation[i].length; ++j) {
//				representation[i][j] /= count;
//			}
//		}
//	}
	
	public double compare(Feature f) {
		double error = 0.0;
		
		for (int i = 0; i < representation.length; ++i) {
			for (int j = 0; j < representation[i].length; ++j) {
				error += Math.pow(representation[i][j] - f.representation[i][j], 2);
			}
		}
		
		return 1.0 - Math.sqrt(error);
	}
	
	public double compare(double[][] f) {
		double error = 0.0;
		
		for (int i = 0; i < representation.length; ++i) {
			for (int j = 0; j < representation[i].length; ++j) {
				error += Math.pow(representation[i][j] - f[i][j], 2);
			}
		}
		
		return 1.0 - Math.sqrt(error);
	}
	
	public void resonate(Feature f, double learningRate) {	
		for (int i = 0; i < representation.length; ++i) {
			for (int j = 0; j < representation[i].length; ++j) {
				representation[i][j] += (representation[i][j] -
						f.representation[i][j]) * learningRate;
			}
		}
	}
	
	public double[][] getRepresentation() {
		return representation;
	}
	
	public String toString() {
		DecimalFormat df = new DecimalFormat( "#.####" );
		String data = "";
		
		for (int i = 0; i < representation.length; ++i) {
			data += "|\t";
			for (int j = 0; j < representation[i].length; ++j) {
				data += df.format(representation[i][j]) + "\t";
			}
			data += "|\n";
		}
		
		return data + "\n";
	}

}
