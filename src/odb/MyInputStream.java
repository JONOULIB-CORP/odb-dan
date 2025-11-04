package odb;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import pack.Pair;

public class MyInputStream {

    private InputStream is;
    ObjectInputStream ois;
    private boolean isodb;

    public MyInputStream() {}

    public MyInputStream(InputStream is, boolean isodb, ObjectInputStream ois) {
        this.is = is;
        this.isodb = isodb;
        this.ois = ois;
        //System.out.println("MyInputStream.constructor: isodb="+isodb);
    }

    public int read() throws IOException {
        if (isodb) {
            if (index != -1) {
                int ret = cache[index++];
                if (index == cache.length) index = -1;
                return ret;
            } else {
                Object obj;
                try {
                    obj = ois.readObject();
                } catch (ClassNotFoundException ex) {
                    throw new IOException("Virtual or Real Descriptor's class not found");
                }
                if (obj instanceof VirtualDescriptor) {
                    VirtualDescriptor d = (VirtualDescriptor)obj;
                    System.out.println("MyInputStreamw.read: download payload ("+d.len+")");
                    cache = Downloader.download(d.host, d.port, d.payloadid);
                } else {
                    RealDescriptor d = (RealDescriptor)obj;
                    cache = d.buff;
                    System.out.println("MyInputStream.read: receive real("+cache.length+")");
                }
                index = 0;
                int ret = cache[index++];
                if (index == cache.length) index = -1;
                return ret;
            }
        } else
            return is.read();
    }

    public int read(Pair buff) throws IOException {
        return read(buff, 0, buff._buff.length);
    }

    private byte[] cache;
    private int index = -1;

    public int read(Pair buff, int off, int len) throws IOException {
        if (isodb) {
            if (index == -1) {
                Object obj;
                try {
                    obj = ois.readObject();
                } catch (ClassNotFoundException ex) {
                    throw new IOException("Virtual or Real Descriptor's class not found");
                }
                if (obj instanceof VirtualDescriptor) {
                    VirtualDescriptor d = (VirtualDescriptor)obj;
                    if ((off==0) && (len==d.len)) {
                        buff._desc = d;
                        buff._access = false;
                        System.out.println("MyInputStream.read: virtual payload("+d.len+")");
                        return d.len;
                    }
                    // else download the paylaod
                    System.out.println("MyInputStream.read: download payload("+d.len+")");
                    cache = Downloader.download(d.host, d.port, d.payloadid);
                } else {
                    RealDescriptor d = (RealDescriptor)obj;
                    cache = d.buff;
                    System.out.println("MyInputStream.read: receive real("+d.len+")");
                }
                index = 0;
            }
            buff._access = true;
            int nb = cache.length - index;
            int ret;
            int offcache = index;
            if (len >= nb) {
                ret = nb;
                index = -1;
            } else {
                ret = len;
                index += len;
            }
            System.arraycopy(cache, offcache, buff._buff, off, ret);
            //System.out.println("MyInputStream.read: return("+ret+")");
            return ret;
        } else {
            return is.read(buff._buff, off, len);
        }
    }

    public void close() throws IOException {
        is.close();
    }
}
