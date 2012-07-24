/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

	public static final String JPG = "jpg";
	public static final String GIF = "gif";
	public static final String PNG = "png";
	public static final String BMP = "bmp";
	public static final String WBMP = "wbmp";

	public static BufferedImage getImageByResource(String url) {

		try {
			return ImageIO.read(ImageUtils.class.getResource(url));
		} catch (Exception e) {
			return null;
		}
	}

	public static BufferedImage getImage(String url) {
		try {
			return ImageIO.read(new File(url));
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isReadable(String url) {

		File file = new File(url);

		if (file.exists() && file.canRead()) {
			return true;
		}
		return false;

	}

	public static BufferedImage getImage(InputStream inputStream) {

		try {
			return ImageIO.read(inputStream);
		} catch (Exception e) {
			return null;
		}
	}

	public static BufferedImage scaleImage(BufferedImage image, double xFactor, double yFactor) {

		if (image != null) {
			return scale(image, xFactor, yFactor);
		}
		return null;

	}

	public static BufferedImage scaleImage(BufferedImage image, int maxHeight, int maxWidth, int quality, int imageType) {
		
		if (image.getWidth() > maxWidth && image.getHeight() > maxHeight) {

			double aspectRatio = (double) image.getWidth() / (double) image.getHeight();

			if (aspectRatio >= 1) {
				
				return scaleImageByWidth(image, maxWidth, quality, imageType);
				
			} else {
				
				return scaleImageByHeight(image, maxHeight, quality, imageType);
			}

		} else if (image.getWidth() > maxWidth) {
			
			return scaleImageByWidth(image, maxWidth, quality, imageType);
			
		} else if (image.getHeight() > maxHeight) {
			
			return scaleImageByHeight(image, maxHeight, quality, imageType);
			
		} else if (image.getType() != imageType){	
		
			return scale(image, image.getHeight(), image.getWidth(), quality, imageType);
			
		} else {
			
			return image;
		}
	}

	public static BufferedImage scaleImageByWidth(BufferedImage image, int maxWidth, int quality, int imageType) {

		double scale;

		if (image.getWidth() > image.getHeight()) {
			scale = (double) maxWidth / (double) image.getWidth();
		} else {
			scale = (double) maxWidth / (double) image.getHeight();
		}

		int scaledW = (int) (scale * image.getWidth());
		int scaledH = (int) (scale * image.getHeight());

		scaledW = checkSize(scaledW);
		scaledH = checkSize(scaledH);

		return scale(image, scaledH, scaledW, quality, imageType);
	}

	private static int checkSize(int value) {

		if (value < 1) {
			return 1;
		} else {
			return value;
		}
	}

	public static BufferedImage scaleImageByHeight(BufferedImage image, int maxHeight, int quality, int imageType) {

		double scale;

		if (image.getHeight() > image.getWidth()) {
			scale = (double) maxHeight / (double) image.getHeight();
		} else {
			scale = (double) maxHeight / (double) image.getWidth();
		}

		int scaledW = (int) (scale * image.getWidth());
		int scaledH = (int) (scale * image.getHeight());

		scaledW = checkSize(scaledW);
		scaledH = checkSize(scaledH);

		return scale(image, scaledH, scaledW, quality, imageType);
	}

	public static void writeImage(BufferedImage image, String url, String format) throws IOException, NullPointerException {

		// write image to file
		if (!url.endsWith("." + format)) {
			url += "." + format;
		}

		File outputfile = new File(url);
		if (image != null) {
			ImageIO.write(image, format, outputfile);
		}

	}

	private static BufferedImage scale(BufferedImage image, double xFactor, double yFactor) {

		// scale image based on factor x and y
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(xFactor, yFactor);

		BufferedImage result = new BufferedImage((int) (image.getWidth() * xFactor), (int) (image.getHeight() * yFactor), BufferedImage.TYPE_INT_RGB);

		Graphics2D g2 = (Graphics2D) result.getGraphics();

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, result.getWidth(), result.getHeight());
		
		g2.drawImage(image, scaleTransform, null);

		return result;
	}

	private static BufferedImage scale(BufferedImage image, int height, int width, int quality, int imageType) {

		BufferedImage result = new BufferedImage(width, height, imageType);

		Graphics2D g2 = (Graphics2D) result.getGraphics();
		
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, height);
		
		Canvas canvas = new Canvas();
		Image tImage = image.getScaledInstance(width, height, quality);

		g2.drawImage(tImage, 0, 0, canvas);

		return result;
	}

	/*public static void scaleAndWriteImage(String inImgURL, String outImgURL, String format, double xFactor, double yFactor ) throws IOException{

		// scale and write the scaled image to file
		BufferedImage image = getImage(inImgURL);

		// changed
		final int MaxDim = 120;
		int thumb_width = image.getWidth();
		int thumb_height = image.getHeight();
		int b = thumb_height > thumb_width ? thumb_height : image.getWidth();
		double per = (b > MaxDim) ? (MaxDim * 1.0) / b : 1.0;
		thumb_height = (int)(thumb_height * per);
	    thumb_width = (int)(thumb_width * per);
		////

		BufferedImage scImage = null;
		if(image != null){
			//scImage = scale(image, xFactor, yFactor);
			scImage = scale(image, thumb_height, thumb_width);
		}
		else
			throw new IOException();
		writeImage(scImage, outImgURL, format);


	}*/
}
