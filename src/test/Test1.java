package test;

import static org.opencv.imgproc.Imgproc.INTER_AREA;
import static org.opencv.imgproc.Imgproc.resize;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Test1 {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		try {
			test1("./resources/faces");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test1(String folderPath) throws IOException {
		List<Mat> trainingSet = readTrainingFilesAsMat(folderPath);
		List<BufferedImage> classifiedSet = new ArrayList<BufferedImage>();
		
		for (Mat img : trainingSet) {
			int newWidth = (int)((double)img.width() * 0.1);
			int newHeight = (int)((double)img.height() * 0.1);
			
			Size scaleSize = new Size(newWidth, newHeight);
			resize(img, img, scaleSize , 0, 0, INTER_AREA);
			
			Imgproc.blur(img, img, new Size(3, 3));
			
			Imgproc.Canny(img, img, 100, 200, 3, false);
			
			{
				BufferedImage image = null;
				int width = img.width(), height = img.height(), channels = img.channels();
				byte[] sourcePixels = new byte[width * height * channels];
				img.get(0, 0, sourcePixels);
				
				if (img.channels() > 1) {
					image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
				} else {
					image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
				}
				
				final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
				System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
				
				classifiedSet.add(image);
			}
		}
		
		int[] vector1 = new int[classifiedSet.get(0).getWidth() * classifiedSet.get(0).getHeight()];
		int[] vector2 = new int[classifiedSet.get(1).getWidth() * classifiedSet.get(1).getHeight()];
		
		int index1 = 0;
		for (int i = 0; i < classifiedSet.get(0).getHeight(); ++i) {
			for (int j = 0; j < classifiedSet.get(0).getWidth(); ++j) {
				int pixel = classifiedSet.get(0).getRGB(j, i) & 0xFF;
				
				if (pixel > 0) {
					vector1[index1] = 1;
				}
				System.out.println(vector1[index1]);
				index1++;
			}
		}
		
		int index2 = 0;
		for (int i = 0; i < classifiedSet.get(1).getHeight(); ++i) {
			for (int j = 0; j < classifiedSet.get(1).getWidth(); ++j) {
				vector2[index2++] = classifiedSet.get(1).getRGB(j, i) & 0xFF;
			}
		}
		
		failures.Utils.avgDistanceDifferenceTest(vector1, vector1);
		
		int count = 0;
		for (BufferedImage img : classifiedSet) {
			File outputfile = new File("./resources/classified/" + count++ + ".png");
			ImageIO.write(img, "png", outputfile);
		}
		
	}
	
	/**
	 * WARNING! Method only works on a directory containing
	 * exclusively image file. Will throw exceptions in all other cases!
	 * @param folderPath: Path to directory of images.
	 * @return List of buffered images.
	 * @throws IOException 
	 */
	public static List<BufferedImage> readTrainingFiles(String folderPath) throws IOException {
		List<BufferedImage> trainingSet = new ArrayList<BufferedImage>();
		
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; ++i) {
			if (listOfFiles[i].isFile()) {
				trainingSet.add(ImageIO.read(listOfFiles[i]));
			}
		}
		
		return trainingSet;
	}
	
	public static List<Mat> readTrainingFilesAsMat(String folderPath) throws IOException {
		List<Mat> trainingSet = new ArrayList<Mat>();
		
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; ++i) {
			if (listOfFiles[i].isFile()) {
				trainingSet.add(Imgcodecs.imread(listOfFiles[i].toString(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE));
			}
		}
		
		return trainingSet;
	}
	
}
