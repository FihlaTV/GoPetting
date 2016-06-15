package com.example.sumit.apple.network;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.StringLoader;
import com.example.sumit.apple.R;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by Sumit on 5/12/2016.
 */
public class RetrofitServiceGenerator {

    public static final String API_BASE_URL = "http://ec2-54-169-7-179.ap-southeast-1.compute.amazonaws.com/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }


    //TODO: Commenting out, will be working on AWS authentication later.

//    public static <S> S createService(Class<S> serviceClass, String aKey, String sKey) {
//        if (aKey != null && sKey != null) {
//            String credentials = username + ":" + password;
//            final String basic =
//                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//
//            httpClient.addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Interceptor.Chain chain) throws IOException {
//                    Request original = chain.request();
//
//                    Request.Builder requestBuilder = original.newBuilder()
//                            .header("Authorization", basic)
//                    .header("Accept", "application/json")
//                    .method(original.method(), original.body());
//
//                    Request request = requestBuilder.build();
//                    return chain.proceed(request);
//                }
//            });
//        }


    public interface RetrofitService {
        @GET("/dog_select.php")
        Call<List<Dog>> getJsonData();
    }

    public static class Dog extends AbstractItem<Dog, Dog.ViewHolder> {

        @SerializedName("item_id")
        private int itemId;

        private String name;

        @SerializedName("unit_price")
        private String unitPrice;

        private String mrp;

        private String discount;

        private int likes;

        @SerializedName("image_url")
        private String imageUrl;


        //Adding and Modifying code as required for FastAdapter



            //The unique ID for this type of item
            @Override
            public int getType() {
                return R.id.fastadapter_dog_item_id;
            }

            //The layout to be used for this type of item
            @Override
            public int getLayoutRes() {
                return R.layout.item_dog;
            }

            //The logic to bind your data to the view
            @Override
            public void bindView(ViewHolder viewHolder) {
                //call super so the selection is already handled for you
                super.bindView(viewHolder);

                //get the context
                Context ctx = viewHolder.itemView.getContext();

                Glide.with(ctx).load(imageUrl).placeholder(R.drawable.ic_shopping_placeholder).into(viewHolder.mItemImageView);

                viewHolder.mItemName.setText(name);
                viewHolder.mItemPrice.setText(unitPrice);

                viewHolder.mItemMRP.setText(mrp);
                viewHolder.mItemMRP.setPaintFlags(viewHolder.mItemMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // To strike through text

                viewHolder.mItemDiscount.setText(discount);

//                viewHolder.mItemlikes.setText(String.valueOf(likes));


            }

            //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
            protected static class ViewHolder extends RecyclerView.ViewHolder {
                protected ImageView mItemImageView;
                protected TextView mItemName;
                protected TextView mItemPrice;
                protected TextView mItemMRP;
                protected TextView mItemDiscount;
//                protected TextView mItemlikes;


                public ViewHolder(View view) {
                    super(view);
                    this.mItemImageView = (ImageView) view.findViewById(R.id.iv_product_list_item);
                    this.mItemName = (TextView) view.findViewById(R.id.tv_item_name);
                    this.mItemPrice = (TextView) view.findViewById(R.id.tv_item_unit_price);
                    this.mItemMRP = (TextView) view.findViewById(R.id.tv_item_mrp);
                    this.mItemDiscount = (TextView) view.findViewById(R.id.tv_item_discount);
//                    this.mItemlikes = (TextView) view.findViewById(R.id.tv_item_likes);

                }
            }
        }


    }




