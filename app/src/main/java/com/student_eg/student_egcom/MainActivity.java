package com.student_eg.student_egcom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.student_eg.student_egcom.adapter.FeedListAdapter;
import com.student_eg.student_egcom.app.AppController;
import com.student_eg.student_egcom.arabic.ArabicUtilities;
import com.student_eg.student_egcom.data.FeedItem;
import com.student_eg.student_egcom.news.Main2Activity;
import com.student_eg.student_egcom.utils.Constants;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // ui elements
    private static TextView user_name, user_score;
    private static CircleImageView user_avatar;

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "http://credit.student-eg.com/api/timeline";

    //student data
    private String url = "http://credit.student-eg.com/api/basic";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // to access item inside header
        View header = navigationView.getHeaderView(0);
        user_name = (TextView) header.findViewById(R.id.user_name);
        user_score = (TextView) header.findViewById(R.id.score_text);
        user_avatar = (CircleImageView) header.findViewById(R.id.user_pic);

        //get old view until data reloaded
        updateViews();


        /*populate list that cary feed*/
        listView = (ListView) findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);


        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                parseJsonFeed(data);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // Restore preferences to get user id
            final SharedPreferences settings = getSharedPreferences(Constants.STUDENT_EG_PREF, 0);
            // Tag used to cancel the request
            String tag_string_req = "string_req";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    URL_FEED, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    //to decode unicode
                    response = StringEscapeUtils.unescapeJava(response);
                    parseJsonFeed(response);



                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                }
            }) {


                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userId", String.valueOf(settings.getLong(Constants.USER_ID, -1)));
                    params.put("token", Constants.APP_TOKEN);

                    return params;

                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq);
        }

        // load user data
        load_user_data();

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void load_user_data() {
        // Restore preferences to get user id
        final SharedPreferences settings = getSharedPreferences(Constants.STUDENT_EG_PREF, 0);

        if (isOnline()) {

            // make request by volley
            String tag_string_req = "json_obj_req";
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    response = StringEscapeUtils.unescapeJava(response);
                    parseUserData(response);
                    Log.d("responce", response);
                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userId", String.valueOf(settings.getLong(Constants.USER_ID, -1)));
                    params.put("token", Constants.APP_TOKEN);
                    return params;
                }
            };


            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


        } else {

        }


    }

    private void parseUserData(String response) {
        try {
            JSONObject  jsonRootObject = new JSONObject(response);
            String name = jsonRootObject.optString("full_name").toString();
            String avatar = jsonRootObject.optString("avatar").toString();
            avatar = "http://credit.student-eg.com/" + avatar;
            String gpa = jsonRootObject.optString("gpa").toString();

            // save data
            SharedPreferences settings = getSharedPreferences(Constants.STUDENT_EG_PREF, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(Constants.USER_NAME, name);
            editor.putString(Constants.USER_AVATAR, avatar);
            editor.putString(Constants.USER_GPA, gpa);
            editor.commit();

            updateViews();

        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    private void updateViews() {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(Constants.STUDENT_EG_PREF, 0);
        String name = settings.getString(Constants.USER_NAME, "username");
        String avatar = settings.getString(Constants.USER_AVATAR, "n");
        String gpa = settings.getString(Constants.USER_GPA, "000");


        // set ui
        user_name.setText(ArabicUtilities.reshape(name));
        user_score.setText(gpa + " points");
        if(!avatar.equalsIgnoreCase("n")){
            Picasso.with(MainActivity.this)
                    .load(avatar)
                    .resize(100, 100)
                    .placeholder(R.drawable.profile)
                    .centerCrop()
                    .into(user_avatar);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_table) {
            // Handle the table
            startActivity(new Intent(MainActivity.this, TableActivity.class));
        }else if(id == R.id.action_material){
            startActivity(new Intent(MainActivity.this, MatrialActivity.class));

        }else if(id == R.id.action_ask){
            startActivity(new Intent(MainActivity.this, AskActivity.class));

        }else if(id == R.id.action_blogger){
            startActivity(new Intent(MainActivity.this, BloggerActivity.class));

        }else if(id == R.id.news){
            startActivity(new Intent(MainActivity.this, Main2Activity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(String response) {
        try {
            JSONArray feedArray = new JSONArray(response);

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("full_name"));

                // Image might be null sometimes
                String image = feedObj.isNull("img") ? null : feedObj
                        .getString("img");
                item.setImge("http://credit.student-eg.com/" + image);
                item.setStatus(feedObj.getString("content"));
                item.setProfilePic("http://credit.student-eg.com/" + feedObj.getString("pic"));
                item.setTimeStamp(feedObj.getString("created_at"));
                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
