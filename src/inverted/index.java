package inverted;
/*
 * InvertedIndex - Given a set of text files, implement a program to create an
 * inverted index. Also create a user interface to do a search using that inverted
 * index which returns a list of files that contain the query term / terms.
 * The search index can be in memory.
 *

 */
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class DictEntry {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry() {
        postingList = new HashSet<Integer>();
    }
}

class InvertedIndex {

    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry> index; // THe inverted index

    InvertedIndex() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry>();
    }

    public void printPostingList(HashSet<Integer> pl) {
        pl.forEach(element -> System.out.print(element + ", "));
    }

    public void printDictionary() {
        System.out.println("------------------------------------------------------");
        System.out.println("*****    Number of terms = " + index.size());
        System.out.println("------------------------------------------------------");

    }

    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
        printDictionary();
    }


    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {

        HashSet<Integer> answer = pL1.stream()
                .distinct()
                .filter(pL2::contains).collect(Collectors.toCollection(HashSet::new));

        return answer;
    }

    public HashSet<Integer> query_union(HashSet<Integer> pL1, HashSet<Integer> pL2)
    {
        HashSet<Integer> answer = new HashSet<>();
        answer.addAll(pL1);
        answer.addAll(pL2);

        return answer;
    }

    HashSet<Integer> not(HashSet<Integer> pL)
    {
        HashSet<Integer> answer = new HashSet<Integer>(sources.keySet());

        answer.removeAll(pL);

        return answer;
    }


    String[] rearrange(String[] words, int[] freq, int len) {
        boolean sorted = false;
        int temp;
        String sTmp;
        for (int i = 0; i < len - 1; i++) {
            freq[i] = index.get(words[i].toLowerCase()).doc_freq;
        }
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < len - 1; i++) {
                if (freq[i] > freq[i + 1]) {
                    temp = freq[i];
                    sTmp = words[i];
                    freq[i] = freq[i + 1];
                    words[i] = words[i + 1];
                    freq[i + 1] = temp;
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

    public String find(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        for (String word : words) {
            res.retainAll(index.get(word).postingList);
        }
        if (res.size() == 0) {
            System.out.println("Not found");
            return "";
        }
        // String result = "Found in: \n";
        for (int num : res) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }

    public String find_for_2_terms(String phrase) { // 2 term phrase  2 postingsLists
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        //  printPostingList(pL1);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        //    printPostingList(pL2);

        // 3- apply the algorithm
        HashSet<Integer> answer = query_union(pL1, pL2);
        //printPostingList(answer);
//        result = "Found in: \n";
//        for (int num : answer) {
//            //System.out.println("\t" + sources.get(num));
//            result += "\t" + sources.get(num) + "\n";
//        }
        return result;
    }

    public String find_for_3_terms(String phrase) { // 3 lists

        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        //printPostingList(pL1);
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        //printPostingList(pL2);
        HashSet<Integer> answer1 = intersect(pL1, pL2);
        //printPostingList(answer1);
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);
        //printPostingList(pL3);
        HashSet<Integer> answer2 = query_union(pL3, answer1);
        // printPostingList(answer2);

//        result = "Found in: \n";
        for (int num : answer2) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + sources.get(num) + "\n";
        }
        return result;

    }

    public String find_for_any_terms_nonOp(String phrase) { // any mumber of terms non-optimized search
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;

        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        int i = 1;
        while (i < len) {
            res = query_union(res, index.get(words[i].toLowerCase()).postingList);
            i++;
        }
        for (int num : res) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + sources.get(num) + "\n";
        }
        return result;

    }

    public String find_documents(String phrase) { // any mumber of terms optimized search
        String result = "";
        ArrayList<String> query = new ArrayList<>(Arrays.asList(phrase.split("\\W+")));
        ArrayList<String> booleans = new ArrayList<>();

        for(int i=0; i<query.size(); i++)
        {
            if(query.get(i).equals("AND") ||
                    query.get(i).equals("OR") ||
                    query.get(i).equals("NOT"))
            {
                booleans.add(query.get(i));
                query.remove(query.get(i));
            }
        }

        query.forEach(System.out::println);
        System.out.println("\n");
        booleans.forEach(System.out::println);

//        int len = words.length;
//        words = rearrange(words, new int[len], len);
//        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
//        int i = 1;
//        while (i < len) {
//            res = query_union(res, index.get(words[i].toLowerCase()).postingList);
//            i++;
//        }
//        for (int num : res) {
//            //System.out.println("\t" + sources.get(num));
//            result += "\t" + sources.get(num) + "\n";
//        }
        return result;
    }



}

public class index {

    public static void main(String[] args) throws IOException {
        InvertedIndex index = new InvertedIndex();
        String phrase = "";
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

        index.find_documents("hello AND I OR am and nadah NOT khaled");
    }





}
