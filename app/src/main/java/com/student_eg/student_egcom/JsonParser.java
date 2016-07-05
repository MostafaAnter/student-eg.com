package com.student_eg.student_egcom;

import android.content.Context;
import android.content.SharedPreferences;

import com.student_eg.student_egcom.models.FavoriteModel;
import com.student_eg.student_egcom.models.FeedPOJO;
import com.student_eg.student_egcom.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mostafa on 20/03/16.
 */
public class JsonParser {
    public static List<FeedPOJO> parseJsonFeed(String feed){

        try {
            JSONObject  jsonRootObject = new JSONObject(feed);//done
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonNewsArray = jsonRootObject.optJSONArray("data");
            List<FeedPOJO> newsList = new ArrayList<>();
            for (int i = 0; i < jsonNewsArray.length(); i++) {
                JSONObject jsonObject = jsonNewsArray.getJSONObject(i);

                // retrieve all metadata
                String id = jsonObject.optString("id");
                String title = jsonObject.optString("title");
                String description = jsonObject.optString("description");
                String content = jsonObject.optString("content");
                String img = Constants.IMAGE_MAIN_URL + jsonObject.optString("img");
                String updated_at = jsonObject.optString("updated_at");

                // put all item inside one object
                FeedPOJO newsPojo = new FeedPOJO(id, title, updated_at,
                        description, content, null, img);
                newsList.add(newsPojo);
            }
            return newsList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static FavoriteModel parseForNotifecation(String feed, Context mContext){
        SharedPreferences favoritePrefs = mContext.getSharedPreferences("notify", Context.MODE_PRIVATE);
        try {
            JSONObject  jsonRootObject = new JSONObject(feed);//done
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonNewsArray = jsonRootObject.optJSONArray("data");
            JSONObject jsonObject = jsonNewsArray.getJSONObject(0);
            String title = jsonObject.optString("title");
            String id = jsonObject.optString("id");



            FavoriteModel favoriteModel = new FavoriteModel();
            favoriteModel.setTitleKey(title);


            if(favoritePrefs.getString(id, null) == null){
                SharedPreferences.Editor editor = favoritePrefs.edit();
                editor.putString(jsonObject.optString("id"), jsonObject.optString("title"));
                editor.commit();

                favoriteModel.setIdValue("first");

            }else {
                favoriteModel.setIdValue("second");
            }


            return favoriteModel;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



}
