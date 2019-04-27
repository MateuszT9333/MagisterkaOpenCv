package pl.polsl.aei.mateusz.agerecognizer.files;

import java.io.File;

public class MergedJsonFile extends FileProduct {
    private static String dir = propertiesLoader.getProperty("pathToData");
    private String filename = propertiesLoader.getProperty("mergedAgeToWrinkleFeaturesData");

    @Override
    public void createFileWithSuffix(String suffixOfFile) {
        this.file = new File(String.format("%s%s%s.txt", dir, filename, suffixOfFile));
        setPrintStream(file);
    }
}
