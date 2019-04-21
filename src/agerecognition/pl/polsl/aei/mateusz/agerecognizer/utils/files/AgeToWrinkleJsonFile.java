package pl.polsl.aei.mateusz.agerecognizer.utils.files;

import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;

import java.io.File;
import java.io.PrintStream;

public class AgeToWrinkleJsonFile extends FileProduct {
    private PrintStream printStream = null;

    @Override
    public void createFileWithSuffix(String suffixOfFile) {
        String baseFilename = new PropertiesLoader().getProperty("ageToWrinkleJsonPath");
        File file = new File(baseFilename + suffixOfFile);
        setPrintStream(file);
    }

    @Override
    public void writeln(Object object) {
        String line = object.toString();//line to write in printstream
        printStream.println(line);
    }
}
