package pl.polsl.aei.mateusz.agerecognizer.wrinklefeature;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class AgeToWrinkleFeature implements Clusterable {
    public final int age;
    public final float wrinkleFeature;
    public final String filename;

    public AgeToWrinkleFeature(int age, float wrinkleFeature, String filename) {
        this.age = age;
        this.wrinkleFeature = wrinkleFeature;
        this.filename = filename;
    }

    @Override
    public double[] getPoint() {
        double[] temp = {(double) this.age, (double) this.wrinkleFeature};
        return temp;
    }
}
