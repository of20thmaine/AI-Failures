package rotation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Circle {
	
	private List<Point> points;
	private double meanX, meanY, minI, maxI, minJ, maxJ, distance;
	private int count;
	private Point meanPoint;
	double[][] bin;
	List<List<Point>> pointData;
	
	public Circle(int[][] matrix) {
		this.setPoints(matrix);
		this.rotate();
		this.getCircle();
//		this.represent();
	}
	
	private void setPoints(int[][] matrix) {
		points = new ArrayList<Point>();
		maxI = 0; minI = Double.MAX_VALUE;
		
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
		meanX /= count;
		meanY /= count;
		meanPoint = new Point(meanX, meanY);
	}
	
	private void rotate() {
		distance = maxI - minI;
		double bestRotation = 0;
		maxI = 0; minI = Double.MAX_VALUE;
		
		for (double i = 1.0; i <= 15.0; i += 3.0) {
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
		
		maxI = 0; minI = Double.MAX_VALUE;
		maxJ = 0; minJ = Double.MAX_VALUE;
		
		for (Point p : points) {
			p.setRotate(meanPoint, bestRotation);
			
			if (p.getX() < minI) {
				minI = p.getX();
			}
			if (p.getX() > maxI) {
				maxI = p.getRotateX();
			}
			if (p.getY() < minJ) {
				minJ = p.getY();
			}
			if (p.getY() > maxJ) {
				maxJ = p.getY();
			}
			
		}
	}
	
	public void getCircle() {
		int numPoints = 16;
		double distance = 100;
		double angle = 0;
		double iters = 360 / numPoints;
		List<List<Point>> pointData = new ArrayList<List<Point>>();
		double[] pointDistance = new double[numPoints];
		
		Point[] circle = new Point[numPoints];
		bin = new double[numPoints][2];
		
		for (int i = 0; i < numPoints; ++i) {
			circle[i] = new Point(meanPoint.getX() + distance * Math.cos(Math.toRadians(angle)),
								  meanPoint.getY() + distance * Math.sin(Math.toRadians(angle)));
			angle += iters;
			pointData.add(new ArrayList<Point>());
		}
		
		for (Point p : points) {
			double min = Double.MAX_VALUE;
			int binNum = 0;
			
			for (int i = 0; i < numPoints; ++i) {
				double pos = p.getDistance(circle[i]);
				if (pos < min) {
					min = pos;
					binNum = i;
				}
			}
			
			p.setDistanceToMean(circle[binNum]);
			pointData.get(binNum).add(p);
			pointDistance[binNum] += p.getDistanceToMean();
		}
		
		for (int i = 0; i < pointData.size(); ++i) {
			pointDistance[i] /= pointData.get(i).size();
			
			for (Point p : pointData.get(i)) {
				if (p.getDistanceToMean() < pointDistance[i]) {
					bin[i][0] += 1;
				} else {
					bin[i][1] += 1;
				}
			}
		}
		
		for (int i = 0; i < numPoints; ++i) {
			for (int j = 0; j < bin[i].length; ++j) {
				bin[i][j] /= count;
			}
		}
	}
	
	public void represent() {
		int numPoints = 16;		
		bin = new double[numPoints][2];
		Point[] circle = this.getOval(numPoints);
		double[] pointDistance = new double[numPoints];
		
		for (Point p : points) {
			double min = Double.MAX_VALUE;
			int binNum = 0;
			
			for (int i = 0; i < numPoints; ++i) {
				double pos = p.getDistance(circle[i]);
				if (pos < min) {
					min = pos;
					binNum = i;
				}
			}
			
			p.setDistanceToMean(circle[binNum]);
			pointData.get(binNum).add(p);
			pointDistance[binNum] += p.getDistanceToMean();
		}
		
		for (int i = 0; i < pointData.size(); ++i) {
			pointDistance[i] /= pointData.get(i).size();
			
			for (Point p : pointData.get(i)) {
				if (p.getDistanceToMean() < pointDistance[i]) {
					bin[i][0] += 1;
				} else {
					bin[i][1] += 1;
				}
			}
		}
		
		for (int i = 0; i < numPoints; ++i) {
			for (int j = 0; j < bin[i].length; ++j) {
				bin[i][j] /= count;
			}
		}
	}
	
	public Point[] getOval(int numPoints) {
		double distance = 100;
		double angle = 0;
		double iters = 360 / numPoints;
		
		maxJ += distance; minJ += distance; maxI += distance; minI += distance;
		int quadrantDivs = (int)(numPoints / 4.0);
		int tracker = quadrantDivs;
		
		Point[] oval = new Point[numPoints];
		pointData = new ArrayList<List<Point>>();
		
		for (int i = 0; i < numPoints; ++i) {
			if (iters <= 90) {
				if (tracker == quadrantDivs) {
					tracker = 0;
					distance = maxJ;
				} else {
					distance -= ((maxJ-maxI)/tracker);
				}
				tracker++;
			} else if (iters <= 180) {
				if (tracker == quadrantDivs) {
					tracker = 0;
					distance = maxI;
				} else {
					distance -= ((maxI-minJ)/tracker);
				}
				tracker++;
			} else if (iters < 270) {
				if (tracker == quadrantDivs) {
					tracker = 0;
					distance = minJ;
				} else {
					distance -= ((minJ-minI)/tracker);
				}
				tracker++;
			} else if (iters <= 360) {
				if (tracker == quadrantDivs) {
					tracker = 0;
					distance = minI;
				} else {
					distance -= ((minI-maxJ)/tracker);
				}
				tracker++;
			}
			
			oval[i] = new Point(meanPoint.getX() + distance * Math.cos(Math.toRadians(angle)),
					  			meanPoint.getY() + distance * Math.sin(Math.toRadians(angle)));


			angle += iters;
			pointData.add(new ArrayList<Point>());
		}
		
		return oval;
	}
	
//	public void polarDescription() {
//		int numPoints = 16; int subDivs = 5;
//		double distance = Math.max(minJ, maxJ)+1;
//		
//		double angle = 0;
//		double iters = 360 / numPoints;
//		
//		Point[] circle = new Point[numPoints * subDivs];
//		
//		
//		for (int i = 0; i < numPoints; ++i) {
//			circle[i] = new Point(meanPoint.getX() + distance * Math.cos(Math.toRadians(angle)),
//								  meanPoint.getY() + distance * Math.sin(Math.toRadians(angle)));
//			angle += iters;
//			pointData.add(new ArrayList<Point>());
//		}
//		
//		for (Point p : points) {
//			double min = Double.MAX_VALUE;
//			int binNum = 0;
//			
//			for (int i = 0; i < numPoints; ++i) {
//				double pos = p.getDistance(circle[i]);
//				if (pos < min) {
//					min = pos;
//					binNum = i;
//				}
//			}
//		}
//	}
//	
	public double compare(Circle c) {
		double error = 0.0;
		
		for (int i = 0; i < bin.length; ++i) {
			for (int j = 0; j < bin[i].length; ++j) {
				error += Math.pow(bin[i][j] - c.bin[i][j], 2);
			}
		}
		
		return 1.0 - Math.sqrt(error/(bin.length * bin[0].length));
	}
	
	public double compare(double[][] c) {
		double error = 0.0;
		
		for (int i = 0; i < bin.length; ++i) {
			for (int j = 0; j < bin[i].length; ++j) {
				error += Math.pow(c[i][j] - bin[i][j], 2);
			}
		}
		
		return 1.0 - error;
	}
	
	public double[][] getRepresentation() {
		return bin;
	}
	
	public String toString() {
		DecimalFormat df = new DecimalFormat( "#.####" );
		String data = "";
		
		for (int i = 0; i < bin.length; ++i) {
			data += "|\t";
			for (int j = 0; j < bin[i].length; ++j) {
				data += df.format(bin[i][j]) + "\t";
			}
			data += "|\n";
		}
		
		return data + "\n";
	}

}
