package inverted;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

        String phrase1 = "agile AND and AND can AND ehab AND should AND only";
        String phrase2 = "NOT agile";
        String phrase3 = "introduction AND NOT agile";

        String result = index.find_documents(phrase2);
        System.out.println(result);
    }

}
