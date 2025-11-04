package app;
public class TestMethod {

    public static void main(String args[]) {

        byte[] buffer = new byte[1024];

        System.out.println("before method call");

        call(buffer);        

        System.out.println("after method call");

        buffer[4] = 0;

        System.out.println("after second access");

        System.out.println("before method returnmeth");

        buffer = returnmeth();

        System.out.println("after method returnmeth");

        buffer[4] = 0;

        System.out.println("after last access");
    }

    private static void call(byte[] b) {
        b[4] = 0;
    }

    private static byte[] returnmeth() {
        return new byte[10];
    }
    
}
