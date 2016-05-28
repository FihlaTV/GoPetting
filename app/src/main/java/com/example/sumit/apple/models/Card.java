package com.example.sumit.apple.models;

import com.example.sumit.apple.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 2/21/2016.
 */
public class Card {
    String product;
    int photo;

    Card(String product,int photo) {
        this.product = product;
        this.photo = photo;
    }

    public String getName() {
        return product;
    }

    public int getPhoto() {
        return photo;
    }

    // This method creates an ArrayList that has three Card objects
    public static List<Card> createCardsList() {
        List<Card> cards = new ArrayList<>();      //This is similar to new ArrayList<Card>();
        cards.add(new Card("Dog", R.drawable.placeholder_offers));
        cards.add(new Card("Cat", R.drawable.placeholder_offers));
        cards.add(new Card("Fish", R.drawable.placeholder_offers));

        return cards;
    }


}



