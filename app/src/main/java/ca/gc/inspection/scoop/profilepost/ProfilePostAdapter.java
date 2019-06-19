package ca.gc.inspection.scoop.profilepost;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;


import ca.gc.inspection.scoop.R;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostViewHolder> {

	private ProfilePostContract.Presenter.AdapterAPI mProfilePostPresenter;
    private ProfilePostFragment mProfilePostView;

    /**
     * Constructor for the adapter
     */
	public ProfilePostAdapter(ProfilePostFragment profileCommentView, ProfilePostContract.Presenter.AdapterAPI presenter) {
        mProfilePostView = profileCommentView;
        mProfilePostPresenter = presenter;
	}

    /**
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public ProfilePostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_profile_layout, viewGroup, false);
        return new ProfilePostViewHolder(v);
    }

    /**
     * Binds new data to the viewholder
     *
     * @param profilePostViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull ProfilePostViewHolder profilePostViewHolder, int i) {
        mProfilePostPresenter.onBindProfilePostViewHolderAtPosition(profilePostViewHolder, i);

        // TODO remove commented lines; add listeners?
//		mProfilePostPresenter = new ProfilePostPresenter(mProfilePostView, posts, images, i, profilePostViewHolder);
////        mProfilePostPresenter.getUserPosts(Config.currentUser);
//		try {
//			mProfilePostPresenter.displayPost();
//			mProfilePostPresenter.displayImages();
//			mProfilePostPresenter.formPostTitle();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }

	@Override
	public int getItemCount() {
		return mProfilePostPresenter.getItemCount();
	}

}
