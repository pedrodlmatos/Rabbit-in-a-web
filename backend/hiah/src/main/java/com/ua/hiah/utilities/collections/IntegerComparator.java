package com.ua.hiah.utilities.collections;

import java.util.Comparator;

public class IntegerComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer arg0, Integer arg1) {
        if (arg0 < arg1)
            return -1;
        else if (arg0 > arg1)
            return 1;
        else
            return 0;
    }

    public static int compare(int arg0, int arg1) {
        if (arg0 < arg1)
            return -1;
        else if (arg0 > arg1)
            return 1;
        else
            return 0;
    }
}
