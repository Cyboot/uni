/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql.programmingtask1;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test of the JaccardSimilarity class.
 */
public class JaccardSimilarityTest {
    /**
     * Test that the Jaccard similarity works properly. Feel free to add more
     * tests!!!
     */
    @Test
    public void testComputeJaccardSimilarity() {
        Set<String> set1 = new HashSet();
        Set<String> set2 = new HashSet();

        // Test case 1
        set1.add("abc");
        set1.add("bcd");
        set1.add("cde");
        set1.add("def");
        set1.add("efg");
        set1.add("fgh");

        set2.add("age");
        set2.add("bcd");
        set2.add("eef");
        set2.add("efg");
        set2.add("ghi");

        Double actualSim = JaccardSimilarity.computeJaccardSimilarity(set1,
                        set2);
        Double expectedSim = new Double(2. / 9);
        Assert.assertEquals(expectedSim, actualSim);

        // Test case 2
        set1 = new HashSet();
        set2 = new HashSet();
        set1.add("abc");
        set2.add("abc");

        actualSim = JaccardSimilarity.computeJaccardSimilarity(set1, set2);
        expectedSim = new Double(1.0);
        Assert.assertEquals(expectedSim, actualSim);

        // Test case 3
        set1 = new HashSet();
        set2 = new HashSet();
        set1.add("abc");
        set2.add("cba");

        actualSim = JaccardSimilarity.computeJaccardSimilarity(set1, set2);
        expectedSim = new Double(0.0);
        Assert.assertEquals(expectedSim, actualSim);

    }
}
