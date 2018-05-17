package com.finalandroidresizer;/*
 *
 
 Copyright (c) 2014, Sebastian Breit
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

3. Neither the name of the <ORGANIZATION> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 
 * 
 * */


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.finalandroidresizer.gif.GifUtil;
import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import org.apache.commons.io.FilenameUtils;

public class ImageProcessor {

	private static final float LDPI_RATIO=3;
	private static final float MDPI_RATIO=4;
	private static final float TVDPI_RATIO=5.33333333f;
	private static final float HDPI_RATIO=6;
	private static final float XHDPI_RATIO=8;
	private static final float XXHDPI_RATIO=12;
	private static final float XXXHDPI_RATIO=16;

	public enum Devices {
		ANDROID, IOS
	}

	public enum Sizes {

		AT1X("1x", MDPI_RATIO, Devices.IOS),
		AT2X("2x", XHDPI_RATIO, Devices.IOS),
		AT3x("3x", XXHDPI_RATIO, Devices.IOS),
		LDPI("ldpi", LDPI_RATIO, Devices.ANDROID),
		MDPI("mdpi", MDPI_RATIO, Devices.ANDROID),
		TVDPI("tvdpi", TVDPI_RATIO, Devices.ANDROID),
		HDPI("hdpi", HDPI_RATIO, Devices.ANDROID),
		XHDPI("xhdpi", XHDPI_RATIO, Devices.ANDROID),
		XXHDPI("xxhdpi", XXHDPI_RATIO, Devices.ANDROID),
		XXXHDPI("xxxhdpi", XXXHDPI_RATIO, Devices.ANDROID);

		private final String size;
		private final float ratio;
		private final Devices device;

		Sizes(String size, float ratio, Devices device) {
			this.size = size;
			this.ratio = ratio;
			this.device = device;
		}

		public String getSize() {
			return size;
		}

		public boolean isIOS() {
			return device.equals(Devices.IOS);
		}

		public boolean isAndroid() {
			return device.equals(Devices.ANDROID);
		}

		public Devices getDevice() {
			return device;
		}

		@Override
		public String toString() {
			return size;
		}
	}
	

	public static File processImage(File f, File resDirectory, Sizes originalSize, String drawableDirectory, boolean overwrite,
			Sizes destSize) throws FileAlreadyExistsException, IOException, NullPointerException {

		if (destSize.isIOS()) {
			return processIOSImage(f, resDirectory, originalSize, overwrite, destSize);
		} else {
			return processAndroidImage(f, resDirectory, originalSize, drawableDirectory, overwrite, destSize);
		}
	}

	private static File processAndroidImage(File f, File resDirectory, Sizes originalSize, String drawableDirectory, boolean overwrite,
											Sizes destSize) throws FileAlreadyExistsException, IOException, NullPointerException {

		String finalPath;

		if(drawableDirectory.equalsIgnoreCase("mipmap")) {
			finalPath = resDirectory.getAbsolutePath() + "/mipmap-" + destSize.size + "/" + f.getName();
		}else{
			finalPath = resDirectory.getAbsolutePath() + "/drawable-" + destSize.size + "/" + f.getName();
		}

		return writeScaledImage(f, finalPath, originalSize, overwrite, destSize);
	}

	private static File processIOSImage(File f, File resDirectory, Sizes originalSize, boolean overwrite,
											Sizes destSize) throws FileAlreadyExistsException, IOException, NullPointerException {

		String fileName = FilenameUtils.getBaseName(f.getName());

		String finalPath = resDirectory.getAbsolutePath() + "/" + fileName + ".imageset/" + fileName + "@" + destSize.size + "." + FilenameUtils.getExtension(f.getName());

		return writeScaledImage(f, finalPath, originalSize, overwrite, destSize);
	}

	private static File writeScaledImage(File f, String finalPath, Sizes originalSize, boolean overwrite,
										 Sizes destSize) throws FileAlreadyExistsException, IOException, NullPointerException {
		File destFile=new File(finalPath);
		if(!overwrite) {
			if (destFile.exists())
				throw new FileAlreadyExistsException();
		}

		destFile.getParentFile().mkdirs();

		BufferedImage image = ImageIO.read(f);

		int size = getRequiredSize(originalSize, destSize, image.getWidth());

		if (FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("gif")) {
			GifUtil.gifInputToOutput(f, destFile, size, (size * image.getHeight() / image.getWidth()));
		} else {

			ResampleOp resampleOp = new ResampleOp(size, (size * image.getHeight())
					/ image.getWidth());
			resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
			resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.None);
			image = resampleOp.filter(image, null);

			ImageIO.write(image, FilenameUtils.getExtension(f.getName()), destFile);
		}

		return destFile;
	}

	private static int getRequiredSize(Sizes originalSize, Sizes destSize,
			int width) {

		return Math.round(((float) width) * destSize.ratio / originalSize.ratio);
	}

}
