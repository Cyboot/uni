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

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
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
                "data/Person_uris.txt")));
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

            // fill uriFeaturesMap
            fillFeatureMap(uriList, adapter, uriFeaturesMap);

            // the resulting (sorted Map)
            // Map<Score, uri1 + uri2>
            Map<Integer, String> resultSortedMap = new TreeMap<Integer, String>();

            // find every pair
            for (Entry<String, Set<Feature>> entryOuter : uriFeaturesMap
                    .entrySet()) {
                for (Entry<String, Set<Feature>> entryInner : uriFeaturesMap
                        .entrySet()) {
                    if (entryInner == entryOuter)
                        continue;

                    // calculate the intersection
                    Set<Feature> intersectFeature = new HashSet<Feature>(
                            entryOuter.getValue());
                    intersectFeature.retainAll(entryInner.getValue());

                    if (intersectFeature.size() != 0) {
                        System.out.println(intersectFeature);
                    }

                    int sum = 0;
                    for (Feature feature : intersectFeature) {

                        // calculate Frequency on the fly
                        if (!allFeatureMap.containsKey(feature)) {
                            System.out.println("calc freq for " + feature);
                            allFeatureMap.put(feature,
                                    adapter.getPredicateFrequency(feature));
                        }

                        Integer weight = allFeatureMap.get(feature);

                        sum += weight;
                    }

                    if (sum > 0) {
                        System.out.println("putting into resultMap");

                        resultSortedMap.put(sum, entryOuter.getKey() + " : "
                                + entryInner.getKey());
                    }
                }
            }

            System.out.println();
            System.out.println("==================================");
            System.out.println("======        RESULT        ======");
            for (Entry<Integer, String> entry : resultSortedMap.entrySet()) {
                System.out.println(entry.getKey() + "\t" + entry.getValue());
            }

            adapter.closeConnection();
        } catch (RepositoryException e) {
            System.err.println("Caught Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static void fillFeatureMap(List<String> uriList,
            SesameAdapter adapter, Map<String, Set<Feature>> uriFeaturesMap)
            throws RepositoryException, MalformedQueryException,
            QueryEvaluationException {
        for (String uri : uriList) {
            System.out.println("Finding Features for " + uri + "...");

            Set<Feature> featureSet = adapter.getIncommingFeatures(uri);
            featureSet.addAll(adapter.getOutgoingFeatures(uri));

            uriFeaturesMap.put(uri, featureSet);
            System.out
                    .println("...found " + featureSet.size() + " features.\n");
        }
    }
}
