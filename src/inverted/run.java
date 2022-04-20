package inverted;

import java.io.IOException;
import java.util.*;

public class run {

    public static void main(String[] args) throws IOException
    {
        HashSet<Integer> union = new HashSet<Integer>();
        ArrayList<Integer> all = new ArrayList<Integer>();
        all.add(0);
        all.add(1);
        all.add(2);
        all.add(3);
        all.add(4);
        all.add(5);
        all.add(6);
        all.add(7);
        all.add(8);
        all.add(9);
        HashSet<Integer> not = new HashSet<Integer>();
        not.add(2);
        not.add(5);
        not.add(6);
        not.add(8);
        not.add(10);
        not.add(13);


        union.addAll(all);
        union.addAll(not);

        for(int i : union)
        {
            System.out.println(i);
        }


    }
}
