package com.finalandroidresizer.gif;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;

public class GifUtil {

	static int MAX_IMAGE_DIMENSION = 240;

	public static void gifInputToOutput(File source, File destination, int dstWidth, int dstHeight)
			throws FileNotFoundException, IOException {

		if (dstWidth < 1) {
			dstWidth = MAX_IMAGE_DIMENSION;
		}

		if (dstHeight < 1) {
			dstHeight = MAX_IMAGE_DIMENSION;
		}

		GifDecoder decoder = new GifDecoder();
		decoder.read(new FileInputStream(source));

		AnimatedGifEncoder encoder = new AnimatedGifEncoder();

		encoder.setRepeat(0);
		encoder.setQuality(1);

		FileOutputStream fileOutputStream = new FileOutputStream(destination);
		encoder.start(fileOutputStream);

		for (int i = 0; i < decoder.getFrameCount(); i++) {
			BufferedImage bufferedImage = decoder.getFrame(i);

			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			float scaleFactor = 1;
			if (width > dstWidth || height > dstHeight) {
				if (width > height) {
					scaleFactor = (float) dstHeight / height;
				} else {
					scaleFactor = (float) dstWidth / width;
				}
			}

			width = (int) (width * scaleFactor);
			height = (int) (height * scaleFactor);
			if (scaleFactor != 1) {
				Image image = bufferedImage.getScaledInstance(width, height,
						Image.SCALE_AREA_AVERAGING);
				bufferedImage.flush();
				bufferedImage = toBufferedImage(image);
				image.flush();
			}
			encoder.setTransparent(new Color(0, 0, 0, 0));
			encoder.setDispose(2);
			encoder.setSize(width, height);
			encoder.addFrame(bufferedImage);
			bufferedImage.flush();

			if (decoder.getDelay(i) == 0) {
				encoder.setDelay(100);
			} else {
				encoder.setDelay(decoder.getDelay(i));
			}
		}

		encoder.finish();
		fileOutputStream.close();
	}

	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent
		// Pixels
		boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.TRANSLUCENT;
			}

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null),
					image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics2D g = bimage.createGraphics();
		g.setRenderingHints(new RenderingHints(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR));

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		image.flush();

		return bimage;
	}

	// This method returns true if the specified image has transparent pixels
	public static boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}
}