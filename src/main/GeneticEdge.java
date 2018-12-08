package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticEdge {
	
	private List<Point> points, edgePoints;
	private Point[] bestEdge;
	double bestEdgeDistance;
	double[] representation;
	Point meanPoint;
	
	public GeneticEdge(int[][] matrix) {
		points = new ArrayList<Point>();
		edgePoints = new ArrayList<Point>();
		this.detectEdges(matrix);
		this.geneticEdges();
		this.shapeRepresentation();
	}
	
	private void shapeRepresentation() {
		representation = new double[bestEdge.length];
		
		for (int i = 0; i < points.size(); ++i) {
			double d = Double.MAX_VALUE;
			int best = 0;
			
			for (int j = 0; j < bestEdge.length; ++j) {
				double dT = points.get(i).getDistance(bestEdge[j]);
				if (dT < d) {
					d = dT;
					best = j;
				}
			}
			representation[best]++;
		}
		
		for (int i = 0; i < representation.length; ++i) {
			representation[i] /= points.size();
		}
		
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
	
	private void geneticEdges() {
		// Adjustable Parameters
		int candidateSize = 10, cycles = 100, populationSize = 100;
		bestEdge = new Point[candidateSize];
		bestEdgeDistance = 0;
		
		Point[][] population = new Point[populationSize][candidateSize];
		
		for (int i = 0; i < population.length; ++i) {
			population[i] = this.generateCandidate(candidateSize);
		}
		
		while (cycles-- > 0) {
			Point[][] newPopulation = new Point[populationSize][candidateSize];
			
			for (int i = 0; i < population.length; ++i) {
				Point[] parent1 = this.tournamentSelection(population);
				Point[] parent2 = this.tournamentSelection(population);
				
				Point[] child = this.crossover(parent1, parent2);
				child = this.mutate(child);
				
				newPopulation[i] = child;
			}
			population = newPopulation;
		}
		
		for (Point[] trip : population) {
			this.candidateFitness(trip);
		}
	}
	
	private Point[] generateCandidate(int size) {
		Point[] candidate = new Point[size];
		
		for (int i = 0; i < size; ++i) {
			Point p = edgePoints.get(ThreadLocalRandom.current().nextInt(0, edgePoints.size()));
			
			while (this.isIn(candidate, p)) {
				p = edgePoints.get(ThreadLocalRandom.current().nextInt(0, edgePoints.size()));
			}
			candidate[i] = p;
		}
		
		return candidate;
	}
	
	private boolean isIn(Point[] candidate, Point p) {
		for (Point c : candidate) {
			if (c != null && c.getX() == p.getX() && c.getY() == p.getY()) {
				return true;
			}
		}
		return false;
	}
	
	private double candidateFitness(Point[] candidate) {
		double fitness = 0.0;
		
		for (int i = 0; i < candidate.length; ++i) {
			if (i + 1 < candidate.length) {
				fitness += candidate[i].getDistance(candidate[i+1]);
			} else {
				fitness += candidate[i].getDistance(candidate[0]);
			}
		}
		fitness /= candidate.length;
		
		if (fitness > bestEdgeDistance) {
			bestEdgeDistance = fitness;
			bestEdge = candidate;
		}
		return fitness;
	}
	
	private Point[] tournamentSelection(Point[][] population) {
		int r1 = ThreadLocalRandom.current().nextInt(0, population.length);
		int r2 = ThreadLocalRandom.current().nextInt(0, population.length);
		
		if (this.candidateFitness(population[r1]) > this.candidateFitness(population[r2])) {
			return population[r1];
		} else {
			return population[r2];
		}
	}
	
	private Point[] crossover(Point[] parent1, Point[] parent2) {
		Point[] child = new Point[parent1.length];
		
		int startPos = ThreadLocalRandom.current().nextInt(0, parent1.length);
		int endPos = ThreadLocalRandom.current().nextInt(0, parent1.length);
		
		if (endPos < startPos) {
			int swap = startPos;
			startPos = endPos;
			endPos = swap;
		}
		
		for (int i = 0; i < child.length; ++i) {
			if (i == startPos) {
				for (int j = startPos; j <= endPos; ++j) {
					child[j] = parent1[j];
				}
				i = endPos;
			} else {
				child[i] = null;
			}
		}
		
		for (int i = 0; i < child.length; ++i) {
			if (child[i] == null) {
				child[i] = parent2[i];
			}
		}

		return child;
	}
	
	private Point[] mutate(Point[] child) {
		Random generator = new Random();
		double probability = generator.nextDouble();
		
		if (probability < 0.1) {
			int x1 = generator.nextInt(child.length);
			int x2 = generator.nextInt(child.length);
			
			Point swap = child[x1];
			child[x1] = child[x2];
			child[x2] = swap;
		}
		return child;
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
	
	public Point[] getBestEdge() {
		return bestEdge;
	}
	
	public double getBestEdgeDistance() {
		return bestEdgeDistance;
	}
	
	public String toString() {
		String s = "|\t";
		
		for (double d : representation) {
			s += d + "\t";
		}
		
		return s + "|\n";
	}

}
