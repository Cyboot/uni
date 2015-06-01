/**
 * University of Freiburg.
 * This code has been provided within the scope of
 * the lecture Data Analysis and Query Languages
 * (Summer term 2015)
 */
package daql.crawler;

import org.jsoup.Jsoup;

/**
 * This class converts a string containing html code into plain text by removing
 * all html tags.
 * 
 * @author arrascue
 * 
 */
public class HtmlToPlainTextParser {
    /**
     * This variable contains the html string that has to be converted.
     */
    private String htmlText;

    /**
     * @return the converted String.
     */
    public String convertToPlainText() {
        return Jsoup.parse(htmlText).text();
    }

    /**
     * Getter of htmlText.
     */
    public String getHtmlText() {
        return htmlText;
    }

    /**
     * Setter of htmlText.
     */
    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

}
