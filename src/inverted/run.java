package inverted;

import java.io.IOException;


public class run {

    public static void main(String[] args) throws IOException {
        InvertedIndex index = new InvertedIndex();
        index.buildIndex(new String[]{
                "docs/100.txt",
                "docs/101.txt",
                "docs/102.txt",
                "docs/103.txt",
                "docs/104.txt",
                "docs/105.txt",
                "docs/106.txt",
                "docs/107.txt",
                "docs/108.txt",
                "docs/109.txt",
                "docs/500.txt",
                "docs/501.txt",
                "docs/502.txt",
                "docs/503.txt",
                "docs/504.txt",

        });

        String[] phrases = {
                "agile AND and AND can AND ehab AND should AND only",
                "NOT agile",
                "agile AND NOT introduction",
                "machine OR condition",
                "computers AND intelligence OR quarantine",
                "doaa OR ehab OR sarah",
                "increased OR prediction OR more",
        };

        for(String phrase : phrases)
        {
            String result = index.find_documents(phrase);
            System.out.println(result);
        }
    }

}
