package failures;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.Point;

public class Utils {
	
	public static void avgDistanceDifferenceTest(int[] feature, int[] cluster) {
		int[] featureData = vectorData(feature);
		int[] clusterData = vectorData(cluster);
		
		int meanDifference = Math.abs(featureData[0] - clusterData[0]);
		
		int[][] distances = new int[featureData[1]][clusterData[1]];
		
		System.out.println(featureData[1] + "\t" + clusterData[1]);
		
		int iIndex = 0;
		for (int i = 0; i < feature.length; ++i) {
			if (feature[i] != 0) {
				int jIndex = 0;
				for (int j = 0; j < cluster.length; ++j) {
					if (cluster[j] != 0) {
						distances[iIndex][jIndex++] = Math.abs(meanDifference - Math.abs((i+1) - (j+1)));
					}
				}
				iIndex++;
			}
		}
		
//		for (int i = 0; i < distances.length; ++i) {
//			System.out.print("|");
//			for (int j = 0; j < distances[i].length; ++j) {
//				System.out.print("\t" + distances[i][j] + "\t");
//			}
//			System.out.print("|\n");
//		}
		
	}
	
	public static int[] vectorData(int[] vector) {
		int mean = 0;
		int count = 0;
		
		for (int i = 0; i < vector.length; ++i) {
			if (vector[i] != 0) {
				mean += (i+1);
				count++;
			}
		}
		
		return new int[] {mean / count, count};
	}
	
	
	
	public static void distanceDifference(int[][] feature, int[][] cluster) {
		double[] fData = getData(feature);
		double[] cData = getData(cluster);
		
		double avgDistance = distance(fData[0], fData[1], cData[0], cData[1]);
		double[][] distances = new double[(int) fData[2]][(int) cData[2]];
		
		int index1 = 0;
		for (int i = 0; i < feature.length; ++i) {
			for (int j = 0; j < feature[i].length; ++j) {
				if (feature[i][j] > 0) {
					
					int index2 = 0;
					for (int k = 0; k < cluster.length; ++k) {
						for (int l = 0; l < cluster[k].length; ++l) {
							if (cluster[k][l] > 0) {
								distances[index1][index2++] = Math.abs(avgDistance - distance(i+1, j+1, k+1, l+1));
							}
						}
					}
					index1++;
				}
			}
		}
		double sim = 0;
		NumberFormat formatter = new DecimalFormat("#0.00");  
		
		for (int i = 0; i < distances.length; ++i) {
			System.out.print("|\t");
			double rowMin = distances[i][0];
			for (int j = 0; j < distances[i].length; ++j) {
				if (rowMin > distances[i][j]) {
					rowMin = distances[i][j];
				}
				System.out.print(formatter.format(distances[i][j]) + "\t");
			}
			sim += rowMin;
			System.out.print("|\n");
		}
		
		System.out.println("Average Distance = " + formatter.format(avgDistance));
		System.out.println("Similarity = " + formatter.format((sim / (double)distances.length)) + "\n");
	}
	
	public static double[] getData(int[][] matrix) {
		double[] data = new double[3];
		
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] > 0) {
					data[0] += i+1;
					data[1] += j+1;
					data[2] += 1.0;
				}
			}
		}
		
		data[0] /= data[2];
		data[1] /= data[2];
		
		return data;
	}
	
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
	}
	
	public static int sideOfLine(Point p0, Point p1, Point p2) {
		double d = ((p0.getX()-p1.getX()) * (p2.getY()-p1.getY())) - 
					((p0.getY()-p1.getY()) * (p2.getX()-p1.getX()));
		
		if (d > 0) {
			return 1;
		} else if (d < 0) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public static List<Point> getFeature(int[][] matrix) {
		ArrayList<Point> data = new ArrayList<Point>();
		
		double meanX = 0; double meanY = 0;
		int count = 0;
		
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] > 0) {
					meanX += (i+1);
					meanY += (j+1);
					count++;
					data.add(new Point(i+1, j+1));
				}
			}
		}
		
		if (count > 0) {
			meanX /= count;
			meanY /= count;
			
			for (Point p : data) {
				p.setMeanDistance(meanX, meanY);
			}
			
			Collections.sort(data, new Comparator<Point>() {
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
			
			double avg = 0.0;
			for (Point p : data) {
				avg += p.getMeanDistance();
			}
			avg /= data.size();
			
			for (Point p : data) {
//				p.normalize(avg, meanX, meanY);
			}
		}
		data.add(new Point(meanX, meanY));
		return data;
	}
	
	public static void distanceDifference2(int[][] feature, int[][] cluster) {
		List<Point> v1 = getFeature(feature);
		List<Point> v2 = getFeature(cluster);
		
		Point m1 = v1.remove(v1.size()-1);
		Point m2 = v2.remove(v2.size()-1);
		
		double avgDistance = distance(m1.getX(), m1.getY(), m2.getX(), m2.getY());
		
		double[][] distances = new double[v1.size()][v2.size()];
		
//		for (int i = 0; i < v1.size(); ++i) {
//			for (int j = 0; j < v2.size(); ++j) {
//				distances[i][j] = Math.abs(avgDistance - distance(v1.get(i).getX(),
//																  v1.get(i).getY(),
//																  v2.get(j).getX(),
//																  v2.get(j).getY())); 
//			}
//		}
		
		for (int i = 0; i < v1.size(); ++i) {
			for (int j = 0; j < v2.size(); ++j) {
//				distances[i][j] = Math.abs(avgDistance - distance(v1.get(i).getX(),
//						  										  v1.get(i).getY(),
//						  										  v2.get(j).getX(),
//						  										  v2.get(j).getY()));
				distances[i][j] = Math.abs(v1.get(i).getMeanDistance() - v2.get(j).getMeanDistance());
			}
		}
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		for (int i = 0; i < distances.length; ++i) {
			System.out.print("|\t");
			for (int j = 0; j < distances[i].length; ++j) {
				System.out.print(formatter.format(distances[i][j]) + "\t");
			}
			System.out.print("|\n");
		}
		
		System.out.println("Average Distance = " + formatter.format(avgDistance));
	}
	
	public static void distanceDifference3(int[][] feature, int[][] cluster) {
		List<Point> v1 = getFeature(feature);
		List<Point> v2 = getFeature(cluster);
	}

}



