package com.student_eg.student_egcom;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.student_eg.student_egcom.adapter.TableListAdapter;
import com.student_eg.student_egcom.app.AppController;
import com.student_eg.student_egcom.data.FeedItem;
import com.student_eg.student_egcom.data.TableItem;
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

public class TableActivity extends AppCompatActivity {
    private static final String TAG = TableActivity.class.getSimpleName();
    private ListView listView;
    private TableListAdapter listAdapter;
    private List<TableItem> feedItems;
    private String URL_FEED = "http://credit.student-eg.com/api/timetable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        listView = (ListView) findViewById(R.id.list);

        feedItems = new ArrayList<TableItem>();

        listAdapter = new TableListAdapter(this, feedItems);
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
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(String response) {
        try {
            JSONArray feedArray = new JSONArray(response);

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                TableItem item = new TableItem();
                item.setId(feedObj.getInt("id"));
                item.setCourseName(feedObj.getString("course"));
                item.setInstructor(feedObj.getString("instructor"));
                item.setHall_or_place(feedObj.getString("hall"));
                item.setDuration(feedObj.getString("duration"));
                item.setDay_no(feedObj.getString("day_no"));
                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
