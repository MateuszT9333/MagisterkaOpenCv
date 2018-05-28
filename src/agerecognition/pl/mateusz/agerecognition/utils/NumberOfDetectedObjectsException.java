package pl.mateusz.agerecognition.utils;

public class NumberOfDetectedObjectsException extends Exception {
    public NumberOfDetectedObjectsException(String path) {
        System.err.println("Throwed by processed file " + path);
    }
}
