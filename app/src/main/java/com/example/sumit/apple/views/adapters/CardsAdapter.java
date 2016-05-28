package com.example.sumit.apple.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sumit.apple.models.Card;
import com.example.sumit.apple.R;

import java.util.List;

/**
 * Created by Sumit on 2/21/2016.
 */

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class CardsAdapter extends
        RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView photoImageView;
        public TextView nameTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            photoImageView = (ImageView) itemView.findViewById(R.id.product_photo);
            nameTextView = (TextView) itemView.findViewById(R.id.product_name);


            //Start Another activity
/*            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("CardsAdapter", "Element clicked.");
                    Toast.makeText(view.getContext(), "os version is: ", Toast.LENGTH_SHORT).show();
                    view.getContext().startActivity(new Intent(view.getContext(), DogShoppingActivity.class));

                }
            });*/



        }


    }

    // Store a member variable for the contacts
    private List<Card> mCards;

    // Pass in the card array into the constructor
    public CardsAdapter(List<Card> cards) {
        mCards = cards;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public CardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();          //TODO: Check why it's parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View cardView = inflater.inflate(R.layout.item_card, parent, false);

        cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.d("CardsAdapter", "Element clicked.");
                    Toast.makeText(view.getContext(), "os version is: ", Toast.LENGTH_SHORT).show();
//                    view.getContext().startActivity(new Intent(view.getContext(), DogShoppingActivity.class));

                }
            });

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CardsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Card card = mCards.get(position);

        // Set item views based on the data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(card.getName());

        // Set item views based on the data model
        ImageView imageView = viewHolder.photoImageView;
        imageView.setImageResource(card.getPhoto());

    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mCards.size();
    }
}