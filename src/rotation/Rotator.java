package rotation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bobby Palmer
 * Class accepts an integer matrix representing a gray-scale image
 * as a parameter, and automatically constructs a rotation normalized
 * representation of the data a an array of doubles.
 */
public class Rotator {
	
	private List<Point> points;
	private double meanX, meanY, minI, maxI, minJ, maxJ, avgDistance, distance, scale;
	private int count;
	private Point meanPoint;
	private double[] representation;
	
	public Rotator(int[][] matrix) {
		this.setPoints(matrix);
		this.rotate();
		this.represent2();
//		this.represent();
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
		minI++;
		maxI++;
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
		avgDistance = 0; scale = 0;
		
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
			
			scale += (Math.pow(p.getX() - meanPoint.getX(), 2));
			scale += (Math.pow(p.getY() - meanPoint.getY(), 2));
			
			p.setDistanceToMean(meanPoint);
			avgDistance += p.getDistanceToMean();
		}
		avgDistance /= count;
		scale = Math.sqrt(scale / count);
	}
	
	private void represent() {
		Point p1 = new Point(minI + (distance/2), minJ);
		Point p2 = new Point(minI + (distance/2), maxJ);
//		Point p1 = new Point(meanPoint.getX(), minJ);
//		Point p2 = new Point(meanPoint.getX(), meanPoint.getY());
		
		List<List<Point>> b1 = this.bisect(points, p1, p2);
		
		Point p3 = new Point(minI, minJ + ((maxJ - minJ)/2));
		Point p4 = new Point(maxI, minJ + ((maxJ - minJ)/2));
//		Point p3 = new Point(minI, meanPoint.getY());
//		Point p4 = new Point(meanPoint.getX(), meanPoint.getY());
		
		List<List<Point>> b2 = this.bisect(b1.get(0), p3, p4);
		List<List<Point>> b3 = this.bisect(b1.get(1), p3, p4);
		
		Point p5 = new Point(minI, minJ);
		Point p6 = new Point(maxI, maxJ);
//		Point p5 = new Point(minI, minJ);
//		Point p6 = new Point(meanPoint.getX(), meanPoint.getY());
		
		List<List<Point>> b4 = this.bisect(b2.get(1), p5, p6);
		List<List<Point>> b5 = this.bisect(b3.get(0), p5, p6);
		
		Point p7 = new Point(maxI, minJ);
		Point p8 = new Point(minI, maxJ);
//		Point p7 = new Point(maxI, minJ);
//		Point p8 = new Point(meanPoint.getX(), meanPoint.getY());
		
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

		representation = new double[finalPoints.size()];
		
		for (int i = 0; i < finalPoints.size(); ++i) {
			for (Point p : finalPoints.get(i)) {
				if (p.getDistanceToMean() <= avgDistance) {
					representation[i] += 1;
				} else {
					representation[i] += p.getDistanceToMean() / avgDistance;
				}
			}
			representation[i] /= count;
		}
	}
	
	private void represent2() {
		maxJ = Double.MIN_VALUE; minJ = Double.MAX_VALUE;
		maxI = Double.MIN_VALUE; minI = Double.MAX_VALUE;
		
		for (Point p : points) {
			p.translate(meanPoint);
			meanPoint.translate(meanPoint);
			p.scale(meanPoint, scale);
			
			if (p.getY() < minJ) {
				minJ = p.getY();
			}
			if (p.getY() > maxI) {
				maxJ = p.getY();
			}
			if (p.getX() < minI) {
				minI = p.getX();
			}
			if (p.getX() > maxI) {
				maxI = p.getX();
			}
		}
		this.flatten();
	}
	
	private void flatten() {
		int[][] matrix = new int[10][10];
		double min = 0; double max = 7;
		
		for (Point p : points) {
			int x = (int)Math.round((max - min) / (maxI - minI) * (p.getX() - minI) + min);
			int y = (int)Math.round((max - min) / (maxJ - minJ) * (p.getY() - minJ) + min);
			
			matrix[x][y]++;
		}
		
		for (int i = 0; i < matrix.length; ++i) {
			System.out.print("|\t");
			for (int j = 0; j < matrix[i].length; ++j) {
				System.out.print(matrix[i][j] + "\t");
			}
			System.out.print("|\n");
		}
		System.out.print("\n");
		
	}
	
	public double comparison(Rotator input) {
		double d = 0;
		for (Point p : points) {
			for (Point v : input.points) {
				d += Math.pow(v.getX()-p.getX(), 2);
				d += Math.pow(v.getY()-p.getY(), 2);
			}
		}
		return Math.sqrt(d);
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
	
	public double compare(Rotator input) {
		double error = 0.0;
		
		for (int i = 0; i < representation.length; ++i) {
			error += Math.pow(representation[i] - input.representation[i], 2);
		}
		
		return 1.0 - Math.sqrt(error);
	}
	
	public void resonate(Rotator input, double learningRate) {		
		for (int i = 0; i < representation.length; ++i) {
			representation[i] += (representation[i] - input.representation[i]) * learningRate;
		}
	}
	

}
