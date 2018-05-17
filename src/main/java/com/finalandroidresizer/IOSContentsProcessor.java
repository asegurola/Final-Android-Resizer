package com.finalandroidresizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class IOSContentsProcessor {

    public void generateContents(File resDirectory, File file, List<ScaledFile> scaledFiles) throws IOException {

        String baseName = FilenameUtils.getBaseName(file.getName());

        String finalPath = resDirectory.getAbsolutePath() + "/" + baseName + ".imageset/Contents.json";

        JSONArray images = new JSONArray();

        JSONObject image = new JSONObject();
        image.put("idiom", "universal");

        for (ScaledFile scaledFile : scaledFiles) {
            images.put(
                    new JSONObject(image, JSONObject.getNames(image))
                            .put("filename", scaledFile.file.getName())
                            .put("scale", scaledFile.size.getSize())
            );
        }

        JSONObject info = new JSONObject();
        info.put("version", 1);
        info.put("author", "Final-Resizer");

        FileUtils.write(
                new File(finalPath),
                new JSONObject()
                        .put("images", images)
                        .put("info", info)
                        .toString(2),
                Charset.forName("UTF-8")
        );
    }

    public static class ScaledFile {
        public File file;
        public ImageProcessor.Sizes size;

        public ScaledFile(File file, ImageProcessor.Sizes size) {
            this.file = file;
            this.size = size;
        }
    }
}
