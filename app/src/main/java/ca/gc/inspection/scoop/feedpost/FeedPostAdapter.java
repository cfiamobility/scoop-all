package ca.gc.inspection.scoop.feedpost;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.gc.inspection.scoop.R;
import ca.gc.inspection.scoop.postcomment.PostCommentFragment;
import ca.gc.inspection.scoop.profilepost.ProfilePostFragment;
import ca.gc.inspection.scoop.util.NetworkUtils;

public class FeedPostAdapter extends RecyclerView.Adapter<FeedPostViewHolder>
    implements FeedPostContract.View.Adapter {
    /**
     * Adapter used to create ViewHolders and bind new data to them for a RecyclerView.
     * Considered to be part of the View.
     */

    private FeedPostContract.Presenter.AdapterAPI mFeedPostPresenter;
    private FeedPostContract.View mFeedPostView;    // current assumption: only implementing community feed fragment
    private NetworkUtils mNetworkUtil;

    /**
     * Constructor for the adapter
     * @param feedPostView needs to be of type FeedPostContract.View for multiple feeds
     */
    public FeedPostAdapter(FeedPostContract.View feedPostView,
                           FeedPostContract.Presenter.AdapterAPI presenter,
                           NetworkUtils network) {
        mFeedPostView = feedPostView;
        mFeedPostPresenter = presenter;
        mFeedPostPresenter.setAdapter(this);
        mNetworkUtil = network;
    }

    /**
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public FeedPostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        return new FeedPostViewHolder(view, (FeedPostContract.Presenter.ViewHolderAPI) mFeedPostPresenter);
    }

    /**
     * Binds new data to the viewholder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull FeedPostViewHolder feedPostViewHolder, int i) {
        mFeedPostPresenter.onBindViewHolderAtPosition(feedPostViewHolder, i);

        // TODO use inheritance and call super? - NOTE that either onBind in Adapter or Presenter
        // should call super but not both as it would cause the same information to be set to the view
        // multiple times
        PostCommentFragment.setDisplayPostListener(feedPostViewHolder);
        PostCommentFragment.setLikesListener(mNetworkUtil, feedPostViewHolder, i);
        PostCommentFragment.setUserInfoListener(feedPostViewHolder,
                mFeedPostPresenter.getPosterIdByIndex(i));
        ProfilePostFragment.setPostOptionsListener(feedPostViewHolder, mFeedPostPresenter.getActivityIdByIndex(i));
    }

    @Override
    public int getItemCount() {
        return mFeedPostPresenter.getItemCount();
    }

    // TODO remove unnecessary override?
    @Override
    public void refreshAdapter() {
        notifyDataSetChanged();
    }
}
