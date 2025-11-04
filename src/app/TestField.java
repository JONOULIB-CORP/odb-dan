package app;

public class TestField {

    private static int x;
    public static byte[] buffer = new byte[1024];

    public static void main(String args[]) {

        System.out.println("before first access");

        x = 5;
        buffer[4] = 0;

        System.out.println("after first access");

        buffer[4] = 0;


        System.out.println("after second access");

    }
}
