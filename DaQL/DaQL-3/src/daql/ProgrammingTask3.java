/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql;

import java.util.Set;

import org.openrdf.repository.RepositoryException;

import daql.programmingtask3.Feature;
import daql.programmingtask3.SesameAdapter;

/**
 * Main class of ProgrammingTask3.
 * 
 * Tim Schmiedl
 */
public class ProgrammingTask3 {
    public static final String dbpediaEndpoint = "http://dbpedia.org/sparql";
    public static final int N = 88684909;

    /**
     * Main Class for Programming task 3.
     * 
     * @param args
     */
    public static void main(String[] args) {
        /*
         * You have to REWRITE this method and calculate the similarity of pairs
         * of person's RDF graphs using the given Linked Data similarity metric
         * 
         * Follow the following steps: 1- Load person's uris from the file:
         * data/Persons_uris.txt 2- Retrieve the features for each person's
         * resource (uri) from dbpedia For this you need to use the methods:
         * SesameAdapter.getIncommingFeatures() and
         * SesameAdapter.getOutgoingFeatures() 3- Compute similarities between
         * each pairs of the loaded resources. For that: - Find (for each pair)
         * the features intersection set. And calculate the corresponding
         * similarity - Compute the similarity as the value of the Information
         * Content of the intersection set. Use the method
         * SesameAdapter.getPredicateFrequency() to find the frequency of a
         * feature. 4- Find pairs of uris of the top 10 similarity values, print
         * them to the outpt following format: Similarity_value : uri1 : uri2
         * ...
         * 
         * Note: In order to calculate the Information Content for each feature
         * (f) of the intersection set you need to find freq(f) - which is how
         * many times the feature (f) appears in the RDF graph. Find a way to
         * avoid recalculating freq(feature) more than once!
         */

        // Replace the following code with your code...

        /*
         * The following code is to provide you with an example of how you can
         * use the SesameAdapter class and post a sparql query
         */
        // First initialize the Sesame adapter
        SesameAdapter adapter = null;
        try {
            adapter = new SesameAdapter(dbpediaEndpoint);
            String uri = "http://dbpedia.org/resource/Marie_Curie";
            // get the incoming features of uri
            Set<Feature> featureList = adapter.getIncommingFeatures(uri);

            // print the result:
            for (Feature f : featureList)
                System.out.println(f);

            System.out.println("Number of retrieved features: "
                            + featureList.size());
            // Finally (VERY IMPORTANT), you need to close the adapter
            // connection.
            adapter.closeConnection();

        } catch (RepositoryException e) {
            System.err.println("Caught Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
