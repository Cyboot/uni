/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql.crawler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * The following class has the role of fetching the content of a wikipedia
 * article.
 * 
 * @author arrascue
 */
public class WikipediaArticleCrawler {
    // TO SEND THE REQUEST
    /**
     * The request handler is responsible for obtaining the response.
     */
    private static RequestHandler requestHandler = new RequestHandler();

    /**
     * This variable represents the base API used in all the queries.
     */
    private final String endpoint = "http://en.wikipedia.org/w/api.php?";

    /**
     * This variables stores the query.
     */
    private String query;

    // TO RETRIEVE INFORMATION
    /**
     * This variable stores a JsonObject object representing an object type in
     * JSON from the last query.
     */
    private JsonObject jObject;

    /**
     * This is the name of article.
     */
    private String articleName = null;

    /**
     * Abstract text of the wikipedia document.
     */
    private String abstractText;

    /**
     * This array saves the name of the sections in the discussion page.
     */
    private String[] sectionNames;

    /**
     * This array saves the anchors of the sections in order to retrieve the
     * HTML content.
     */
    private String[] sectionAnchors;

    /**
     * This array saves the index of the section in the discussion page. In the
     * response of the query, the index is the value of the variable "number".
     * Do not confuse this index with the variable "index" in the response.
     */
    private String[] sectionIndexes;

    /**
     * This arrays saves the content of the different sections parsed as HTML.
     */
    private String[] sectionsText;

    /**
     * Method processes the JSON objects returned by the API.
     * 
     * @throws UnsupportedEncodingException
     */
    public void crawl() throws UnsupportedEncodingException {
        abstractText = null;
        sectionNames = null;
        sectionAnchors = null;
        sectionIndexes = null;
        sectionsText = null;

        if (articleName == null) {
            return;
        }
        retrieveAbstractInformation();
    }

    /**
     * The tasks of this method is to get the abstract text of the article.
     * 
     * @throws UnsupportedEncodingException
     */
    public void retrieveAbstractInformation()
                    throws UnsupportedEncodingException {

        query = endpoint + "action=query&format=json&"
                        + "prop=extracts&exintro=&explaintext=&titles="
                        + URLEncoder.encode(articleName, "UTF-8");

        requestHandler.setQuery(query);
        jObject = requestHandler.sendRequest();

        // We detect first if the API returns an error for the given query.
        // In case of errors we abort the retrieval
        JsonObject jErrorObject = jObject.getAsJsonObject("error");
        // System.out.println(jErrorObject.toString());
        if (jErrorObject != null) {
            System.out.println("ERROR");
            return;
        }

        // We start to navigate into the JSON object until we get the abstract
        // text.
        JsonObject jQueryObject = jObject.getAsJsonObject("query");
        JsonObject jPagesObject = jQueryObject.getAsJsonObject("pages");
        // The next JsonElement depends on the page id. We have to get
        // it in order to access "extract".
        String pageId = jPagesObject.entrySet().iterator().next().getKey();
        JsonObject jPageIdObject = jPagesObject.getAsJsonObject(pageId);
        abstractText = jPageIdObject.get("extract").toString();
        retrieveSectionsInformation();
    }

