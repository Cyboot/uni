/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql.programmingtask2;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Class with a static method that given a representation of a document as a
 * set of k-Shingles, computes the SimHash.
 * @author ...write your name here...
 *
 */
public class SimHash {
    
    /**
     * 
     * @param kShingles Document represented by a set of k-shingles.
     * @return an array of bits represented as an array of booleans, where TRUE
     * at position i is a 1 and FALSE is 0.
     * 
     */
    public static boolean[] computeFingerprint(Set<String> kShingles) {
        //This set stores the hash code of each kShingle
        Set<HashCode> hashCodes = new HashSet<HashCode>();
        //Hash function used is MD5 (128 hash bits):
        HashFunction hf = Hashing.md5();
        HashCode hc = null;
                                
        //One has to iterate over all k-shingles:
        for (String kShingle: kShingles) {
            hc = hf.newHasher()
                    .putString(kShingle, Charsets.UTF_8)
                    .hash();
            hashCodes.add(hc);
        }
        
        System.out.println(hc.asBytes().length);
        
        //SINGLE HASH CODE -> ONE array of bits
        //Note that you can invoke hc.asBytes() to get the hashcode as an array
        //of bytes (NOT bits).
        //Use the method SimHash.bits(...) to convert a single byte to an array
        //of bits. You have to combine all arrays of bits together taking into 
        //account the order of the byte. 
        //In this way you will have a hash code as a single array of bits.
        
        //SET of HASH CODES -> SET of arrays of bits -> Finderprint
        //From the set of hash codes (one for each k-shingle) you have to 
        //compute one single array of bits, the fingerprint, as described in 
        //slide 22 of 02-DAQL-Finding_similarities.pdf.        
        
        /*
         * Complete your code here.
         */
        return null;
    }
    
    /**
     * Given two fingerprints (arrays of bits), this method computes the XOR
     * of both arrays and returns the number of ones of the resulting vector.
     * See the test to understand what is the expected result.
     * @param fingerprint1
     * @param fingerprint2
     * @return
     */
    public static int distance(
            boolean[] fingerprint1, boolean[] fingerprint2) {         
        /*
         * Complete your code here.
         * It shouldn't return 0.
         */
        return 0;
    }
    
    /**
     * Converts a byte into an array of bits (represented as an array of 
     * booleans, where true is 1 and false is 0).
     * @param b
     * @return
     */
    public static boolean[] bits(final byte b) {
        return new boolean[] {
            (b &    1) != 0,
            (b &    2) != 0,
            (b &    4) != 0,
            (b &    8) != 0,
            (b & 0x10) != 0,
            (b & 0x20) != 0,
            (b & 0x40) != 0,
            (b & 0x80) != 0
        };
    }
}
