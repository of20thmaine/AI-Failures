package main;

import java.util.ArrayList;
import java.util.List;

public class EdgeBisector {
	
	private List<Point> points, edgePoints;
	double[] representation;
	
	public EdgeBisector(int[][] matrix) {
		points = new ArrayList<Point>();
		edgePoints = new ArrayList<Point>();
		this.detectEdges(matrix);
	}
	
	private void detectEdges(int[][] matrix) {
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
	}
	
}
