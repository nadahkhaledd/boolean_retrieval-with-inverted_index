package inverted;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class run {

    public static void main(String[] args) throws IOException
    {
        HashSet<Integer> union = new HashSet<Integer>();
        ArrayList<Integer> all = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        HashSet<Integer> not = new HashSet<Integer>(Arrays.asList(2, 5, 6, 8, 10, 15));

//        union.addAll(all);
//        union.addAll(not);

        Set<Integer> result = not.stream()
                .distinct()
                .filter(all::contains)
                .collect(Collectors.toSet());

        union.addAll(result);

        for(int i : union)
        {
            System.out.println(i);
        }


    }
}
