package pl.polsl.aei.mateusz.agerecognizer.exceptions;


public class WrinkleFeaturesException extends Exception {
    private String message = "";

    public WrinkleFeaturesException(String message) {
        message = "WrinkleFeatureException: " + message;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
