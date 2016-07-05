package com.student_eg.student_egcom.fragments;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.student_eg.student_egcom.R;
import com.student_eg.student_egcom.models.FeedPOJO;
import com.student_eg.student_egcom.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;



/**
 * Created by mostafa on 08/03/16.
 */
public class DetailsFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private static FeedPOJO feedPOJO;

    // modify UI
    @Bind(R.id.main_title) TextView title;
    @Bind(R.id.timestamp) TextView timeStamp;
    @Bind(R.id.txtDescription) TextView description;
    @Bind(R.id.txtStatusMsg) TextView status;



    public DetailsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            feedPOJO = getArguments().getParcelable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            final CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {


                Picasso.with(getActivity()).load(feedPOJO.getImageUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        appBarLayout.setBackground(new BitmapDrawable(bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

                appBarLayout.setTitle("");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        title.setText(feedPOJO.getTitle());
        timeStamp.setText(Utils.manipulateDateFormat(feedPOJO.getTimeStamp()));
        description.setText(feedPOJO.getDescription());
        status.setText(feedPOJO.getContent());
        return view;
    }
}
