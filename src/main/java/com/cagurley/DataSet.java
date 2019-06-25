package com.cagurley;

import java.io.File;
import java.net.URL;

public class DataSet {
    private String fileName;
//    private String fileExt;
    private File file;

    public DataSet(String fileName) {
        this.fileName = fileName;
//        String[] splitName = fileName.split("\\.(?=[^.]+$)");
//        if (splitName.length == 2) {
//            this.fileExt = splitName[1].toLowerCase();
//        }
        this.file = this.getFileFromResources();
    }

    private File getFileFromResources() {
        URL resource = this.getClass().getClassLoader().getResource(this.fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File is not accessible.");
        } else {
            return new File(resource.getFile());
        }
    }

    public File getFile() {
        return this.file;
    }
}
