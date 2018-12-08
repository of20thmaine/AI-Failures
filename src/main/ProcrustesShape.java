package main;

import java.util.ArrayList;
import java.util.List;

public class ProcrustesShape {
	
	private List<Point> points;
	private Point mean;
	private int[] vector;
	
	public ProcrustesShape(int[][] matrix) {
		this.getPoints(matrix);
		this.translate();
		this.setIntVector();
	}
	
	public void getPoints(int[][] matrix) {
		points = new ArrayList<Point>();
		double meanX = 0.0, meanY = 0.0;
		
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] > 0) {
					points.add(new Point(i+1, j+1));
					meanX += (i+1);
					meanY += (j+1);
				}
			}
		}
		mean = new Point(meanX / points.size(), meanY / points.size());
	}
	
	
	
	
	
	
	
	
	public void translate() {
		double s = 0;
		
		for (Point p : points) {
			p.translate(mean);
			s += Math.pow(p.getX() - mean.getX(), 2);
			s += Math.pow(p.getY() - mean.getY(), 2);
		}
		
		s = Math.sqrt(s / points.size());
		
		for (Point p : points) {
			p.scale(mean, s);
			p.setMeanDistance(mean);
			System.out.println(p.getMeanDistance());
		}
	}
	
	public void setIntVector() {
		double max = 1.0/10.0;
		double min = 0.0;
		
		vector = new int[10];
		
		for (int i = 0; i < vector.length; ++i) {
			for (Point p : points) {
				if (p.getMeanDistance() >= min && p.getMeanDistance() <= max) {
					vector[i]++;
				}
			}
			min = max;
			max += 1.0/10.0;
		}	
	}
	
	public double compare(ProcrustesShape input) {
		double error = 0.0;
		
		for (int i = 0; i < vector.length; ++i) {
			error += Math.pow(vector[i] - input.vector[i], 2);
		}
		
		return 1.0 - Math.sqrt(error);
	}
	
	public String toString() {
		String data = "|\t";
		
		for (int i : vector) {
			data += vector[i] + "\t";
		}
		
		return data + "|";
	}
	

}
