package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticEdge1 {
	
	private List<Point> points, edgePoints;
	private Point[] bestEdge;
	double bestEdgeDistance;
	double[] representation;
	
	public GeneticEdge1(int[][] matrix) {
		points = new ArrayList<Point>();
		edgePoints = new ArrayList<Point>();
		this.detectEdges(matrix);
		this.geneticEdges();
		this.shapeRepresentation();
	}
	
	private void shapeRepresentation() {
		List<Point> queue = new ArrayList<>();
		queue.add(bestEdge[0]);
		bestEdge[0] = null;
		
		while (queue.size() < bestEdge.length) {
			double min = Double.MAX_VALUE;
			int minIndex = 0;
			
			for (int i = 0; i < bestEdge.length; ++i) {
				if (bestEdge[i] != null) {
					double m = queue.get(queue.size()-1).getDistance(bestEdge[i]);
					if (m < min) {
						min = m;
						minIndex = i;
					}
				}
			}
			queue.add(bestEdge[minIndex]);
			bestEdge[minIndex] = null;
		}
		
		representation = new double[queue.size()];
		
		for (int i = 0; i < queue.size(); ++i) {
			if (i+1 < queue.size()) {
				Point p1 = queue.get(i);
				Point p2 = queue.get(i+1);

				representation[i] = p1.getDistance(p2);

			} else {
				Point p1 = queue.get(i);
				Point p2 = queue.get(0);

				representation[i] = p1.getDistance(p2);
			}
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
		int candidateSize = 10, cycles = 20, populationSize = 50;
		
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
		
		bestEdgeDistance = 0;
		bestEdge = population[0];
		
		for (Point[] trip : population) {
			double d = this.candidateFitness(trip);
			
			if (d > bestEdgeDistance) {
				bestEdge = trip;
				bestEdgeDistance = d;
			}
		}
	}
	
	private Point[] generateCandidate(int size) {
		Point[] candidate = new Point[size];
		
		for (int i = 0; i < size; ++i) {
			candidate[i] = edgePoints.get(ThreadLocalRandom.current().nextInt(0, edgePoints.size()));
		}
		
		return candidate;
	}
	
	private double candidateFitness(Point[] candidate) {
		double fitness = 0.0;
		
		for (int i = 0; i < candidate.length; ++i) {
			for (int j = 0; j < candidate.length; ++j) {
				if (j == i) {
					continue;
				}
				fitness += candidate[i].getDistance(candidate[j]);
			}
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
		
		if (probability < .05) {
			int x1 = generator.nextInt(child.length);
			int x2 = generator.nextInt(edgePoints.size());
			
			child[x1] = edgePoints.get(x2);
		}
		return child;
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
