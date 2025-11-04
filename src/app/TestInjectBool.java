package app;

public class TestInjectBool {

    public static void main(String args[]) {

        long t1, t2;
        byte[] buffer = new byte[1024];
        boolean _access = true;


        System.out.println("begin");

        t1 = System.currentTimeMillis();

        for (int j=0;j<100000000;j++)
        for (int i=0;i<100;i++) {
            if (!_access) System.out.println("buffer fault");
            buffer[i] = 0;
            if (i==50) break;
        }

        t2 = System.currentTimeMillis();

        System.out.println("\n time:"+(t2-t1)+" millis");

        System.out.println("end");
    }
}
