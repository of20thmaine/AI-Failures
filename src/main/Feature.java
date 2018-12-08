package main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bobby Palmer
 *
 */
public class Feature {
	
	private List<Point> points, edgePoints;
	private double meanX, meanY, averageDistance, distanceDeviation;
	private int count, dimX, dimY;
	private Point meanPoint;
	private double[] representation;
	private double shapeRepresentation;

	public Feature(int[][] matrix) {
		dimX = matrix[0].length+1;
		dimY = matrix.length+1;
		points = new ArrayList<Point>();
		this.setPoints(matrix);

		if (count == 0) {
			averageDistance = 0;
			return;
		}
		
		this.calculateMeans();
//		this.fourPointBisect();
//		this.regressionFlattener();
		this.flatten();
//		this.naiveBisect();
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
			p.setMeanDistance(meanPoint);
			averageDistance += p.getMeanDistance();
		}
		
		averageDistance /= count;
		
		distanceDeviation = 0;
		
		for (Point p : points) {
			distanceDeviation += Math.pow((p.getMeanDistance() - averageDistance), 2);
		}
		
		distanceDeviation = Math.sqrt(distanceDeviation / (count-1));
	}
	
	private double[] regressionLine(List<Point> points) {
		if (points.size() == 0) {
			return new double[] {0,0};
		}
		
        double sumx = 0.0, sumy = 0.0;
        
        for (Point p : points) {
        	sumx  += p.getX();
            sumy  += p.getY();
        }

        double xbar = sumx / points.size();
        double ybar = sumy / points.size();

        double xxbar = 0.0, xybar = 0.0;

        for (Point p : points) {
        	xxbar += (p.getX() - xbar) * (p.getX() - xbar);
            xybar += (p.getX() - xbar) * (p.getY() - ybar);
        }
        
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;
        
        return new double[] {beta1, beta0};
	}
	
	public void flatten() {
		double[] line = this.regressionLine(this.points);
		
		double m = line[0];
		double b = line[1];
		
		Point p1 = new Point(0, (m * 0 + b));
		Point p2 = new Point(dimX, (m * dimX+ b));

		List<List<Point>> b1 = this.bisect(points, p1, p2);
		
		double pm;
		if (m == 0) {
			pm = 0;
		} else {
			pm = -1 / m;
		}
		
		double pb = meanPoint.getY() - (pm * meanPoint.getX());
		
		Point p3 = new Point(0, (pm * 0 + pb));
		Point p4 = new Point(dimX, (pm * dimX + pb));
		
		List<List<Point>> b2 = this.bisect(b1.get(0), p3, p4);
		List<List<Point>> b3 = this.bisect(b1.get(1), p3, p4);
		b1.clear();
		
		double angle1 = this.angle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		double angle2 = this.angle(p3.getX(), p3.getY(), p4.getX(), p4.getY());
		
		double diff = (angle1 - angle2) / 2;
		m = Math.tan(diff);
		b = meanPoint.getY() - (m * meanPoint.getX());
		
		
		Point p5 = new Point(0, (m * 0 + b));
		Point p6 = new Point(dimX, (m * dimX+ b));
		
		
		b1 = this.bisect(b2.get(0), p5, p6);
		List<List<Point>> b4 = this.bisect(b3.get(1), p5, p6);
		
		// Last 45 angle....
		if (m == 0) {
			pm = 0;
		} else {
			pm = -1 / m;
		}
		
		pb = meanPoint.getY() - (pm * meanPoint.getX());
		
		Point p7 = new Point(0, (pm * 0 + pb));
		Point p8 = new Point(dimX, (pm * dimX + pb));
		
		List<List<Point>> b5 = this.bisect(b2.get(1), p7, p8);
		List<List<Point>> b6 = this.bisect(b3.get(0), p7, p8);
		
		
		List<List<Point>> finalPoints = new ArrayList<List<Point>>();


		finalPoints.add(b1.get(0));
		finalPoints.add(b1.get(1));
		finalPoints.add(b5.get(0));
		finalPoints.add(b5.get(1));
		finalPoints.add(b4.get(0));
		finalPoints.add(b4.get(1));
		finalPoints.add(b6.get(0));
		finalPoints.add(b6.get(1));

		

		representation = new double[finalPoints.size()];
		
		for (int i = 0; i < finalPoints.size(); ++i) {
			representation[i] = 0;
			
			for (int j = 0; j < finalPoints.get(i).size(); ++j) {
				if (finalPoints.get(i).get(j).getMeanDistance() <= distanceDeviation) {
					representation[i] += 1.0;
				} else if (finalPoints.get(i).get(j).getMeanDistance() <= distanceDeviation * 2) {
					representation[i] += 0.6;
				} else if (finalPoints.get(i).get(j).getMeanDistance() <= distanceDeviation * 3) {
					representation[i] += 0.4;
				} else if (finalPoints.get(i).get(j).getMeanDistance() <= distanceDeviation * 4) {
					representation[i] += 0.2;
				} else {
					representation[i] += 0.01;
				}
			}
			if (representation[i] != 0) {
				representation[i] /= count;
			}
		}
	}
	
	public double angle (double x1, double y1, double x2, double y2) {
	    double xdiff = x1 - x2;
	    double ydiff = y1 - y2;
	    //double tan = xdiff / ydiff;
	    double atan = Math.atan2(ydiff, xdiff);
	    return atan;
	}
	
	public void regressionFlattener() {
		double[] line = this.regressionLine(this.points);
		List<Double> angles = new ArrayList<Double>();
		
		double m = line[0];
		double b = line[1];
		
		Point p1 = new Point(0, (m * 0 + b));
		Point p2 = new Point(dimX, (m * dimX+ b));
		angles.add(Math.atan2(p2.getY()-p1.getY(), p2.getX()-p1.getX()));

		List<List<Point>> b1 = this.bisect(points, p1, p2);
		
		List<List<Point>> b2 = new ArrayList<List<Point>>();
		List<List<Point>> b3 = new ArrayList<List<Point>>();
		
		for (List<Point> p : b1) {
			line = this.regressionLine(p);
			
			m = line[0];
			b = line[1];
			
			p1 = new Point(0, (m * 0 + b));
			p2 = new Point(dimX, (m * dimX+ b));
			
			angles.add(Math.atan2(p2.getY()-p1.getY(), p2.getX()-p1.getX()));
			
			b2 = this.bisect(p, p1, p2);
			
			for (List<Point> pts : b2) {
				b3.add(pts);
			}
		}
		
		b1.clear();
		b2.clear();
		
		for (List<Point> p : b3) {
			line = this.regressionLine(p);
			
			m = line[0];
			b = line[1];
			
			angles.add(m);
			
			p1 = new Point(0, (m * 0 + b));
			p2 = new Point(dimX, (m * dimX+ b));
			
			angles.add(Math.atan2(p2.getY()-p1.getY(), p2.getX()-p1.getX()));
			
			b2 = this.bisect(p, p1, p2);
			
			for (List<Point> pts : b2) {
				b1.add(pts);
			}
		}
		
		representation = new double[angles.size()];
		
		double adjustment = angles.get(0) - Math.atan2(0, 0);
		
		for (int i = 0; i < angles.size(); ++i) {
			representation[i] = angles.get(i) - adjustment;
		}
		
		
		representation = new double[b1.size()];
		
		for (int i = 0; i < b1.size(); ++i) {
			representation[i] = 0;
			
			for (int j = 0; j < b1.get(i).size(); ++j) {
				if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation) {
					representation[i] += 1.0;
				} else if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation * 2) {
					representation[i] += 0.6;
				} else if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation * 3) {
					representation[i] += 0.4;
				} else if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation * 4) {
					representation[i] += 0.2;
				} else {
					representation[i] += 0.001;
				}
			}
			if (representation[i] != 0) {
				representation[i] /= count;
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
	
	
	public void fourPointBisect() {		
		Point[] compares = new Point[] {
				new Point(0, 0),
				new Point(dimY, 0),
				new Point(0, dimX),
				new Point(dimX, dimY)};
		Point[] minPoints = new Point[4];
		double[] minValues = new double[4]; 
		
		for (int i = 0; i < compares.length; ++i) {
			minValues[i] = compares[i].getDistance(points.get(0));
			minPoints[i] = points.get(0);
		}
		
		for (Point p : points) {
			for (int i = 0; i < compares.length; ++i) {
				double d = compares[i].getDistance(p);
				if (d < minValues[i]) {
					minPoints[i] = p;
					minValues[i] = d;
				}
			}
		}
		
		double d1 = minPoints[0].getDistance(minPoints[1]) + minPoints[2].getDistance(minPoints[3]);
		double d2 = minPoints[0].getDistance(minPoints[2]) + minPoints[1].getDistance(minPoints[3]);
		
		List<List<Point>> b1;
		Point p1, p2;
		
		if (d1 > d2) {
			p1 = new Point(((minPoints[0].getX() + minPoints[1].getX()) / 2), 
						   ((minPoints[0].getY() + minPoints[1].getY()) / 2));
			p2 = new Point(((minPoints[2].getX() + minPoints[3].getX()) / 2), 
					 	   ((minPoints[2].getY() + minPoints[3].getY()) / 2));
			b1 = this.bisect(points, p1, p2);
		} else {
			p1 = new Point(((minPoints[0].getX() + minPoints[2].getX()) / 2), 
					 	   ((minPoints[0].getY() + minPoints[2].getY()) / 2));
			p2 = new Point(((minPoints[1].getX() + minPoints[3].getX()) / 2), 
		 			 	   ((minPoints[1].getY() + minPoints[3].getY()) / 2));
			b1 = this.bisect(points, p1, p2);
		}
		
		Point p3 = new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
		
		double delta = p1.getDistance(p2) / Math.sqrt(Math.pow(p1.getY()-p2.getY(), 2) + Math.pow(p2.getX()-p1.getX(), 2));
		double deltaX = (p1.getY() - p2.getY()) * delta;
		double deltaY = (p2.getX() - p1.getX()) * delta;
		
		p1 = new Point(p3.getX() + deltaX, p3.getY() + deltaY);
		p2 = new Point(p3.getX() - deltaX, p3.getY() - deltaY);
		
		List<List<Point>> b2 = this.bisect(b1.get(0), p1, p2);
		List<List<Point>> b3 = this.bisect(b1.get(1), p1, p2);
		
		b1.clear();
		
		for (List<Point> p : b2) {
			b1.add(p);
		}
		for (List<Point> p : b3) {
			b1.add(p);
		}
		
		representation = new double[b1.size()];
		
		for (int i = 0; i < b1.size(); ++i) {
			representation[i] = 0;
			
			for (int j = 0; j < b1.get(i).size(); ++j) {
				if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation) {
					representation[i] += 1.0;
				} else if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation * 2) {
					representation[i] += 0.6;
				} else if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation * 3) {
					representation[i] += 0.4;
				} else if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation * 4) {
					representation[i] += 0.2;
				} else {
					representation[i] += 0.001;
				}
			}
			representation[i] /= count;
		}
		
	}
	
	public void naiveBisect() {
		int min = count;
		Point bestp1 = points.get(0);
		Point bestp2 = points.get(0);
		
		outer:
		for (int i = 0; i < dimX; ++i) {
			for (int j = 0; j < dimX; ++j) {
				Point p1 = new Point(0, i);
				Point p2 = new Point(dimY, j);
				List<List<Point>> b1 = this.bisect(points, p1, p2);
				
				int possible = Math.abs(b1.get(0).size() - b1.get(1).size());
				
				if (possible < min) {
					bestp1 = p1;
					bestp2 = p2;
					min = possible;
					
					if (min == 0) {
						break outer;
					}
				}
			}
		}
		
		Point p1 = bestp1;
		Point p2 = bestp2;
		
		List<List<Point>> b1 = this.bisect(points, p1, p2);

		Point p3 = new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
		
		double delta = p1.getDistance(p2) / Math.sqrt(Math.pow(p1.getY()-p2.getY(), 2) + Math.pow(p2.getX()-p1.getX(), 2));
		double deltaX = (p1.getY() - p2.getY()) * delta;
		double deltaY = (p2.getX() - p1.getX()) * delta;
		
		p1 = new Point(p3.getX() + deltaX, p3.getY() + deltaY);
		p2 = new Point(p3.getX() - deltaX, p3.getY() - deltaY);
		
		List<List<Point>> b2 = this.bisect(b1.get(0), p1, p2);
		List<List<Point>> b3 = this.bisect(b1.get(1), p1, p2);
		
		b1.clear();
		
		for (List<Point> p : b2) {
			b1.add(p);
		}
		for (List<Point> p : b3) {
			b1.add(p);
		}
		
		representation = new double[b1.size()];
		
		for (int i = 0; i < b1.size(); ++i) {
			representation[i] = 0;
			
			for (int j = 0; j < b1.get(i).size(); ++j) {
				if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation) {
					representation[i] += 1.0;
				} else if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation * 2) {
					representation[i] += 0.6;
				} else if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation * 3) {
					representation[i] += 0.4;
				} else if (b1.get(i).get(j).getMeanDistance() <= distanceDeviation * 4) {
					representation[i] += 0.2;
				} else {
					representation[i] += 0.001;
				}
			}
			representation[i] /= count;
		}
	}
	
	public void getShapeFactor(int[][] matrix) {
		points = new ArrayList<Point>();
		edgePoints = new ArrayList<Point>();
		
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] > 0) {
					points.add(new Point(i+1, j+1));

					if (i - 1 > 0 && matrix[i-1][j] == 0) {
						edgePoints.add(new Point(i+1, j+1));
					} else if (i + 1 < matrix.length && matrix[i+1][j] == 0) {
						edgePoints.add(new Point(i+1, j+1));
					} else if (j + 1 < matrix[i].length && matrix[i][j+1] == 0) {
						edgePoints.add(new Point(i+1, j+1));
					} else if (j - 1 > 0 && matrix[i][j-1] == 0) {
						edgePoints.add(new Point(i+1, j+1));
					}
				} 
			}
		}
		
		this.shapeRepresentation = points.size() / Math.pow(edgePoints.size(), 2);
	}
	
	public double compare(Feature input) {
		double error = 0.0;
		
		for (int i = 0; i < representation.length; ++i) {
			error += Math.pow(representation[i] - input.representation[i], 2);
		}
		
		return 1.0 - Math.sqrt(error);
	}
	
	public void resonate(Feature input, double learningRate) {		
		for (int i = 0; i < representation.length; ++i) {
			representation[i] += (representation[i] - input.representation[i]) * learningRate;
		}
	}
	
	public String toString() {
		DecimalFormat df1 = new DecimalFormat( "#.####" );
		String data = "|\t";
		
		for (double d : representation) {
			data += df1.format(d) + "\t";
		}
		
		return data + "|";
	}
	
	public double getAverageDistance() {
		return averageDistance;
	}
	
	public double getDistanceStandardDeviation() {
		return distanceDeviation;
	}
	
	public int getDimX() {
		return dimX;
	}
	
	public int getDimY() {
		return dimY;
	}
	
	public double getShapeRepresentation() {
		return shapeRepresentation;
	}
	
}
