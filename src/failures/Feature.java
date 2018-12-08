package main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bobby Palmer
 *
 */
public class Feature {
	
	private List<Point> points;
	private double meanX, meanY, xMean, yMean, averageDistance, distanceDeviation, regressionSlope, correlationCoefficient;
	private int count, dimX, dimY;
	private Point meanPoint;
	private double[] representation;

	public Feature(int[][] matrix) {
		dimX = matrix.length;
		dimY = matrix[0].length;
		points = new ArrayList<Point>();
		this.setPoints(matrix);
		
		if (count == 0) {
			averageDistance = 0;
			return;
		}
		
		this.calculateMeans();
		this.flatten();
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
		
		distanceDeviation = 0;
		
		for (Point p : points) {
			distanceDeviation += Math.pow((p.getMeanDistance() - averageDistance), 2);
		}
		
		distanceDeviation = Math.sqrt(distanceDeviation / (count-1));
	}
	
//	private void linearRegression() {
//		xMean = 0;
//		yMean = 0;
//		double xDev = 0;
//		double yDev = 0;
//		
//		for (Point p : points) {
//			xMean += p.getX();
//			yMean += p.getY();
//		}
//		
//		xMean /= points.size();
//		yMean /= points.size();
//		
//		for (Point p : points) {
//			xDev += Math.pow((p.getX()-xMean), 2);
//			yDev += Math.pow((p.getY()-yMean), 2);
//		}
//		
//		xDev = Math.sqrt(xDev / (points.size()-1));
//		yDev = Math.sqrt(yDev / (points.size()-1));
//		
//		correlationCoefficient = 0;
//		
//		for (Point p : points) {
//			correlationCoefficient += (((p.getX()-xMean)/xDev) * ((p.getY()-yMean)/yDev));
//		}
//		
//		correlationCoefficient /= (points.size()-1);
//		
//		regressionSlope = correlationCoefficient * yDev / xDev;
//	}
	
//	private void bobbycize() {		
//		double yIntercept = yMean - (xMean * regressionSlope);
//		List<List<Point>> newData1 = this.bisect(points, yIntercept);
//		
//		List<List<Point>> newData2 = new ArrayList<List<Point>>();
//		List<List<Point>> newData3 = new ArrayList<List<Point>>();
//		
//		double newSlope = -1 / regressionSlope;
//		yIntercept = yMean - (xMean * newSlope);
//		
//		for (List<Point> l : newData1) {
//			newData2 = this.bisect(l, yIntercept);
//			
//			for (List<Point> m : newData2) {
//				newData3.add(m);
//			}
//		}
//		
//		newData1.clear();
//		newData2.clear();
//		
//		newSlope = Math.abs((newSlope - 1) / 2);
//		
//		for (int i = 0; i < newData3.size(); ++i) {
//			if (i % 2 == 0) {
//				yIntercept = yMean - (xMean * newSlope);
//				
//				newData1 = this.bisect(newData3.get(i), yIntercept);
//				
//				for (List<Point> l : newData1) {
//					newData2.add(l);
//				}
//			} else {
//				double newSlope2 = -1 / newSlope;
//				yIntercept = yMean - (xMean * newSlope2);
//				
//				newData1 = this.bisect(newData3.get(i), yIntercept);
//				
//				for (List<Point> l : newData1) {
//					newData2.add(l);
//				}
//			}
//		}
//		
//		representation = new double[newData2.size()];
//		
//		for (int i = 0; i < newData2.size(); ++i) {
//			representation[i] = (double) newData2.get(i).size() / (double) points.size();
//		}
//		
//	}
//	
//	private void normalize1() {
//		List<List<Point>> data1 = this.regressionBisect(points);
//		
//		List<List<Point>> data2 = this.regressionBisect(data1.get(0));
//		List<List<Point>> data3 = this.regressionBisect(data1.get(1));
//		
//		data1.clear();
//		
//		for (int i = 0; i < data2.size(); ++i) {
//			List<List<Point>> data4 = this.regressionBisect(data2.get(i));
//			
//			for (List<Point> l : data4) {
//				data1.add(l);
//			}
//		}
//		
//		for (int i = 0; i < data3.size(); ++i) {
//			List<List<Point>> data4 = this.regressionBisect(data3.get(i));
//			
//			for (List<Point> l : data4) {
//				data1.add(l);
//			}
//		}
//		
//		representation = new double[data1.size()];
//		
//		for (int i = 0; i < data1.size(); ++i) {
//			representation[i] = (double) data1.get(i).size() / (double) points.size();
//		}
//	}
	
//	private List<List<Point>> regressionBisect(List<Point> pts) {
//		xMean = 0;
//		yMean = 0;
//		double xDev = 0;
//		double yDev = 0;
//		
//		for (Point p : pts) {
//			xMean += p.getX();
//			yMean += p.getY();
//		}
//		
//		xMean /= pts.size();
//		yMean /= pts.size();
//		
//		for (Point p : pts) {
//			xDev += Math.pow((p.getX()-xMean), 2);
//			yDev += Math.pow((p.getY()-yMean), 2);
//		}
//		
//		xDev = Math.sqrt(xDev / (pts.size()-1));
//		yDev = Math.sqrt(yDev / (pts.size()-1));
//		
//		correlationCoefficient = 0;
//		
//		for (Point p : pts) {
//			correlationCoefficient += (((p.getX()-xMean)/xDev) * ((p.getY()-yMean)/yDev));
//		}
//		
//		correlationCoefficient /= (pts.size()-1);
//		
//		regressionSlope = correlationCoefficient * yDev / xDev;
//		System.out.println(regressionSlope);
//		
//		double yIntercept = yMean - (xMean * regressionSlope);
//		return this.bisect(pts, yIntercept);
//	}
	
//	private List<List<Point>> bisect(List<Point> data, double yIntercept) {
//		List<List<Point>> newData = new ArrayList<List<Point>>();
//		List<Point> b1 = new ArrayList<Point>();
//		List<Point> b2 = new ArrayList<Point>();
//		
//		for (Point p : data) {
//			if (Utils.sideOfLine(p, meanPoint, new Point(0, yIntercept)) > 0) {
//				b1.add(p);
//			} else {
//				b2.add(p);
//			}
//		}
//		newData.add(b1);
//		newData.add(b2);
//		
//		return newData;
//	}
	
