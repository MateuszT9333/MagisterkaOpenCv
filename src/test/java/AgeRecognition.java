import org.junit.Test;
import pl.polsl.aei.mateusz.agerecognizer.recognize.AgeRecognizer;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;

import java.io.File;
import java.io.IOException;

/**
 * Recognizing Age from selected image
 */
public class AgeRecognition {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
    private final static String imagePath = propertiesLoader.getProperty("ageToRecognizePath");

    @Test
    public void recognizeAge() throws IOException {
        AgeRecognizer.recognizeAge(new File(imagePath));
    }

}
