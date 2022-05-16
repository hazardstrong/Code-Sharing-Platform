public class Main {

    public static void main(String[] args) {

        SingleMethodInterface instance = new SingleMethodInterface() {
            @Override
            public void doSomething() {
                System.out.println("The anonymous class does something");
            }
        };
                /* create an instance of an anonymous class here,
                                            do not forget ; in the end */

        instance.doSomething();
    }
}

interface SingleMethodInterface {

    void doSomething();
}