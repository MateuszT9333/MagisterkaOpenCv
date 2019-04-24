package pl.polsl.aei.mateusz.agerecognizer.utils.files;

import com.google.gson.Gson;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AgeToWrinkleJsonFile extends FileProduct {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
    private String dir = propertiesLoader.getProperty("pathToData");
    private String filename = propertiesLoader.getProperty("ageToWrinkleJsonData");

    @Override
    public void createFileWithSuffix(String suffixOfFile) {
        this.file = new File(String.format("%s%s%s.txt", dir, filename, suffixOfFile));
        setPrintStream(file);
    }

    @Override
    public void writeln(Object ageToWrinkleFeature) {
        String line = new Gson().toJson(ageToWrinkleFeature);
        printStream.println(line);
    }
    @Override
    public int nextIntegerCounter() {
        //iterate over file in dir
        File dataDir = new File(dir);
        File[] filesLikeAgeToWrinkleJson = dataDir.listFiles(pathname -> pathname.getName()
                .startsWith(filename));

        //find last index in suffix
        assert filesLikeAgeToWrinkleJson != null;
        List<Integer> suffixes = Arrays.stream(filesLikeAgeToWrinkleJson)
                .map(f -> extractSuffix(f.getName()))
                .collect(Collectors.toList());
        if (suffixes.size() > 0) {
            return Collections.max(suffixes) + 1;
        } else return 0;
    }

    private int extractSuffix(String name) {
        Pattern pattern = Pattern.compile("_[0-9]*");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(0).replace("_", ""));
        }
        return -1;
    }
}
