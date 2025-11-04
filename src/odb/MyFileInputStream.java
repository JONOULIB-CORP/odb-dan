package odb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import pack.Pair;

public class MyFileInputStream extends MyInputStream {

    private FileInputStream fis;

    public MyFileInputStream(String name) throws FileNotFoundException {
        this.fis = new FileInputStream(name);
    }

    public int read() throws IOException {
            return fis.read();
    }

    public int read(Pair buff) throws IOException {
        return read(buff, 0, buff._buff.length);
    }

    public int read(Pair buff, int off, int len) throws IOException {
        if (!buff._access) {
            // overwrite the buffer
            buff._access = false;
        }
        return fis.read(buff._buff, off, len);
    }

    public void close() throws IOException {
        fis.close();
    }
}
