package ca.gc.inspection.scoop.notif.notificationstoday;

import android.graphics.Bitmap;

import ca.gc.inspection.scoop.base.BasePresenter;
import ca.gc.inspection.scoop.base.BaseView;

public interface NotificationsTodayContract {

    /**
     * interface to be implemented by the NotificationsFragment class
     */
    interface View extends BaseView<Presenter> {
        void onLoadedDataFromDatabase();

        void showNoNotifications();
        void hideNoNotifications();

        interface ViewHolder {
            NotificationsTodayContract.View.ViewHolder setActionType(String actionType);
            NotificationsTodayContract.View.ViewHolder setActivityType(String activityType);
            NotificationsTodayContract.View.ViewHolder setTime(String time);
            NotificationsTodayContract.View.ViewHolder hideTime() ;
            NotificationsTodayContract.View.ViewHolder setFullName(String fullName);
            NotificationsTodayContract.View.ViewHolder setImage(Bitmap bitmap);
            NotificationsTodayContract.View.ViewHolder hideImage();
            NotificationsTodayContract.View.ViewHolder setUserImageFromString(String image);
        }

        interface Adapter {
            void refreshAdapter();
        }
    }
    /**
     * interface to be implemented by the NotificationsTodayPresenter class
     */
    interface Presenter extends BasePresenter {
        void loadDataFromDatabase();

        interface AdapterAPI {
            void setAdapter(NotificationsTodayContract.View.Adapter adapter);
            void onBindViewHolderAtPosition(
                    NotificationsTodayContract.View.ViewHolder notificationsViewHolder, int i);
            int getItemCount();
        }

        interface ViewHolderAPI {
        }

    }
}


