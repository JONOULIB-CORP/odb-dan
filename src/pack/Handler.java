package pack;

import java.util.function.Function;

public class Handler {

        static Function<Pair, Integer> defaulthandler = (p) -> {
                p._access = true;
                System.out.println("#################################");
                System.out.println("got buffer fault !!!");
                System.out.println("#################################");
                return null;
        };

        static Function<Pair, Integer> handler = defaulthandler; 


        public static void registerHandler(Function<Pair, Integer> hdlr) {
                handler = hdlr;
        }

        public static void bufferFault(Pair p) {
                handler.apply(p);
        }

}
