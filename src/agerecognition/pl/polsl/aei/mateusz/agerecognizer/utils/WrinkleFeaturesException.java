package pl.polsl.aei.mateusz.agerecognizer.utils;


public class WrinkleFeaturesException extends Exception {
    private String message = "";

    public WrinkleFeaturesException(String message) {
        message = "WrinkleFeatureException: " + message;
        this.message = message;
        System.err.println(message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
