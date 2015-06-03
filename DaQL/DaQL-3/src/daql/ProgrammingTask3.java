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
            TreeMap<Double, String> resultSortedMap = new TreeMap<Double, String>();

            // find every pair
            int size = uriFeaturesMap.size() * uriFeaturesMap.size();
            int i = 1;

            for (Entry<String, Set<Feature>> entryOuter : uriFeaturesMap
                    .entrySet()) {

                for (Entry<String, Set<Feature>> entryInner : uriFeaturesMap
                        .entrySet()) {
                    System.out.println("Progress " + i + "/" + size);
                    i++;
                    if (entryInner == entryOuter)
                        continue;

                    // calculate the intersection
                    Set<Feature> intersectFeature = new HashSet<Feature>(
                            entryOuter.getValue());
                    intersectFeature.retainAll(entryInner.getValue());

                    double sum = 0;
                    for (Feature feature : intersectFeature) {
                        // calculate Frequency on the fly
                        if (!allFeatureMap.containsKey(feature)) {
                            allFeatureMap.put(feature,
                                    adapter.getPredicateFrequency(feature));
                        }

                        double occurences = allFeatureMap.get(feature);
                        double infoContent = -Math.log10(occurences / N);

                        sum += infoContent;
                    }

                    if (sum > 0) {
                        resultSortedMap.put(sum, entryOuter.getKey() + " : "
                                + entryInner.getKey());
                    }
                }
            }

            System.out.println();
            System.out.println("==================================");
            System.out.println("======        RESULT        ======");
            for (Entry<Double, String> entry : resultSortedMap.descendingMap()
                    .entrySet()) {
                System.out.printf("%.2f \t %s\n", entry.getKey(),
                        entry.getValue());
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
