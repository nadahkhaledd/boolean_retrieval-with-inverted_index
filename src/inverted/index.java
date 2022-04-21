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


    HashSet<Integer> intersect1(HashSet<Integer> pL1, HashSet<Integer> pL2) {

        HashSet<Integer> answer = new HashSet<Integer>();
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();

        int docId1 = 0, docId2 = 0;
        if (itP1.hasNext())
            docId1 = itP1.next();
        if (itP2.hasNext())
            docId2 = itP2.next();

        while (itP1.hasNext() && itP2.hasNext()) {

            if (docId1 == docId2) {
                answer.add(docId1);
                docId1 = itP1.next();
                docId2 = itP2.next();
            }
            else if (docId1 < docId2) {
                if (itP1.hasNext())
                    docId1 = itP1.next();
                else return answer;

            } else {
                if (itP2.hasNext())
                    docId2 = itP2.next();
                else return answer;
            }

        }
        if (docId1 == docId2) {
            answer.add(docId1);
        }

        return answer;
    }

    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {

        HashSet<Integer> answer = pL1.stream()
                .distinct()
                .filter(pL2::contains).collect(Collectors.toCollection(HashSet::new));

        return answer;
    }


    public HashSet<Integer> union1(HashSet<Integer> pL1, HashSet<Integer> pL2)
    {
        HashSet<Integer> answer = new HashSet<Integer>();
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();

        int docId1 = 0, docId2 = 0;
        if (itP1.hasNext())
            docId1 = itP1.next();
        if (itP2.hasNext())
            docId2 = itP2.next();

        answer.add(docId1);
        while (itP1.hasNext())
        {
            docId1 = itP1.next();
            answer.add(docId1);
        }

        answer.add(docId2);
        while(itP2.hasNext())
        {
            docId2 = itP2.next();
            answer.add(docId2);
        }

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


    ArrayList<String> rearrange(ArrayList<String> query, int[] freq, int len) {
        String[] words = new String[query.size()];
        for(int e=0; e<query.size(); e++)
        {
            words[e] = query.get(e);
        }
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
        return new ArrayList<String>(Arrays.asList(words));
    }

    public String find_documents(String phrase) { // any mumber of terms optimized search
        String result = "";
        ArrayList<String> query = new ArrayList<>(Arrays.asList(phrase.split("\\W+")));
        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> booleans = new ArrayList<>();

        for(int i=0; i<query.size(); i++)
        {
            if(query.get(i).equals("AND") ||
                    query.get(i).equals("OR") ||
                    query.get(i).equals("NOT"))
            {
                booleans.add(query.get(i));
            }
            else
                words.add(query.get(i));
        }

        //words = rearrange(words, new int[words.size()], words.size());

        HashSet<Integer> answer = new HashSet<Integer>(index.get(words.get(0).toLowerCase()).postingList);

        if(booleans.size()==1 && booleans.get(0).equals("NOT") && words.size()==1)
        {
            answer = not(answer);
        }
        for (int i=1; i<words.size(); i++)
        {
            while (booleans.size()!=0)
            {
                String nextOperation = booleans.get(0);
                switch (nextOperation) {
                    case "AND":
                    {
                        HashSet<Integer> temp = index.get(words.get(i).toLowerCase()).postingList;

                        if(booleans.size()>1 && booleans.get(1).equals("NOT"))
                        {
                            temp = not(temp);
                            booleans.remove(1);
                        }
                        //answer = intersect(answer, temp);
                        answer = intersect1(answer, temp);
                        break;
                    }

                    case "OR":
                    {
                        HashSet<Integer> temp = index.get(words.get(i).toLowerCase()).postingList;
                        if(booleans.size()>1 && booleans.get(1).equals("NOT"))
                        {
                            temp = not(temp);
                            booleans.remove(1);
                        }
                        //answer = query_union(answer, temp);
                        answer = union1(answer, temp);
                        break;
                    }
                }
                booleans.remove(0);
            }
        }

        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }

}

public class index {

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
        String phrase2 = "agile AND introduction";
        String phrase3 = "introduction AND NOT agile";

        String result = index.find_documents(phrase2);
        System.out.println(result);
    }

}
