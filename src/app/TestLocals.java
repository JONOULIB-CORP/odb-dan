package app;

public class TestLocals {

    public static void main(String args[]) {

        byte[] buffer = new byte[1024];

        System.out.println("before first access");

        buffer[4] = 0;

        System.out.println("after first access");

        buffer[4] = 0;


        System.out.println("after second access");

    }

}
