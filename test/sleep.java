public class sleep {
    public static void main(String[] args) throws InterruptedException {
        final int sleepSeconds = Integer.parseInt(args[0]);
        while(true) {
            Thread.sleep(sleepSeconds);
        }
    }
}
