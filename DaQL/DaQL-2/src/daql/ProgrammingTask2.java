/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql;

import java.util.HashSet;
import java.util.Set;

import daql.programmingtask2.SimHash;

/**
 * Main class of ProgrammingTask2.
 * @author ...write your name here...
 *
 */
public class ProgrammingTask2 {
    /**
     * Main Class for Programming task 2.
     * @param args
     */
    public static void main(String[] args) {
        /*
         * You have to REWRITE this method and calculate the similarity of 
         * pairs of Wikipedia articles using the SimHash fingerprints instead 
         * of using the Jaccard Similarity. 
         * 
         * The first step consists in computing the fingerprint of each article.
         * You can store the fingerprints in a hash table.
         * Then, to compute the similarity of two fingerprints you can compute 
         * the XOR of fingerprints and count the resulting number of 1's. 
         * Remember that this is a distance metric and not a similarity metric.
         * Therefore, treat elements as similar, whenever this number is 
         * smaller or equal to k.
         * 
         * The list of documents you have to consider is the same as for the 
         * previous programming task 1./data/ListartTalks.txt.
         */
        
        //You can remove the following code. It is just to give you an idea 
        //of the program flow.
        //Suppose you have a document represented by the set of k-shingles set1
        //(you can get this invoking KShingling.getKShingles()):
        Set<String> set1 = new HashSet();
        set1.add("abc");
        set1.add("bcd");
        set1.add("cde");
        set1.add("def");
        
        //To compute the fingerprint:
        boolean[] fingerprint1 = SimHash.computeFingerprint(set1);
        
        //Now you do the same with another document:
        Set<String> set2 = new HashSet();
        set2.add("opq");
        set2.add("pqr");
        set2.add("qrs");
        set2.add("rst");
        
        boolean[] fingerprint2 = SimHash.computeFingerprint(set2);
        
        //Now you can compute the distance of both fingerprints with this 
        //method (based on XOR)
        double numOfDifferentBits 
            = SimHash.distance(fingerprint1, fingerprint2);
        
        //Finally, decide if the documents are similar if numOfDifferentBits < k
        //where k is a threshold you can choose.        
        //Compute the most similar pairs of documents.
    }
}
