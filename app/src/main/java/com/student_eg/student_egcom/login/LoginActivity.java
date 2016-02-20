package com.student_eg.student_egcom.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.student_eg.student_egcom.MainActivity;
import com.student_eg.student_egcom.R;
import com.student_eg.student_egcom.app.AppController;
import com.student_eg.student_egcom.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    // ui elements
    private String TAG = LoginActivity.class.getSimpleName();
    private EditText inputMail, inputPassword;
    private TextInputLayout inputLayoutMail, inputLayoutPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* hide collapsing title */
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();
            }
        });

        inputLayoutMail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputMail = (EditText) findViewById(R.id.txt_email);
        inputPassword = (EditText) findViewById(R.id.txt_password);

        inputMail.addTextChangedListener(new MyTextWatcher(inputMail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.txt_email:
                    validateEmail();
                    break;
                case R.id.txt_password:
                    validatePassword();
                    break;
            }
        }
    }

    // Validating email
    private boolean validateEmail() {
        String email = inputMail.getText().toString().trim();

        if (email.isEmpty()) {
            inputLayoutMail.setError(getString(R.string.error_invalid_email));
            requestFocus(inputMail);
            return false;
        } else {
            inputLayoutMail.setErrorEnabled(false);
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Validating password
    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()
                || inputPassword.getText().toString().length() < 4) {
            inputLayoutPassword.setError(getString(R.string.error_invalid_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    //login
    private void login() {
        if (validateEmail() && validatePassword()) {
            if (isOnline()) {
                requestData();


            } else {
                // show error message
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Please check your Network connection!")
                        .show();
            }

        } else {

        }
    }

    private void requestData() {
        // Set up a progress dialog
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        // Tag used to cancel the request
        String tag_string_req = "string_req";
        String url = "http://credit.student-eg.com/api/login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                pDialog.hide();
                parseFeed(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user-name", inputMail.getText().toString().trim());
                params.put("password", inputPassword.getText().toString().trim());
                params.put("token", Constants.APP_TOKEN);

                return params;

            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    /* parse feed and store id */
    private void parseFeed(String strJson) {
        /* parse feed */
        JSONObject jsonRootObject = null;
        try {
            jsonRootObject = new JSONObject(strJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int id = Integer.parseInt(jsonRootObject.optString("id").toString());

        //debug
        Log.d("user_id", "" + id);

        // store user id
        SharedPreferences settings = getSharedPreferences(Constants.STUDENT_EG_PREF, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(Constants.USER_ID, id);

        // Commit the edits!
        editor.commit();

        // go to main activity
        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}
