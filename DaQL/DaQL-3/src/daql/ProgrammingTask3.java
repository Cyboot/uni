/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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
    public static void main(String[] args) throws Exception {
        List<String> uriList = new ArrayList<String>();

        // read all articles from the text file
        BufferedReader br = new BufferedReader(new FileReader(new File(
                "data/small.txt")));
        String line = null;
        while ((line = br.readLine()) != null) {
            uriList.add(line);
        }
        br.close();

        SesameAdapter adapter = null;
        try {
            // First initialize the Sesame adapter
            adapter = new SesameAdapter(dbpediaEndpoint);

            Map<String, Set<Feature>> uriFeaturesMap = new HashMap<String, Set<Feature>>();
            Map<Feature, Integer> allFeatureMap = new HashMap<Feature, Integer>();
            Set<Feature> allFeatures = new HashSet<Feature>();

            for (String uri : uriList) {
                Set<Feature> featureSet = adapter.getIncommingFeatures(uri);
                // featureSet.addAll(adapter.getOutgoingFeatures(uri));

                // add to allFeatures
                allFeatures.addAll(featureSet);

                uriFeaturesMap.put(uri, featureSet);
            }

            for (Feature feature : allFeatures) {
                allFeatureMap.put(feature,
                        adapter.getPredicateFrequency(feature));
            }

            Map<Integer, String> resultSortedMap = new TreeMap<Integer, String>();

            for (Entry<String, Set<Feature>> entryOuter : uriFeaturesMap
                    .entrySet()) {
                for (Entry<String, Set<Feature>> entryInnter : uriFeaturesMap
                        .entrySet()) {
                    // calculate the intersection
                    Set<Feature> intersectFeature = entryOuter.getValue();
                    intersectFeature.retainAll(entryInnter.getValue());

                    int sum = 0;
                    for (Feature feature : intersectFeature) {
                        Integer weight = allFeatureMap.get(feature);

                        sum += weight;
                    }
                    resultSortedMap.put(sum, entryOuter.getKey() + ":"
                            + entryInnter.getKey());
                }
            }

            for (Entry<Integer, String> entry : resultSortedMap.entrySet()) {
                System.out.println(entry.getKey() + "\t" + entry.getValue());
            }

            adapter.closeConnection();
        } catch (RepositoryException e) {
            System.err.println("Caught Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
