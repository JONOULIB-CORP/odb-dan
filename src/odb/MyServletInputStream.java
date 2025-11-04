package odb;

import java.io.IOException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ReadListener;

public class MyServletInputStream extends ServletInputStream {

    private final MyInputStream mis;

public MyServletInputStream(jakarta.servlet.ServletInputStream sis) {
    this.mis = new MyInputStream(sis, false, null);
}

    public MyServletInputStream(MyInputStream mis) {
        this.mis = mis;
    }

    @Override
    public int read() throws IOException {
        return mis.read();
    }

    @Override
    public boolean isFinished() {
        // On ne connaît pas le nombre exact de bytes, retourne toujours false pour simplifier
        return false;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        // Non utilisé pour ODB
    }
}
