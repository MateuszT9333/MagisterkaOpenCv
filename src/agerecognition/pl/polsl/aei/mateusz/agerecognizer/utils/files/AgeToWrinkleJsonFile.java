package pl.polsl.aei.mateusz.agerecognizer.utils.files;

import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;

import java.io.File;

public class AgeToWrinkleJsonFile extends FileProduct {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
    private String dir = propertiesLoader.getProperty("pathToData");
    private String filename = propertiesLoader.getProperty("ageToWrinkleJsonData");

    @Override
    public void createFileWithSuffix(String suffixOfFile) {
        this.file = new File(dir + filename + suffixOfFile);
        setPrintStream(file);
    }

    @Override
    public void writeln(Object object) {
        String line = object.toString();//line to write in printstream
        printStream.println(line);
    }

    @Override
    public int nextIntegerCounter() {
        //iterate over file in dir

        //find only with name like "this.filename.*"

        //find last index in suffix
        return 0;
    }
}
