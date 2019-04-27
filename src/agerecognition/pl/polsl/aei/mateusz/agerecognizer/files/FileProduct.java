package pl.polsl.aei.mateusz.agerecognizer.files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public abstract class FileProduct {
    final static PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
    private static final Logger log = LogManager.getLogger("main");
    protected File file;
    PrintStream printStream;

    public abstract void createFileWithSuffix(String suffixOfFile);

    public void writeln(Object object) {
        this.printStream.println((String) object);
    }

    final void setPrintStream(File file) {
        try {
            this.printStream = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            log.catching(e);
            this.printStream = null;
        }
    }
}
