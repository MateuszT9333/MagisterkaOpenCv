package pl.polsl.aei.mateusz.agerecognizer.utils;


    public final class AgeSection {

        private static final String _0_26 = "0 to 26";
        private static final String _26_35 = "26 to 35";
        private static final String _35_45 = "35 to 45";
        private static final String _45_55 = "45 to 55";
        private static final String _55_65 = "55 to 65";
        private static final String _65_inf = "over 65";

        private AgeSection() { }

        public static String getAgeSectionFromWrinkleFeature(float f) {
            if (f < 0.04) return _0_26;
            if (f >= 0.04 && f < 0.1) return _26_35;
            if (f >= 0.1 && f < 0.14) return _35_45;
            if (f >= 0.14 && f < 0.17) return _45_55;
            if (f >= 0.17 && f < 0.2) return _55_65;
            return _65_inf;
        }
    }
