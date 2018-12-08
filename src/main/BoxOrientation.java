package main;

import java.util.ArrayList;
import java.util.List;

public class BoxOrientation {
	
	private List<Point> points;
	private Point box1, box2, box3, box4;
	
	public BoxOrientation(int[][] matrix) {
		points = new ArrayList<Point>();
		this.boxData(matrix);
	}
	
	private void boxData(int[][] matrix) {
		int i1 = 0, i2 = 0, j1 = 0, j2 = 0;
		
		outer:
			for (int i = 0; i < matrix.length; ++i) {
				for (int j = 0; j < matrix[i].length; ++j) {
					if (matrix[i][j] > 0) {
						i1 = i+1;
						break outer;
					}
				}
			}
		
		outer:
			for (int i = matrix.length-1; i >= 0; --i) {
				for (int j = 0; j < matrix[i].length; ++j) {
					if (matrix[i][j] > 0) {
						i2 = i+1;
						break outer;
					}
				}
			}
			
		outer:
			for (int i = 0; i < matrix[0].length; ++i) {
				for (int j = 0; j < matrix.length; ++j) {
					if (matrix[j][i] > 0) {
						j1 = i+1;
						break outer;
					}
				}
			}
			
		outer:
			for (int i = matrix[0].length-1; i >= 0; --i) {
				for (int j = 0; j < matrix.length; ++j) {
					if (matrix[j][i] > 0) {
						j2 = i+1;
						break outer;
					}
				}
			}
			
		box1 = new Point(i1, j1);
		box2 = new Point(i1, j2);
		box3 = new Point(i2, j1);
		box4 = new Point(i2, j2);
		
		for (int i = i1-1; i < i2-1; ++i) {
			for (int j = j1-1; j < j2-1; ++j) {
				if (matrix[i][j] > 0) {
					points.add(new Point(i+1, j+1));
				}
			}
		}
		
		System.out.println(points.size() + "\t" + box1.getDistance(box2) + "\t" + box1.getDistance(box3));
		
		
	}
}
