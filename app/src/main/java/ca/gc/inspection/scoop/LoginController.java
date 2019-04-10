package ca.gc.inspection.scoop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginController {


    /**
     *
     * @param email: email of the person logging in
     * @param password: password of the person logging in
     * @param context: context of activity
     * @param activity: activity
     */
    public static void loginUser(final String email, final String password, final Context context, final Activity activity){
        String URL = Config.baseIP + "signup/login"; //url for which the http request will be made, corresponding to Node.js code
        RequestQueue requestQueue = Volley.newRequestQueue(context); //setting up the request queue for volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() { //setting up the request as a post request
            @Override
            public void onResponse(String response) {
                Log.i("response", response); //gets the response sent back by the Node.js code

                if(response.contains("Success")){ //if login was successful it will go through this if statement
                    String[] c = response.split(" ");
                    Toast.makeText(context, c[0], Toast.LENGTH_SHORT).show(); //toast appears depending on the response message
                    SharedPreferences sharedPreferences = context.getSharedPreferences("ca.gc.inspection.scoop", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("userId", c[1]).apply();//store user id into application
                    Config.currentUser = c[1];
                    Intent intent = new Intent(context, mainScreen.class); //changes activities once login is successful
                    context.startActivity(intent);
                    activity.finish();
                }else{
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show(); //toast appears depending on the response message
                }

            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error info", error.toString()); //if there is an error, it will log the error

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email); //puts the required parameters into the Hashmap which is sent to Node.js code
                params.put("password", password);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy( //refrains Volley from sending information twice
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest); //adds the request to the request queue

    }
}