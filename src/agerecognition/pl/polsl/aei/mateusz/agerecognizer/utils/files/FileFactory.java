package pl.polsl.aei.mateusz.agerecognizer.utils.files;

public class FileFactory {

    public static FileProduct create(FileType type) {
        switch (type) {
            case ageToWrinkleJson2:
                return new AgeToWrinkleJsonFile();
            case clusteredJson:
                return new ClusteredJsonFile();
            case mergedJson:
                return new MergedJsonFile();
            case matlabDat:
                return new MatlabDatFile();
            default:
                throw new RuntimeException("Invalid FileType");
        }
    }
}
