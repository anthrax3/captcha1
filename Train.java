import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Train {

	public static Map<BufferedImage, String> loadTrainingSet(String category) throws Exception {
		Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
		File dir = new File("trainSet/" + category);
		// select all jpg picture files
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg");
			}
		});
		for (File file : files) {
			// put key: image			value: change name of number to a String
			map.put(ImageIO.read(file), file.getName().charAt(0) + "");
		}
		return map;
	}
	
	public static void scaleTraindata(String category, int threshold) throws Exception {
		File dir = new File("trainSet/" + category);
		File dataFile = new File("trainSet/" + category + "/data.txt");
		FileOutputStream fs = new FileOutputStream(dataFile);
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg");
			}
		});
		for (final File file : files) {
			//final 
		}
	}
	
}
