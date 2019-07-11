package pl.polsl.aei.mateusz.agerecognizer.utils;

public class WrinkleToAgeFunction {
    private static double _0_26(float f)  {return 0.000912187996975040 * f;}
    private static double _26_35(float f)  {return 0.000912187996975040 * f;}
    private static double _35_45(float f)  {return 0.000912187996975040 * f;}
    private static double _45_55(float f)  {return 0.000912187996975040 * f;}
    private static double _55_65(float f)  {return 0.000912187996975040 * f;}
    private static double _65_inf(float f)  {return 0.000912187996975040 * f;}

    public static double getAgeFromWrinkleFeatureNonLinearFunction(float f) {
        if (f < 0.04) return _0_26(f);
        if (f >= 0.04 && f < 0.1) return _26_35(f);
        if (f >= 0.1 && f < 0.14) return _35_45(f);
        if (f >= 0.14 && f < 0.17) return _45_55(f);
        if (f >= 0.17 && f < 0.2) return _55_65(f);
        return _65_inf(f);
    }

    public static double getAgeFromWrinkleFeatureLinearFunction(float f) {
        return (int) (1 / (Math.pow(f, 2) * 1.98531412899040e-05
                + 0.000912187996975040 * f
                + 0.000963979767558933));
    }
}
