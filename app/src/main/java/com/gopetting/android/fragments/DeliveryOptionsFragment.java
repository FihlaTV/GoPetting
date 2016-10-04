package com.gopetting.android.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gopetting.android.R;
import com.gopetting.android.models.Credential;
import com.gopetting.android.models.DeliveryDetails;
import com.gopetting.android.network.Controller;
import com.gopetting.android.network.OAuthTokenService;
import com.gopetting.android.network.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sumit on 8/23/2016.
 */
public class DeliveryOptionsFragment extends Fragment{


    public static final String ITEM_ID = "item_id";

    private EditText mEditText;
    private Button mButton;
    private TextView mDeliveryText1;
    private TextView mDeliveryText2;
    private int itemId;
    private int mPincode;
    private ProgressBar mProgressBar;

    public static DeliveryOptionsFragment newInstance(int itemId) {
        DeliveryOptionsFragment fragment = new DeliveryOptionsFragment();
        Bundle args = new Bundle();
        args.putInt(ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_delivery_options, container, false);

        Bundle bundle=getArguments();
        //TODO: Optimize this null handling
        if (bundle.getInt(ITEM_ID,9999)==9999){             //9999 is just a random default value to check null
            Toast.makeText(getActivity(),"ITEM_ID is null in DeliveryOptionsFragment", Toast.LENGTH_SHORT).show();
        }
        itemId = bundle.getInt(ITEM_ID);

        mEditText   = (EditText) rootView.findViewById(R.id.et_pincode_text);
        mButton = (Button) rootView.findViewById(R.id.btn_check);
        mDeliveryText1 = (TextView) rootView.findViewById(R.id.tv_delivery_text1);
        mDeliveryText2 = (TextView) rootView.findViewById(R.id.tv_delivery_text2);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_pincode);

//      Hiding both text views and space consumed by them initially
        mDeliveryText1.setVisibility(View.GONE);
        mDeliveryText2.setVisibility(View.GONE);

        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (mEditText.getText().toString().trim().length() <= 5) {

                    mDeliveryText1.setVisibility(View.VISIBLE);
                    mDeliveryText1.setText("Please enter a valid pincode");
                    mDeliveryText1.setTextColor(Color.RED);
                    mDeliveryText2.setVisibility(View.GONE);

                } else {
//                    Toast.makeText(getActivity(), mEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPincode = Integer.parseInt(mEditText.getText().toString());
                    checkDeliveryDetails();

                }
            }
        });

        return rootView;
    }


    private void checkDeliveryDetails() {

        final OAuthTokenService oAuthTokenService = OAuthTokenService.getInstance(getActivity());

//        oAuthTokenService.deleteTokenWithId("default");
//          oAuthTokenService.deleteAllToken();
        Credential credential = oAuthTokenService.getAccessTokenWithID("default");

        if(credential == null || credential.getAccess_token()==null || oAuthTokenService.isExpired("default"))
        {
            oAuthTokenService.authenticateUsingOAuth( new Controller.MethodsCallback<Credential>()
            {
                @Override public void failure(Throwable throwable)
                {
                    Toast.makeText(getActivity(), throwable.getMessage(),Toast.LENGTH_SHORT).show();       //TODO: Change this to some appropriate statement like 'Log'
                }
                @Override public void success(Credential credential)
                {
                    if(credential != null)
                    {
                        oAuthTokenService.saveTokenWithID(credential, "default");

                        getData(credential);

                    }
                }
                @Override public void responseBody(Call<Credential> call)
                {

                }
            });
        }else {

            getData(credential);

        }
    }


    public void getData(Credential credential) {

        Controller.CheckDeliveryDetails retrofitSingleton = RetrofitSingleton.getInstance().create(Controller.CheckDeliveryDetails.class);
        Call<DeliveryDetails> call = retrofitSingleton.checkDeliveryDetails("Bearer " + credential.getAccess_token(), itemId, mPincode);
        call.enqueue(new Callback<DeliveryDetails>() {
            @Override
            public void onResponse(Call<DeliveryDetails> call, Response<DeliveryDetails> response) {
                if (response.isSuccessful()) {

                    populateDetailsInLayout(response.body());


                } else {
                    Log.d("Error Response", "DeliveryOptionsFragment :Error Response");
                }
            }

            @Override
            public void onFailure(Call<DeliveryDetails> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show(); //TODO: Change this to some appropriate statement like 'Log'
            }
        });

    }


    private void populateDetailsInLayout(DeliveryDetails body) {

        if (body.getQueryStatus().equals("Failed")){    //No Data received from server

            mDeliveryText1.setVisibility(View.VISIBLE);
            mDeliveryText2.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.INVISIBLE);

            mDeliveryText1.setText("Unfortunately, we do not deliver to this pincode");
            mDeliveryText1.setTextColor(Color.RED);

        }else {
            mDeliveryText1.setVisibility(View.VISIBLE);
            mDeliveryText2.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);

            mDeliveryText1.setText("Normal Delivery: " + body.getDeliveryDays() + "days");
            mDeliveryText1.setTextColor(Color.parseColor("#ff696e79"));
            mDeliveryText2.setText(body.getOtherDetails());
        }

    }


}

