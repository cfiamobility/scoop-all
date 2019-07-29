package ca.gc.inspection.scoop.postcomment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.gc.inspection.scoop.postoptionsdialog.PostOptionsDialogReceiver;
import ca.gc.inspection.scoop.searchprofile.UserProfileListener;
import ca.gc.inspection.scoop.util.CameraUtils;
import ca.gc.inspection.scoop.R;
import ca.gc.inspection.scoop.util.TextFormat;

import static ca.gc.inspection.scoop.createpost.CreatePostActivity.getTextWatcher;
import static ca.gc.inspection.scoop.searchprofile.view.SearchProfileViewHolder.getSpannableStringBuilderWithFormat;

/**
 * ViewHolder for replying to a post action; it is the most generic View Holder
 * and contains the minimum views (no comment count, options menus, or images)
 * related to "posting" actions. Parent View Holder for ProfilePostViewHolder.
 */

public class PostCommentViewHolder extends RecyclerView.ViewHolder implements
        PostCommentContract.View.ViewHolder,
        UserProfileListener,
        PostOptionsDialogReceiver.EditCommentReceiver {

    PostCommentContract.Presenter.ViewHolderAPI mPresenter;

    public TextView username, date, postText, likeCount, editText, counter;
    public ImageView profileImage, upvote, downvote, editButton, cancelButton;
    public ImageView optionsMenu;
    public Boolean savedStatus;

    protected TextWatcher mTextEditorWatcher;

    public PostCommentViewHolder(View v, PostCommentContract.Presenter.ViewHolderAPI presenter) {
        super(v);
        username = v.findViewById(R.id.name);
        profileImage = v.findViewById(R.id.profile_image);
        date = v.findViewById(R.id.date);
        postText = v.findViewById(R.id.post_text);
        likeCount = v.findViewById(R.id.vote_count);
        upvote = v.findViewById(R.id.up_vote);
        downvote = v.findViewById(R.id.down_vote);
        optionsMenu = v.findViewById(R.id.options_menu);

        // edit comment
        setupEditComment(v);

        mPresenter = presenter;
    }

    protected void setupEditComment(View v) {
        counter = v.findViewById(R.id.edit_post_text_counter);
        mTextEditorWatcher= getTextWatcher(counter);
        editText = v.findViewById(R.id.edit_post_text);
        editText.addTextChangedListener(mTextEditorWatcher);
        editButton = v.findViewById(R.id.edit_post_text_btn);
        cancelButton = v.findViewById(R.id.edit_post_cancel_btn);
        cancelButton.setOnClickListener(view -> hideEditText());
        hideEditText();
    }

    private void hideEditText() {
        if (editText != null && editButton != null && counter != null) {
            postText.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            counter.setVisibility(View.GONE);
        }
    }

    private void showEditText() {
        if (editText != null && editButton != null && counter != null) {
            postText.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            counter.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * @param postText
     */
    @Override
    public PostCommentContract.View.ViewHolder setPostText(String postText) {
        this.postText.setText(postText);
        return this;
    }

    /**
     *
     * @param postText
     */
    @Override
    public PostCommentContract.View.ViewHolder setPostTextWithFormat(String postText, TextFormat textFormat) {
        SpannableStringBuilder spannableStringBuilder = getSpannableStringBuilderWithFormat(postText, textFormat);
        this.postText.setText(spannableStringBuilder);
        return this;
    }

    /**
     *
     * @param userName
     */
    @Override
    public PostCommentContract.View.ViewHolder setUserName(String userName) {
        username.setText(userName);
        return this;
    }

    /**
     *
     * @param likeCount
     */
    @Override
    public PostCommentContract.View.ViewHolder setLikeCount(String likeCount) {
        if(likeCount.equals("null"))
            likeCount = "0";
        this.likeCount.setText(likeCount);
        return this;
    }

    /**
     *
     * @param date
     */
    @Override
    public PostCommentContract.View.ViewHolder setDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //formats the date accordingly
            Date parsedDate = dateFormat.parse(date); //parses the created date to be in specified date format
            DateFormat properDateFormat = new SimpleDateFormat("MM-dd-yy"); //formats the date to be how we want it to output
            this.date.setText(properDateFormat.format(parsedDate));
            return this;
        } catch(Exception e){
            e.printStackTrace();
            return hideDate();
        }
    }

    @Override
    public PostCommentContract.View.ViewHolder setLikeState(LikeState likeState) {
        if (likeState == null)
            return this;

        Log.i("likestate: ", likeState.getDatabaseValue());

        switch (likeState) {
            case UPVOTE:
                setLikeUpvoteState();
                break;
            case NEUTRAL:
                setLikeNeutralState();
                break;
            case DOWNVOTE:
                setLikeDownvoteState();
                break;
            default:
                setLikeNeutralState();
                break;
        }
        return this;
    }

    private void setLikeDownvoteState() {
        upvote.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP); //sets upvote color to black
        downvote.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP); //sets downvote color to blue
    }

    private void setLikeNeutralState() {
        upvote.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP); //sets upvote color to black
        downvote.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP); //sets downvote color to black
    }

    private void setLikeUpvoteState() {
        upvote.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP); //sets upvote color to red
        downvote.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP); //sets downvote color to black
    }

    @Override
    public PostCommentContract.View.ViewHolder hideDate() {
        date.setVisibility(View.GONE);
        return this;
    }

    /**
     * Description: changes image from a string to a bitmap, then setting image
     * @param image: image to convert
     */
    @Override
    public PostCommentContract.View.ViewHolder setUserImageFromString(String image){
        if (image != null && !image.isEmpty()) {
            Bitmap bitmap = CameraUtils.stringToBitmap(image); //converts image string to bitmap
            Log.i("image", image);
            profileImage.setImageBitmap(bitmap);
        }
        return this;
    }

    public void changeUpvoteLikeState(int i) {
        try {
            mPresenter.changeUpvoteLikeState(this, i);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeDownvoteLikeState(int i) {
        try {
            mPresenter.changeDownvoteLikeState(this, i);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSavedStatus(int i, Boolean savedStatus){
        try {
            mPresenter.updateSavedStatus(this, i, savedStatus);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public PostCommentContract.View.ViewHolder setSavedStatus(Boolean savedStatus) {
        this.savedStatus = savedStatus;
        return this;
    }

    public Boolean getSavedStatus(){
        return savedStatus;
    }



    @Override
    public TextView getUserName() {
        return username;
    }

    @Override
    public ImageView getProfileImage() {
        return profileImage;
    }

    @Override
    public void onEditPostComment(int i) {
        editText.setText(postText.getText());
        showEditText();
    }
}
