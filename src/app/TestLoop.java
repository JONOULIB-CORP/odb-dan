package app;

public class TestLoop {

    public static void main(String args[]) {

        byte[] buffer = new byte[1024];

        System.out.println("before loop");

        for (int i=0;i<100;i++) {
            buffer[i] = 0;
            if (i==50) break;
        }

        System.out.println("after loop");
    }
}
