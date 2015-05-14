/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql.programmingtask2;

import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Class with a static method that given a representation of a document as a set
 * of k-Shingles, computes the SimHash.
 * 
 * @author Tim Schmiedl
 *
 */
public class SimHash {

    /**
     * 
     * @param kShingles
     *            Document represented by a set of k-shingles.
     * @return an array of bits represented as an array of booleans, where TRUE
     *         at position i is a 1 and FALSE is 0.
     * 
     */
    public static boolean[] computeFingerprint(Set<String> kShingles) {
        // Hash function used is MD5 (128 hash bits):
        HashFunction hf = Hashing.md5();

        int[] fingerPrintVector = new int[128];

        // One has to iterate over all k-shingles:
        for (String kShingle : kShingles) {
            HashCode hc = hf.newHasher().putString(kShingle, Charsets.UTF_8)
                            .hash();

            byte[] bytes = hc.asBytes();
            for (int i = 0; i < bytes.length; i++) {
                boolean[] bits = bits(bytes[i]);

                for (int j = 0; j < bits.length; j++) {
                    // incremeent/decrement Vector (see slides step 2)
                    if (bits[j]) {
                        fingerPrintVector[i * 8 + j]++;
                    } else {
                        fingerPrintVector[i * 8 + j]--;
                    }
                }
            }
        }

        boolean[] resultFingerPrint = new boolean[128];
        for (int i = 0; i < fingerPrintVector.length; i++) {
            int value = fingerPrintVector[i];
            resultFingerPrint[i] = value > 0;
        }

        return resultFingerPrint;
    }

    /**
     * Given two fingerprints (arrays of bits), this method computes the XOR of
     * both arrays and returns the number of ones of the resulting vector. See
     * the test to understand what is the expected result.
     * 
     * @param fingerprint1
     * @param fingerprint2
     * @return
     */
    public static int distance(boolean[] fingerprint1, boolean[] fingerprint2) {
        int distance = 0;

        for (int i = 0; i < fingerprint1.length; i++) {
            boolean b1 = fingerprint1[i];
            boolean b2 = fingerprint2[i];

            if (b1 != b2)
                distance++;
        }

        return distance;
    }

    /**
     * Converts a byte into an array of bits (represented as an array of
     * booleans, where true is 1 and false is 0).
     * 
     * @param b
     * @return
     */
    public static boolean[] bits(final byte b) {
        return new boolean[] { (b & 1) != 0, (b & 2) != 0, (b & 4) != 0,
                        (b & 8) != 0, (b & 0x10) != 0, (b & 0x20) != 0,
                        (b & 0x40) != 0, (b & 0x80) != 0 };
    }
}
