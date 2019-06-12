package ca.gc.inspection.scoop;

import ca.gc.inspection.scoop.base.BasePresenter;
import ca.gc.inspection.scoop.base.BaseView;

public interface CreatePostContract {
    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {

        void sendPostToDatabase(NetworkUtils network, final String userId, final String title, final String text, final String imageBitmap);
    }
}
