package pl.polsl.aei.mateusz.agerecognizer.wrinklefeature;

public class AgeToWrinkleFeatureHogKnn {
    public final int age;
    public final float[] wrinkleFeature;
    public final String filename;

    public AgeToWrinkleFeatureHogKnn(int age, float[] wrinkleFeature, String filename) {
        this.age = age;
        this.wrinkleFeature = wrinkleFeature;
        this.filename = filename;
    }
}
