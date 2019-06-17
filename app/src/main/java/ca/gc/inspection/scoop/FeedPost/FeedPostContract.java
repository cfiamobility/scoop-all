package ca.gc.inspection.scoop.FeedPost;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;

import ca.gc.inspection.scoop.MySingleton;
import ca.gc.inspection.scoop.ProfilePost.ProfilePostContract;
import ca.gc.inspection.scoop.base.BasePresenter;
import ca.gc.inspection.scoop.base.BaseView;


public interface FeedPostContract extends ProfilePostContract {

    interface View extends BaseView<Presenter> {
        void setPostImage(Bitmap image, FeedPostViewHolder holder);
        void hidePostImage(FeedPostViewHolder holder);
//        void setRecyclerView(JSONArray postResponse, JSONArray imageResponse);
        String getFeedType();
    }


    interface Presenter extends BasePresenter {
        void displayImages() throws JSONException;
        void formatImage(String image, String type);
//        void getPosts(MySingleton singleton);
        String getFeedType();

    }

}
