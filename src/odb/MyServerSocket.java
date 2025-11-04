package odb;

import java.io.IOException;
import java.net.ServerSocket;

public class MyServerSocket {

    ServerSocket s;

    public MyServerSocket(int port) throws IOException {
        s = new ServerSocket(port);
    }

    public MySocket accept() throws IOException {
        return new MySocket(s.accept());
    }

    public void close() throws IOException {
        s.close();
    }
}
