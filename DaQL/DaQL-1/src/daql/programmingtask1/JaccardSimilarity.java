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
 * Class with a static method that computes the Jaccard similarity of two sets.
 * 
 * @author ...write your name here...
 *
 */
public class JaccardSimilarity {

    /**
     * Complete this code.
     * 
     * @param set1
     * @param set2
     * @return
     */
    public static double computeJaccardSimilarity(Set set1, Set set2) {
        Set unionSet = new HashSet(set1);
        unionSet.addAll(set2);

        double intersectCount = 0;
        for (Object object : set1) {
            if (set2.contains(object))
                intersectCount++;
        }

        return intersectCount / unionSet.size();
    }
}
