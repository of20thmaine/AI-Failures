package test;

import main.GeneticEdge;
import mnist.MnistDataReader;
import mnist.MnistMatrix;

public class GeneticTest {

	public static void main(String[] args) {
		test1();
	}
	
	public static void test1() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			GeneticEdge[] features = new GeneticEdge[mnistMatrix.length];
			GeneticEdge[] clusters = new GeneticEdge[10];
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new GeneticEdge(mnistMatrix[i].getData());
				System.out.println(mnistMatrix[i].getLabel() + "\t" + features[i]);
			}
			
			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte", "./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			double accuracy = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
//				features[i] = new GeneticEdge(mnistMatrix[i].getData());
//				
//				double max = -1;
//				int label = 0;
//				
//				if (mnistMatrix[i].getLabel() == label) {
//					accuracy += 1;
//				}
			}
			
			System.out.println("MNIST Training Accuracy: " + accuracy / mnistMatrix.length);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
