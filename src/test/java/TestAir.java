import aryanware.air.Service;

public class TestAir extends Service {
    TestAir() throws Exception {
        super("Assassin", ServiceType.DIRECT_LOCAL);
    }

    public static void main(String[] args) throws Exception {
        TestAir testAir = new TestAir();
        Thread.sleep(1000);
        testAir.stop();
    }
}
