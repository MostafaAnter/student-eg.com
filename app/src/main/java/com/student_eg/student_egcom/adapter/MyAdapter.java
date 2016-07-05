package com.student_eg.student_egcom.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.student_eg.student_egcom.FavoriteStore;
import com.student_eg.student_egcom.R;
import com.student_eg.student_egcom.fragments.DetailsFragment;
import com.student_eg.student_egcom.fragments.ItemsFragment;
import com.student_eg.student_egcom.models.FavoriteModel;
import com.student_eg.student_egcom.models.FeedPOJO;
import com.student_eg.student_egcom.news.DetailsActivity;
import com.student_eg.student_egcom.news.Main2Activity;
import com.student_eg.student_egcom.utils.SquaredImageView;
import com.student_eg.student_egcom.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;



/**
 * Created by mostafa on 11/03/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "CustomAdapter";
    private static Context mContext;
    private List<FeedPOJO> mDataSet;

    // manage enter animate
    private static final int ANIMATED_ITEMS_COUNT = 2; // number of item that animated is 1
    private int lastAnimatedPosition = -1;

    // manage like animations
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();

    // put control on one item selected
    private int lastCheckedPosition = -1;




    /**
     * Initialize the constructor of the Adapter.
     *
     * @param mDataSet String[] containing the data to populate views to be used by RecyclerView.
     * @param mContext Context hold context
     */
    public MyAdapter(Context mContext, List<FeedPOJO> mDataSet) {
        this.mDataSet = mDataSet;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        lastCheckedPosition = holder.getPosition();
        notifyItemRangeChanged(0, mDataSet.size());

        if (!likedPositions.contains(holder.getPosition())) {
            likedPositions.add(holder.getPosition());
            updateHeartButton(holder, true);
        }
        // add to my database
        addItem(holder.getPosition());

    }

    /**
     * Provide a reference to the type of views (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.main_title) TextView mainTitel;
        @Bind(R.id.timestamp) TextView timeStamp;
        @Bind(R.id.txtStatusMsg) TextView textStatusMsg;
        @Bind(R.id.txtUrl) TextView textUrl;
        @Bind(R.id.feedImage1)
        SquaredImageView imageView;
        @Bind(R.id.txtDescription) TextView description;
        @Bind(R.id.favorite_button) ImageButton favorite;
        @Bind(R.id.progressBar) ProgressBar mProgress;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                    if (Main2Activity.mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(DetailsFragment.ARG_ITEM_ID, mDataSet.get(getPosition()));
                        DetailsFragment fragment = new DetailsFragment();
                        fragment.setArguments(arguments);
                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DetailsActivity.class);
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(DetailsFragment.ARG_ITEM_ID, mDataSet.get(getPosition()));
                        intent.putExtras(arguments);
                        context.startActivity(intent);

                    }
                }
            });
        }

        public TextView getMainTitel() {
            return mainTitel;
        }

        public TextView getTimeStamp() {
            return timeStamp;
        }

        public TextView getTextStatusMsg() {
            return textStatusMsg;
        }

        public TextView getTextUrl() {
            return textUrl;
        }

        public SquaredImageView getImageView() {
            return imageView;
        }

        public ImageButton getFavorite() {
            return favorite;
        }


        public TextView getDescription() {
            return description;
        }

        public ProgressBar getProgressBar(){
            return mProgress;
        }

    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_forecast, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        // run enter animation
        runEnterAnimation(viewHolder.itemView, position);

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getMainTitel().setText(mDataSet.get(position).getTitle());
        viewHolder.getTimeStamp().setText(Utils.manipulateDateFormat(mDataSet.get(position).getTimeStamp()));
        viewHolder.getDescription().setText(mDataSet.get(position).getDescription());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(mDataSet.get(position).getContent())) {
            viewHolder.getTextStatusMsg().setText(mDataSet.get(position).getContent());
            viewHolder.getTextStatusMsg().setVisibility(View.VISIBLE);
            if (ItemsFragment.type == 0) {
                viewHolder.getTextStatusMsg().setVisibility(View.GONE);
            }

        } else {
            // status is empty, remove from view
            viewHolder.getTextStatusMsg().setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (mDataSet.get(position).getLinkAttachedWithContent() != null) {
            viewHolder.getTextUrl().setText(Html.fromHtml("<a href=\"" + mDataSet.get(position).getLinkAttachedWithContent() + "\">"
                    + mDataSet.get(position).getLinkAttachedWithContent() + "</a> "));
            // Making url clickable
            viewHolder.getTextUrl().setMovementMethod(LinkMovementMethod.getInstance());
            if (ItemsFragment.type == 0) {
                viewHolder.getTextUrl().setVisibility(View.GONE);
            }

        } else {
            // url is null, remove from the view
            viewHolder.getTextUrl().setVisibility(View.GONE);
        }

        // Feed image
        if (mDataSet.get(position).getImageUrl() != null) {
            // show progressBar
            viewHolder.getProgressBar().setVisibility(View.VISIBLE);
            // Adapter re-use is automatically detected and the previous download canceled.
            Picasso.with(mContext).load(mDataSet.get(position).getImageUrl())
                    .placeholder(R.drawable.rectangle)
                    .into(viewHolder.getImageView(), new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (viewHolder.getProgressBar() != null) {
                                viewHolder.getProgressBar().setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }
        else {
            viewHolder.getImageView().setVisibility(View.GONE);
        }

        //viewHolder.getImageView().setImageBitmap();

        // like button
        viewHolder.getFavorite().setOnClickListener(this);
        viewHolder.getFavorite().setTag(viewHolder);
        if(position == lastCheckedPosition) {
            viewHolder.getFavorite().setImageResource(R.drawable.ic_favorite_24dp);
        }else if (new FavoriteStore(mContext).findItem(mDataSet.get(position).getId(),
                mDataSet.get(position).getTitle())){
            // this item is in my database
            viewHolder.getFavorite().setImageResource(R.drawable.ic_favorite_24dp);
        }else {
            viewHolder.getFavorite().setImageResource(R.drawable.ic_favorite_outline_24dp);
        }
    }

    private void addItem(int position) {
        //add item to favorite
        FavoriteModel item = new FavoriteModel();
        item.setTitleKey(mDataSet.get(position).getTitle());
        item.setIdValue(mDataSet.get(position).getId());
        new FavoriteStore(mContext).update(item);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // manage enter animation function
    private void runEnterAnimation(View view, int position) {
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(mContext));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    // manage animate like button
    private void updateHeartButton(final ViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.getFavorite(), "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.getFavorite(), "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.getFavorite(), "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.getFavorite().setImageResource(R.drawable.ic_favorite_24dp);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.getFavorite().setImageResource(R.drawable.ic_favorite_24dp);
            } else {
                holder.getFavorite().setImageResource(R.drawable.ic_favorite_outline_24dp);
            }
        }
    }

    private void resetLikeAnimationState(ViewHolder holder) {
        likeAnimations.remove(holder);
    }





}
