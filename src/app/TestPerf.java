package app;
public class TestPerf {


    public static void main(String args[]) {

        long t1, t2;
        byte[] buffer = new byte[1024];

        System.out.println("begin");

        t1 = System.currentTimeMillis();

        for (int j=0;j<100000000;j++)
        for (int i=0;i<100;i++) {
            buffer[i] = 0;
            if (i==50) break;
        }

        t2 = System.currentTimeMillis();

        System.out.println("\n time:"+(t2-t1)+" millis");

        System.out.println("end");
    }
}
