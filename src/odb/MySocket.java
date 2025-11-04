package odb;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Function;

import pack.Handler;
import pack.Pair;

public class MySocket {

    Socket s;
    boolean isodb;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    
    // list of hosts/ports that should be managed with ODB
    // clients : when socket is used on the client side (with new MySocket(host,port)), these host/port should be ODB managed
    // servers : when socket is used on the server side (with new MySocket(socket) / called from accept()), these ports should be ODB managed
    static List<String> clients = List.of("localhost:2001","localhost:2002");
    static List<Integer> servers = List.of(2001,2002);

    // the handler called when a buffer fault occurs
    // it invokes the download of the payload
    static {
        final Function<Pair, Integer> handler = (p) -> {
            System.out.println("#################################");
            System.out.println("got buffer fault !!!");
            System.out.println("#################################");
            VirtualDescriptor d = (VirtualDescriptor)p._desc;
            System.out.println("Handler.bufferFault: download payload("+d.len+")");
            p._buff = odb.Downloader.download(d.host, d.port, d.payloadid);
            p._access = true;
            return null;
        };
        Handler.registerHandler(handler);
    }


    public MySocket(String host, int port) throws UnknownHostException, IOException {
        s = new Socket(host, port);
        //System.out.println("MySocket: client connected");
        isodb = clients.contains(host+":"+port);
        if (isodb) {
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
        }
    }

    public MySocket(Socket s) throws IOException {
        this.s = s;
        isodb = servers.contains(s.getLocalPort());
        if (isodb) {
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
        }
    }

    public MyOutputStream getOutputStream() throws IOException {
        //System.out.println("MySocket: getOutputStream");
        return new MyOutputStream(s.getOutputStream(), isodb, oos);
    }

    public MyInputStream getInputStream() throws IOException {
        //System.out.println("MySocket: getInputStream");
        return new MyInputStream(s.getInputStream(), isodb, ois);
    }

    public void close() throws IOException {
        s.close();
    }
}
