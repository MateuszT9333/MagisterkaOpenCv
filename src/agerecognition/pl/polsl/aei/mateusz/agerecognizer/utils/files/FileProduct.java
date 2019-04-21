package pl.polsl.aei.mateusz.agerecognizer.utils.files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public abstract class FileProduct {

    private static final Logger log = LogManager.getLogger("main");
    private PrintStream printStream;

    public abstract void createFileWithSuffix(String suffixOfFile);

    public abstract void writeln(Object object);

    final void setPrintStream(File file) {
        try {
            this.printStream = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            log.catching(e);
            this.printStream = null;
        }
    }
}
