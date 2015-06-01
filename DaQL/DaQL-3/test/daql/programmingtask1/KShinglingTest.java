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
 * Test of the KShingling class.
 */
public class KShinglingTest {
    /**
     * Test that the KShingling class works properly. Feel free to add more
     * tests!!!
     */
    @Test
    public void testComputeJaccardSimilarity() {
        // Test case 1
        String text = "abcdef";
        Set<String> actualKShingles = KShingling.getKShingles(3, text);
        Set<String> expectedKShingles = new HashSet<String>();
        expectedKShingles.add("abc");
        expectedKShingles.add("bcd");
        expectedKShingles.add("cde");
        expectedKShingles.add("def");

        // Asserts that both sets have the same size.
        Assert.assertEquals(expectedKShingles.size(), actualKShingles.size());

        // Asserts that all elements of the expected set of shingles is included
        // in the actual sets of shingles.
        for (String kShingle : expectedKShingles) {
            Assert.assertTrue(actualKShingles.contains(kShingle));
        }

        // Test case 2
        text = "ab";
        actualKShingles = KShingling.getKShingles(3, text);
        expectedKShingles = new HashSet<String>(); // No shingles is fine

        // Asserts that both sets have the same size.
        Assert.assertEquals(expectedKShingles.size(), actualKShingles.size());

        // Asserts that all elements of the expected set of shingles is included
        // in the actual sets of shingles.
        for (String kShingle : expectedKShingles) {
            Assert.assertTrue(actualKShingles.contains(kShingle));
        }

        // Test case 3
        text = "abc def ghi";
        actualKShingles = KShingling.getKShingles(4, text);
        expectedKShingles = new HashSet<String>();
        expectedKShingles.add("abc ");
        expectedKShingles.add("bc d");
        expectedKShingles.add("c de");
        expectedKShingles.add(" def");
        expectedKShingles.add("def ");
        expectedKShingles.add("ef g");
        expectedKShingles.add("f gh");
        expectedKShingles.add(" ghi");

        // Asserts that both sets have the same size.
        Assert.assertEquals(expectedKShingles.size(), actualKShingles.size());

        // Asserts that all elements of the expected set of shingles is included
        // in the actual sets of shingles.
        for (String kShingle : expectedKShingles) {
            Assert.assertTrue(actualKShingles.contains(kShingle));
        }
    }
}
