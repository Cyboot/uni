package daql;

import org.junit.Assert;
import org.junit.Test;

/**
 * A class with some test examples.
 * 
 * @author arrascue
 *
 */
public class HelloWorldTest {
    /**
     * Error tolerance.
     */
    private static final double DELTA = 1e-15;

    /**
     * Test example.
     */
    @Test
    public void testExample() {
        Assert.assertTrue(new Boolean(true));
        Assert.assertEquals(new Double(1.0), new Double(1.0));
        Assert.assertEquals(1.0, 0.9999999999999999999999, DELTA);
        Assert.assertEquals(new Double(2.0), new Double(2.0));
    }
}
