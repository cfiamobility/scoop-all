package ca.gc.inspection.scoop.profilecomment;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import ca.gc.inspection.scoop.Config;
import ca.gc.inspection.scoop.MySingleton;
import ca.gc.inspection.scoop.profilepost.ProfilePostContract;
import ca.gc.inspection.scoop.profilepost.ProfilePostPresenter;

import static ca.gc.inspection.scoop.Config.USERID_KEY;
import static ca.gc.inspection.scoop.profilecomment.ProfileComment.PROFILE_COMMENT_ACTIVITYID_KEY;
import static ca.gc.inspection.scoop.profilecomment.ProfileComment.PROFILE_COMMENT_LIKE_POSTERID_KEY;
import static ca.gc.inspection.scoop.profilecomment.ProfileComment.PROFILE_COMMENT_LIKE_TYPE_KEY;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class ProfileCommentInteractor {
    protected ProfileCommentPresenter mPresenter;

    /**
     * Empty constructor called by child classes (ie. ProfilePostInteractor) to allow them to set
     * their own presenter
     */
    public ProfileCommentInteractor() {
    }

    public ProfileCommentInteractor(ProfileCommentPresenter presenter){
        mPresenter = checkNotNull(presenter);
    }

    /**
     * HTTPRequests for comments and profile images
     * @param userid: userid
     */
    public void getUserCommentsAndImages(MySingleton singleton, final String userid) {
        String url = Config.baseIP + "profile/commenttextfill/" + userid + "/" + Config.currentUser;
        String responseUrl = Config.baseIP + "profile/commentimagefill/" + userid;
        JsonArrayRequest commentRequest = newProfileJsonArrayRequest(url, responseUrl);
        Config.requestQueue.add(commentRequest);
//        singleton.addToRequestQueue(commentRequest);
    }

    public JsonArrayRequest newProfileJsonArrayRequest(String url, String responseUrl) {
        return new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                final JsonArrayRequest imageRequest = new JsonArrayRequest(Request.Method.GET, responseUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray imagesResponse) {
                        // TODO: *** if called by ProfilePostInteractor, setData should call the overridden method ***
                        mPresenter.setData(response, imagesResponse);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }) {
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        // inserting the token into the response header that will be sent to the server
                        Map<String, String> header = new HashMap<>();
                        header.put("authorization", Config.token);
                        return header;
                    }
                };
                Config.requestQueue.add(imageRequest);
//                singleton.addToRequestQueue(imageRequest);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                // inserting the token into the response header that will be sent to the server
                Map<String, String> header = new HashMap<>();
                header.put("authorization", Config.token);
                return header;
            }
        };
    }

    /**
     * Description: updates likes in table and adds notifications if like type is 1
     * @param likeType : current like type
     * @param activityid : activity id of post
     * @param posterid : user id of poster of post
     * @param viewHolderInterface
     * @throws JSONException
     */
    public void updateLikes(
            MySingleton singleton, LikeState likeType, String likeCount, final String activityid, final String posterid,
            int i, ProfileCommentContract.View.ViewHolder viewHolderInterface) {

        Log.i("hello", "should be here");
        String URL = Config.baseIP + "display-post/updatelikes";
        //sends a PUT request to update new likes
        StringRequest request = newLikesStringRequest(
                Request.Method.PUT, URL, likeType, likeCount, activityid, posterid, i, viewHolderInterface);
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Config.requestQueue.add(request);
//        singleton.addToRequestQueue(request);
    }

    /**
     * Description: inserts likes in table and adds notifications if like type is 1
     * @param likeType : current like type
     * @param activityid : activity id of post
     * @param posterid : user id of poster of post
     * @param viewHolderInterface
     * @throws JSONException
     */
    public void insertLikes(
            MySingleton singleton, LikeState likeType, final String activityid, final String posterid,
            int i, ProfileCommentContract.View.ViewHolder viewHolderInterface) {

        Log.i("hello", "should be here");
        String URL = Config.baseIP + "display-post/insertlikes";
        String likeCount = "1";

        StringRequest request = newLikesStringRequest(
                Request.Method.POST, URL, likeType, likeCount, activityid, posterid, i, viewHolderInterface);
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Config.requestQueue.add(request);
//        singleton.addToRequestQueue(request);

    }

    /**
     * //sends a POST request to insert new like
     * @param requestMethod
     * @param URL
     * @param likeType
     * @param activityid
     * @param posterid
     * @param i
     * @param viewHolderInterface
     * @return
     */
    private StringRequest newLikesStringRequest(
            int requestMethod, String URL, LikeState likeType, String likeCount, final String activityid,
            final String posterid, int i, ProfileCommentContract.View.ViewHolder viewHolderInterface) {

        return new StringRequest(requestMethod, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                mPresenter.updateLikeState(viewHolderInterface, i, likeType);
                mPresenter.updateLikeCount(viewHolderInterface, i, likeCount);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Log.i("ProfileCommentInteractor", "getParams for insert/updateLikes");
                Map<String, String> params = new HashMap<>();
                params.put(PROFILE_COMMENT_LIKE_TYPE_KEY, likeType.getDatabaseValue());
                params.put(PROFILE_COMMENT_ACTIVITYID_KEY, activityid);
                params.put(PROFILE_COMMENT_LIKE_POSTERID_KEY, posterid);
                params.put(USERID_KEY, Config.currentUser);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // inserting the token into the response header that will be sent to the server
                Map<String, String> header = new HashMap<>();
                header.put("authorization", Config.token);
                return header;
            }
        };
    }

}