    /**
     * The tasks of this method is to split the content text retrieved into
     * sections.
     * 
     * @throws UnsupportedEncodingException
     */
    public void retrieveSectionsInformation()
                    throws UnsupportedEncodingException {

        query = endpoint
                        + "action=parse&format=json&"
                        + "prop=text%7Cwikitext%7Csections%7Crevid&generatexml=&page="
                        + URLEncoder.encode(articleName, "UTF-8");

        requestHandler.setQuery(query);
        jObject = requestHandler.sendRequest();

        // We detect first if the API returns an error for the given query.
        // In case of errors we abort the retrieval
        JsonObject jErrorObject = jObject.getAsJsonObject("error");
        // System.out.println(jErrorObject.toString());
        if (jErrorObject != null) {
            System.out.println("ERROR");
            return;
        }

        // First of all we retrieve the section and place them on a array. The
        // index of the section is equivalent to the index of the array +1.
        JsonObject jParseObject = jObject.getAsJsonObject("parse");
        JsonArray jSectionsArray = jParseObject.getAsJsonArray("sections");
        int jArrayLength = jSectionsArray.size();
        sectionNames = new String[jArrayLength];
        sectionAnchors = new String[jArrayLength];
        sectionIndexes = new String[jArrayLength];
        sectionsText = new String[jArrayLength];

        String sectionNameWithQuotes = "";
        String sectionNumber = "";
        String anchor = "";

        for (int i = 0; i < jArrayLength; i++) {
            sectionNameWithQuotes = jSectionsArray.get(i).getAsJsonObject()
                            .get("line").toString();
            sectionNumber = jSectionsArray.get(i).getAsJsonObject()
                            .get("number").toString();
            anchor = jSectionsArray.get(i).getAsJsonObject().get("anchor")
                            .toString();

            // Sub-sections are considered as sections.
            sectionNumber = sectionNumber.substring(1,
                            sectionNumber.length() - 1);
            if (sectionNumber.contains(".")) {
                int indexOfFirstPoint = sectionNumber.indexOf(".");
                String entire = sectionNumber.substring(0,
                                indexOfFirstPoint + 1);
                String decimal = sectionNumber.substring(indexOfFirstPoint)
                                .replaceAll("\\.", "");
                sectionNumber = entire + decimal;
            }

            sectionNames[i] = sectionNameWithQuotes.substring(1,
                            sectionNameWithQuotes.length() - 1);
            sectionAnchors[i] = anchor.substring(1, anchor.length() - 1);
            sectionIndexes[i] = sectionNumber;
        }

        // Now that we know the section names and the indexes, we can retrieve
        // the content with wiki markups from "parsed tree". We iterate over the
        // sections retrieved in the previous step.
        JsonObject jHtml = jParseObject.getAsJsonObject("text");
        String htmlText = jHtml.get("*").toString();
        htmlText = htmlText.replaceAll("\\\\n", "");

        String currentAnchor = "";
        String nextAnchor = "";
        String openingHtmlTag = "";
        String closingHtmlTag = "";
        String finalClosingHtmlTag = "<!-- ";
        String sectionText = "";
        int startIndex = 0;
        int endIndex = 0;

        for (int i = 0; i < sectionNames.length; i++) {
            currentAnchor = sectionAnchors[i];
            if (i < sectionNames.length - 1) {
                nextAnchor = sectionAnchors[i + 1];
                openingHtmlTag = "<span class=\\\"mw-headline\\\" id=\\\""
                                + currentAnchor + "\\\">";
                closingHtmlTag = "<span class=\\\"mw-headline\\\" id=\\\""
                                + nextAnchor + "\\\">";
                startIndex = htmlText.indexOf(openingHtmlTag) - 4;
                endIndex = htmlText.indexOf(closingHtmlTag) - 4;
            } else {
                openingHtmlTag = "<span class=\\\"mw-headline\\\" id=\\\""
                                + currentAnchor + "\\\">";
                startIndex = htmlText.indexOf(openingHtmlTag) - 4;
                endIndex = htmlText.indexOf(finalClosingHtmlTag);
            }
            sectionText = htmlText.substring(startIndex, endIndex).replaceAll(
                            "\\\\\"", "\"");

            // We remove the last new lines at the end of section.
            sectionText = sectionText.trim();

            HtmlToPlainTextParser parser = new HtmlToPlainTextParser();
            parser.setHtmlText(sectionText);
            sectionText = parser.convertToPlainText();

            sectionsText[i] = sectionText;
        }
    }

    /**
     * @return the abstract text of the page
     */
    public String getAbstractText() {
        return abstractText;
    }

    /**
     * @return the sections array wrt. the set article.
     */
    public String[] getSectionNames() {
        if (articleName == null) {
            return null;
        }
        return sectionNames;
    }

    /**
     * @return the section anchors wrt. the set article.
     */
    public String[] getSectionAnchors() {
        if (articleName == null) {
            return null;
        }
        return sectionAnchors;
    }

    /**
     * @return the sections array wrt. the set article.
     */
    public String[] getSectionIndexes() {
        if (articleName == null) {
            return null;
        }
        return sectionIndexes;
    }

    /**
     * @return the html section anchors wrt. the set article.
     */
    public String[] getSectionsText() {
        if (articleName == null) {
            return null;
        }
        return sectionsText;
    }

    /**
     * @return Returns the name of the current article.
     */
    public String getArticleName() {
        return articleName;
    }

    /**
     * Method to set the name of the article.
     */
    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }
}