	private double[] regressionLine() {
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
		double[] line = this.regressionLine();
		
		double m = line[0];
		double b = line[1];
		
		Point p1 = new Point(0, (m * 0 + b));
		Point p2 = new Point(dimX, (m * dimX+ b));

		List<List<Point>> b1 = this.bisect(points, p1, p2);
		
		double pm = -1 / m;
		double pb = meanPoint.getY() - (pm * meanPoint.getX());
		
		p1 = new Point(0, (pm * 0 + pb));
		p2 = new Point(dimX, (pm * dimX + pb));
		
		List<List<Point>> b2 = this.bisect(b1.get(0), p1, p2);
		List<List<Point>> b3 = this.bisect(b1.get(1), p1, p2);
		b1.clear();
		
		pm = Math.abs((pm - 1) / 2);
		pb = meanPoint.getY() - (pm * meanPoint.getX());
		
		p1 = new Point(0, (pm * 0 + pb));
		p2 = new Point(dimX, (pm * dimX + pb));
		
		pm = -1 / m;
		pb = meanPoint.getY() - (pm * meanPoint.getX());
		
		Point p3 = new Point(0, (pm * 0 + pb));
		Point p4 = new Point(dimX, (pm * dimX + pb));
		
		List<List<Point>> finalPoints = new ArrayList<List<Point>>();
		
		for (int i = 0; i < b2.size(); ++i) {
			if (i < 2) {
				b1 = this.bisect(b2.get(i), p1, p2);
				
				for (List<Point> p : b1) {
					finalPoints.add(p);
				}
			} else {
				b1 = this.bisect(b2.get(i), p3, p4);
				
				for (List<Point> p : b1) {
					finalPoints.add(p);
				}
			}
		}
		
		for (int i = 0; i < b3.size(); ++i) {
			if (i < 2) {
				b1 = this.bisect(b3.get(i), p1, p2);
				
				for (List<Point> p : b1) {
					finalPoints.add(p);
				}
			} else {
				b1 = this.bisect(b3.get(i), p3, p4);
				
				for (List<Point> p : b1) {
					finalPoints.add(p);
				}
			}
		}
		
		representation = new double[finalPoints.size()];
		
		for (int i = 0; i < finalPoints.size(); ++i) {
			representation[i] = (double) finalPoints.get(i).size() / (double) points.size();
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
	
	
	public List<Point> oneStDev() {
		List<Point> pts = new ArrayList<Point>();
		
		for (Point p : points) {
			if (p.getMeanDistance() <= distanceDeviation) {
				pts.add(p);
			}
		}

		return pts;
	}
	
	public double compare(Feature input) {
		double similarity = 0;
		
		
		return similarity;
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
	
}
