package pl.polsl.aei.mateusz.agerecognizer.files;

import com.google.gson.Gson;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.AgeToWrinkleFeatureHogKnn;

import java.io.File;
import java.util.Arrays;

public class MatlabDatFileHogKnn extends FileProduct {
    private static final String dir = propertiesLoader.getProperty("pathToData");
    private final String filename = propertiesLoader.getProperty("mergedAgeToWrinkleFeaturesData");

    @Override
    public void createFileWithSuffix(String suffixOfFile) {
        this.file = new File(String.format("%s\\%s%s.dat", dir, filename, suffixOfFile));
        setPrintStream(file);
    }

    @Override
    public void writeln(Object line) {
        Gson gson = new Gson();

        AgeToWrinkleFeatureHogKnn ageToWrinkleFeature = gson.fromJson((String) line, AgeToWrinkleFeatureHogKnn.class);
        String wrinkleVector = Arrays.toString(ageToWrinkleFeature.wrinkleFeature)
                .replaceAll("[\\[\\]]", "")
                .replaceAll(",", "   ");

        this.printStream.println(String.format("   %s   %s   %s"
                , ageToWrinkleFeature.age
                , wrinkleVector
                , ageToWrinkleFeature.filename));
    }
}
