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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import daql.crawler.WikipediaArticleCrawler;
import daql.programmingtask1.JaccardSimilarity;
import daql.programmingtask1.KShingling;

/**
 * Main class of ProgrammingTask1.
 * 
 * @author Tim Schmiedl
 *
 */
public class ProgrammingTask1 {
    private static WikipediaArticleCrawler crawler;

    /**
     * Main Class for Programming task 1.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        crawler = new WikipediaArticleCrawler();
        Map<String, Set<String>> articleShingleMap = new HashMap<String, Set<String>>();

        // read all articles from the text file
        BufferedReader br = new BufferedReader(new FileReader(new File(
                        "data/ListArtTalks.txt")));
        String line = null;
        while ((line = br.readLine()) != null) {
            String article = readArticle(line);
            Set<String> kShingles = KShingling.getKShingles(5, article);

            articleShingleMap.put(line, kShingles);
        }
        br.close();

        // calculate the Jaccard similarity between all articles
        TreeMap<Double, String> scoreMap = new TreeMap<Double, String>();
        for (Entry<String, Set<String>> entryOuter : articleShingleMap
                        .entrySet()) {
            for (Entry<String, Set<String>> entryInner : articleShingleMap
                            .entrySet()) {
                // don't compare equal article
                if (entryOuter == entryInner)
                    continue;

                // compute the Jaccard similarity
                double sim = JaccardSimilarity.computeJaccardSimilarity(
                                entryOuter.getValue(), entryInner.getValue());

                scoreMap.put(sim,
                                entryOuter.getKey() + " - "
                                                + entryInner.getKey());
            }
        }

        // print the similarity in descresing order
        int count = 0;
        for (Entry<Double, String> entry : scoreMap.descendingMap().entrySet()) {
            if (count >= 15)
                break;
            count++;

            System.out.printf("%.3f : %s\n", entry.getKey(), entry.getValue());
        }
    }

    private static String readArticle(String article) {
        crawler.setArticleName(article);
        try {
            crawler.crawl();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String articleText = crawler.getAbstractText();

        // Merge the text from abstract and sections:
        for (String section : crawler.getSectionsText()) {
            articleText += section;
        }

        return articleText;
    }
}
