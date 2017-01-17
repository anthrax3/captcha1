import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;



//import org.apache.commons.io.IOUtils;


public class Captcha
{
	private static String cName = "Ocr1";
	private static int WHITE_THRES = 100;
	private static List<BufferedImage> splitImage(BufferedImage img) throws Exception {
		List<BufferedImage> res = new ArrayList<BufferedImage>();
		/*
		// naive split
		res.add(img.getSubimage(10, 6, 8, 10));
		res.add(img.getSubimage(19, 6, 8, 10));
		res.add(img.getSubimage(28, 6, 8, 10));
		res.add(img.getSubimage(37, 6, 8, 10));
		*/ 
		// detection split
		int height = img.getHeight();
		int width = img.getWidth();
		List<Integer> weightList = new ArrayList<Integer>();
		for (int x = 0; x < width; ++x) {
			int count = 0;
			for (int y = 0; y < height; ++y) {
				if (ImgProc.isWhite(img.getRGB(x, y), WHITE_THRES) == 0) {
					count++;
				}
			}
			//System.out.println(count);
			weightList.add(count);
		}
		for (int i = 0; i < weightList.size(); i++) {
			int length = 0;
			while (i < weightList.size() && weightList.get(i) > 0 ) {
				length++;
				i++;
			}
			if (length > 2) {
				res.add(ImgProc.removeBlank(img.getSubimage(i - length, 0, length, height), WHITE_THRES, 0));
			}
		}
		return res;
	}
	
	private static String singleOCR(BufferedImage img, Map<BufferedImage, String> map) {
		String res = "";
		int width = img.getWidth();
		int height = img.getHeight();
		int min = width * height;
		for (BufferedImage bi : map.keySet()) {
			int count = 0;
			Label1: for (int x = 0; x < width; ++x) {
				for (int y = 0; y < height; ++y) {
					if (img.getRGB(x, y) != bi.getRGB(x, y)) {
						count++;
						if (count >= min) {
							break Label1;
						}
					}
				}
			}
			// 
			if (count < min) {
				min = count;
				res = map.get(bi);
			}
		}
		return res;
	}
	
	// OCR: optical character recognition
	// get the result of the whole OCR process
	public static String allOCR(String file) throws Exception {
		BufferedImage img = ImgProc.removeBG(file, WHITE_THRES);
		List<BufferedImage> list = splitImage(img);
		Map<BufferedImage, String> map = Train.loadTrainingSet(cName);
		String result = "";
		for (BufferedImage bIm : list) {
			result += singleOCR(bIm, map);
		}
		// save the image and rename with the result
		ImageIO.write(img, "JPG", new File("tst/OCR/" + result + ".jpg"));
		return result;
	}
	
	public static void main(String[] args) throws Exception
	{
	   
		new File("result/" + "Ocr1").mkdirs();

		// %%1 the test for image processing: 
		new File("tst/RemoveBG").mkdirs();
		new File("tst/Split").mkdirs();
		new File("tst/SingleOCR").mkdirs();
		int count = 0;
		String fileName = "img/Ocr1/2.jpg";
		// 1. test read image: OK
		BufferedImage img = ImageIO.read(new File(fileName));
		// 2. test removeBG: OK
		img = ImgProc.removeBG(fileName, WHITE_THRES);
		ImageIO.write(img, "JPG", new File("tst/RemoveBG/"+ Integer.toString(count)+ ".jpg"));
		// 3. test SplitImega & removeBlank: OK
		List<BufferedImage> list = splitImage(img);
	    for (BufferedImage bi : list) {
   		   ImageIO.write(bi, "JPG", new File("tst/Split/"+ Integer.toString(count)+ ".jpg"));
		   count++;
		}
	    /*
	    // %%2 test for recognition
	    // 0. load the TrainData (init the map for simple solution)
	    Map<BufferedImage, String> map = Train.loadTrainingSet(cName);
	    // 1. test the singleOCR
	    count = 0;
	    for (BufferedImage bi : list) {
		    String res = singleOCR(bi, map);
		    ImageIO.write(bi, "JPG", new File("tst/SingleOCR/"+ Integer.toString(count)+
				   "_" + res + ".jpg"));
		    count++;
	    }
	    // 2. test the all OCR
	    count = 0;
		for (int i = 0; i < 30; ++i) {
			System.out.println("number " + i + " processing");
			final String text = allOCR("img/" + cName + "/" + i + ".jpg");
			System.out.println(i + ".jpg = " + text);
		}	   
   */
	}
}