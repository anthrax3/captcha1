

import java.awt.Color;
import java.awt.color.*;
import java.awt.image.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.*;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.jhlabs.image.ScaleFilter;

public class ImgProc {
	public static void downloadImg(String url, String Category) {
		final HttpClient httpClient = HttpClientBuilder.create().build();

		
		for (int i = 0; i < 20; i++) {
			try {
				final HttpGet httpGet = new HttpGet(url);
			    //httpGet.setHeader();
				
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int isWhite(int intColor, int whiteThreshold) {
		Color color = new Color(intColor);
		if (color.getRed() + color.getBlue() + color.getGreen() > whiteThreshold) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public static int isBlack(int intColor, int whiteThreshold) {
		Color color = new Color(intColor);
		if (color.getRed() + color.getBlue() + color.getGreen() <= whiteThreshold) {
			return 1;
		} else {
			return 0;
		}
	}
	/*
	 * removeBlank: cut the blank vertically, after the split process
	 * 
	 *  white: to pick white words, remove black blanks use white = 1;
	 *         to pick black words, remove white blanks use white = 0;
	 */
	public static BufferedImage removeBlank(BufferedImage img, 
											int whiteThreshold,
											int white) throws Exception {
		int width = img.getWidth();
		int height = img.getHeight();
		// the start and end without blank
		int start = -1;
		int end = -1;
		// get the start, from top to bottom
		for (int j = 0; j < height; ++j) {
			for (int i = 0; i < width; ++i) {
				if (isWhite(img.getRGB(i, j), whiteThreshold) == white) {
					start = j;
					break;
				}
			}
			if (start != -1) {
				break;
			}
		}
		// get the end, from the bottom to top
		for (int j = height - 1; j >= 0; --j) {
			for (int i = 0; i < width; ++i) {
				if (isWhite(img.getRGB(i, j), whiteThreshold) == white) {
					end = j;
					break;
				}
			}
			if (end != -1) {
				break;
			}
		}
		return img.getSubimage(0, start, width, end - start + 1);
	}
	// remove the background of the file
	// this removeBg only for pic without interfere
	public static BufferedImage removeBG(String fileName, int whiteThreshold) throws Exception {
		BufferedImage img = ImageIO.read(new File(fileName));
		int width = img.getWidth();
		int height = img.getHeight();
		System.out.print(width);
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				//System.out.println(isWhite(img.getRGB(i, j), whiteThreshold));
				if (isWhite(img.getRGB(i, j), whiteThreshold) == 1) {
					img.setRGB(i, j, Color.WHITE.getRGB());
				} else {
					img.setRGB(i, j, Color.BLACK.getRGB());
				}
			}
		}
		return img;
	}
	// removeBG1 for pic with interfere
	// the method is that count the pixels of different color and get the major one
	public static BufferedImage removeBG1(String fileName) throws Exception {
		BufferedImage img = ImageIO.read(new File(fileName));
		img = img.getSubimage(1, 1, img.getWidth() - 2, img.getHeight() - 2);
		// the number of letters
		int num = 5;
		int width = img.getWidth();
		int height = img.getHeight();
		// the 5 is the number for this captcha, you can change
		double subWidth = (double)width / num;
		for (int i = 0; i < num; i++) {
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			for (int x = (int)(subWidth * i); x < (1 + i) * subWidth && x < width - 1; ++x) {
				for (int y = 0; y < height; ++y) {
					int color = img.getRGB(x, y);
					// if white don't count
					if (isWhite(color, 100) == 1) {
						continue;
					}
					// if other color
					if (!map.containsKey(color)) {
						map.put(color, 1);
					} else {
						map.put(color, map.get(color) + 1);
					}
				}

			}
			// get max color
			int max = -1;
			int maxColor = -1;
			for (Integer color : map.keySet()) {
				if (map.get(color) > max) {
					max = map.get(color);
					maxColor = color;
				}
			}
			// set max color to BLACK, others to White
			for (int x = (int)(subWidth * i); x < (1 + i) * subWidth && x < width - 1; ++x) {
				for (int y = 0; y < height; ++y) {
					if (img.getRGB(x, y) != maxColor) {
						img.setRGB(x, y, Color.WHITE.getRGB());
					} else {
						img.setRGB(x, y, Color.BLACK.getRGB());
					}
				}
			}
		}
		return img;
	}
	

	public static void main(String[] args) {
		System.out.println("this is main");
	}
}
