package com.example.billsyfy.ui.BillsGallery;

import java.util.Comparator;
import java.util.HashMap;

//Redifine comparison for string HashMap elements
class MapComparator implements Comparator<HashMap<String, String>>
{
    private final String key;
    private final String order;

    public MapComparator(String key, String order)
    {
        this.key = key;
        this.order = order;
    }

    public int compare(HashMap<String, String> first,
                       HashMap<String, String> second)
    {
        String firstValue = first.get(key);
        String secondValue = second.get(key);

        if(firstValue != null || secondValue != null)
            return 0;

        if(this.order.toLowerCase().contentEquals("asc"))
        {
            return firstValue.compareTo(secondValue);
        }else{
            return secondValue.compareTo(firstValue);
        }

    }
}