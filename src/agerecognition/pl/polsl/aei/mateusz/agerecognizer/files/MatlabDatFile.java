package pl.polsl.aei.mateusz.agerecognizer.files;

import com.google.gson.Gson;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.AgeToWrinkleFeature;

import java.io.File;

public class MatlabDatFile extends FileProduct {
    private static final String dir = propertiesLoader.getProperty("pathToData");
    private final String filename = propertiesLoader.getProperty("mergedAgeToWrinkleFeaturesData");

    @Override
    public void createFileWithSuffix(String suffixOfFile) {
        this.file = new File(String.format("%s%s%s.dat", dir, filename, suffixOfFile));
        setPrintStream(file);
    }

    @Override
    public void writeln(Object line) {
        Gson gson = new Gson();

        AgeToWrinkleFeature ageToWrinkleFeature = gson.fromJson((String) line, AgeToWrinkleFeature.class);

        this.printStream.println(String.format("   %s   %s"
                , ageToWrinkleFeature.age
                , ageToWrinkleFeature.wrinkleFeature));
    }
}
