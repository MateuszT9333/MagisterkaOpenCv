package pl.mateusz.agerecognition.utils;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class AgeToWrinkleFeature implements Clusterable {
    public final int age;
    public final float wrinkleFeature;

    public AgeToWrinkleFeature(int age, float wrinkleFeature) {
        this.age = age;
        this.wrinkleFeature = wrinkleFeature;
    }

    @Override
    public double[] getPoint() {
        double[] temp = {(double) this.age, (double) this.wrinkleFeature};
        return temp;
    }
}
