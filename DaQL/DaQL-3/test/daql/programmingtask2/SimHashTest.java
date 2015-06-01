/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql.programmingtask2;

import java.nio.ByteBuffer;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test of the SimHash class.
 */
public class SimHashTest {
    /**
     * Test of method distance().
     */
    @Test
    public void testDistance() {

        // Test case 1
        boolean[] fingerprint1 = new boolean[1];
        boolean[] fingerprint2 = new boolean[1];

        fingerprint1[0] = false;
        fingerprint2[0] = false;

        int expectedDistance = 0;
        int actualDistance = SimHash.distance(fingerprint1, fingerprint2);

        Assert.assertEquals(expectedDistance, actualDistance);

        // Test case 2
        fingerprint1 = new boolean[1];
        fingerprint2 = new boolean[1];

        fingerprint1[0] = true;
        fingerprint2[0] = true;

        expectedDistance = 0;
        actualDistance = SimHash.distance(fingerprint1, fingerprint2);

        Assert.assertEquals(expectedDistance, actualDistance);

        // Test case 3
        fingerprint1 = new boolean[1];
        fingerprint2 = new boolean[1];

        fingerprint1[0] = false;
        fingerprint2[0] = true;

        expectedDistance = 1;
        actualDistance = SimHash.distance(fingerprint1, fingerprint2);

        Assert.assertEquals(expectedDistance, actualDistance);

        // Test case 4
        fingerprint1 = new boolean[1];
        fingerprint2 = new boolean[1];

        fingerprint1[0] = true;
        fingerprint2[0] = false;

        expectedDistance = 1;
        actualDistance = SimHash.distance(fingerprint1, fingerprint2);

        Assert.assertEquals(expectedDistance, actualDistance);

        // Test case 5
        fingerprint1 = new boolean[3];
        fingerprint2 = new boolean[3];

        fingerprint1[0] = true;
        fingerprint1[1] = true;
        fingerprint1[2] = true;

        fingerprint2[0] = false;
        fingerprint2[1] = false;
        fingerprint2[2] = false;

        expectedDistance = 3;
        actualDistance = SimHash.distance(fingerprint1, fingerprint2);

        Assert.assertEquals(expectedDistance, actualDistance);

        // Add more test cases here if you need it.

    }

    /**
     * Test of method distance().
     */
    @Test
    public void testBits() {
        ByteBuffer byteInt = ByteBuffer.allocate(4);
        byteInt.putInt(125);
        byte[] byteArray = byteInt.array(); // byte array of integer 125.

        // We check if the byte representation is the same as the integer
        ByteBuffer wrapped = ByteBuffer.wrap(byteArray); //
        Integer actualInteger = wrapped.getInt(); // 1
        Assert.assertEquals(new Integer(125), actualInteger);

        // The byte array should have 4 cells (.allocate(4))
        Assert.assertEquals(new Integer(4), new Integer(byteArray.length));

        // We convert each byte to an array of bits:
        boolean[] bitArrays0 = SimHash.bits(byteArray[0]);
        boolean[] bitArrays1 = SimHash.bits(byteArray[1]);
        boolean[] bitArrays2 = SimHash.bits(byteArray[2]);
        boolean[] bitArrays3 = SimHash.bits(byteArray[3]);

        // Note that bitArrays3 is the lowest order byte:
        // 01111101 = 2^6+2^5+2^4+2^3+2^2+2^0 = 125

        // bitArrays0, bitArrays1 and bitArrays2 are full with zeros.
        // Taking the byte order into account, the arrays together should
        // be as follows:
        // [bitArrays0][bitArrays1][bitArrays2][bitArrays3]
    }
}
