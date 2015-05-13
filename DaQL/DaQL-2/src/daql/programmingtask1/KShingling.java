/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql.programmingtask1;

import java.util.HashSet;
import java.util.Set;

/**
 * Class with a static method that transform a text as a set of K-Shingles.
 * 
 * @author Tim Schmiedl
 *
 */
public class KShingling {

    /**
     * 
     * @param k
     *            the shingle size.
     * @param text
     *            the text you want to convert into a set of k-shingles.
     * @return
     */
    public static Set<String> getKShingles(int k, String text) {
        Set<String> shingleSet = new HashSet<String>();

        for (int i = 0; i <= text.length() - k; i++) {
            String subString = text.substring(i, i + k);

            shingleSet.add(subString);
        }

        return shingleSet;
    }
}
