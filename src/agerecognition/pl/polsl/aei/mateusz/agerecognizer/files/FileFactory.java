package pl.polsl.aei.mateusz.agerecognizer.files;

public class FileFactory {

    public static FileProduct create(FileType type) {
        switch (type) {
            case ageToWrinkleJson:
                return new AgeToWrinkleJsonFile();
            case clusteredJson:
                return new ClusteredJsonFile();
            case mergedJson:
                return new MergedJsonFile();
            case matlabDat:
                return new MatlabDatFile();
            case matlabDatHogKnn:
                return new MatlabDatFileHogKnn();
            default:
                throw new RuntimeException("Invalid FileType");
        }
    }
}
