package com.example.sumit.apple.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sumit on 8/18/2016.
 */
public class ExpandableListDataPump {


    private static final String CHAR_TEXT1 = "Temperament";
    private static final String CHAR_TEXT2 = "Barking";
    private static final String LIFE_SIZE_TEXT1 = "Max Male Height";
    private static final String LIFE_SIZE_TEXT2 = "Max Female Height";


    public static HashMap<String, List<List<String>>> getData(DogDetails dogDetails) {


        HashMap<String, List<List<String>>> expandableListDetail = new HashMap<>();

        //Characteristics Data
        List<String> charRow1 = new ArrayList<>();
        charRow1.add(CHAR_TEXT1);
        charRow1.add(dogDetails.getTemperament());

        List<String> charRow2 = new ArrayList<>();
        charRow2.add(CHAR_TEXT2);
        charRow2.add(dogDetails.getBarking());

        List<List<String>> characteristics = new ArrayList<>();
        characteristics.add(charRow1);
        characteristics.add(charRow2);

        //Life & Size Data
        List<String> lifeSizeRow1 = new ArrayList<>();
        lifeSizeRow1.add(dogDetails.getLife());

        List<String> lifeSizeRow2 = new ArrayList<>();
        lifeSizeRow2.add(LIFE_SIZE_TEXT1);
        lifeSizeRow2.add(dogDetails.getMaleHeight());

        List<String> lifeSizeRow3 = new ArrayList<>();
        lifeSizeRow3.add(LIFE_SIZE_TEXT2);
        lifeSizeRow3.add(dogDetails.getFemaleHeight());

        List<List<String>> lifeSize = new ArrayList<>();
        lifeSize.add(lifeSizeRow1);
        lifeSize.add(lifeSizeRow2);
        lifeSize.add(lifeSizeRow3);

        expandableListDetail.put("CHARACTERISTICS", characteristics);
        expandableListDetail.put("LIFE & SIZE", lifeSize);

        return expandableListDetail;
    }
}