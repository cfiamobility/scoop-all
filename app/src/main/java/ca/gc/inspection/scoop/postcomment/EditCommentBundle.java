package ca.gc.inspection.scoop.postcomment;


import ca.gc.inspection.scoop.createpost.InteractorBundle;
import ca.gc.inspection.scoop.editpost.EditPostData;
import ca.gc.inspection.scoop.postcomment.PostCommentContract;

public class EditCommentBundle extends InteractorBundle {
    private PostCommentContract.View.ViewHolder mViewHolderInterface;
    private EditCommentData mEditCommentData;

    EditCommentBundle() {
        super();
    }

    public void setViewHolder(PostCommentContract.View.ViewHolder viewHolderInterface) {
        mViewHolderInterface = viewHolderInterface;
    }

    public PostCommentContract.View.ViewHolder getViewHolder() {
        return mViewHolderInterface;
    }

    public void setEditCommentData(EditCommentData editPostData) {
        mEditCommentData = editPostData;
    }

    public EditCommentData getEditCommentData() {
        return mEditCommentData;
    }
}