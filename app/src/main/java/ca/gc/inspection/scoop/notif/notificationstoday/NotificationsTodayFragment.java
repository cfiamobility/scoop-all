package ca.gc.inspection.scoop.notif.notificationstoday;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import ca.gc.inspection.scoop.R;
import ca.gc.inspection.scoop.util.NetworkUtils;

import static ca.gc.inspection.scoop.Config.SWIPE_REFRESH_COLOUR_1;
import static ca.gc.inspection.scoop.Config.SWIPE_REFRESH_COLOUR_2;
import static ca.gc.inspection.scoop.Config.SWIPE_REFRESH_COLOUR_3;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class NotificationsTodayFragment extends Fragment implements
        NotificationsTodayContract.View,
        SwipeRefreshLayout.OnRefreshListener{

    //recycler view widgets
    private RecyclerView mRecyclerView;
    private NotificationsTodayAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //private NotificationsTodayPresenter notificationsScreenController;
    private NotificationsTodayContract.Presenter mPresenter;
    private View view;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView noNotificationsTitle, noNotificationsText;
    private ImageView noNotificationsImage;

    @Override
    public void setPresenter(NotificationsTodayContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications_today, container, false);
        setPresenter(new NotificationsTodayPresenter(this, NetworkUtils.getInstance(getContext())));
        setSwipeRefreshLayout(view);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setRecyclerView();

        noNotificationsTitle = view.findViewById(R.id.fragment_notifications_no_new_title);
        noNotificationsText = view.findViewById(R.id.fragment_notifications_no_new_text);
        noNotificationsImage = view.findViewById(R.id.fragment_notifications_no_new_image);
    }

    public void setRecyclerView() {
        // Initializing the recycler view
        mRecyclerView = view.findViewById(R.id.fragment_notifications_today_rv); //instantiating the recyclerview
        mRecyclerView.setHasFixedSize(true); //

        // Setting the layout manager for the recycler view
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false); //instantiates how the layout should look like for recyclerview
        mRecyclerView.setLayoutManager(mLayoutManager); //sets the layout manager to one chosen

        // Setting the custom adapter for the recycler view
        mAdapter = new NotificationsTodayAdapter(this,
                (NotificationsTodayContract.Presenter.AdapterAPI) mPresenter); //instantiates the adapter
        mRecyclerView.setAdapter(mAdapter); //sets the adapter
        //notificationsScreenController.listenTodayRecyclerView(mRecyclerView, notificationResponse);
//        mPresenter.listenTodayRecyclerView(mRecyclerView, notificationResponse);
    }

    protected void setSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.fragment_notifications_today_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                SWIPE_REFRESH_COLOUR_1,
                SWIPE_REFRESH_COLOUR_2,
                SWIPE_REFRESH_COLOUR_3);
    }

    /**
     * To implement SwipeRefreshLayout.OnRefreshListener
     */
    @Override
    public void onRefresh() {
        loadDataFromDatabase();
    }

    @Override
    public void onLoadedDataFromDatabase() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadDataFromDatabase() {
        mSwipeRefreshLayout.setRefreshing(true);
        mPresenter.loadDataFromDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mSwipeRefreshLayout.isRefreshing())
            loadDataFromDatabase();
    }

    /**
     * Description: shows no new notification text and icon
     */
    public void showNoNotifications(){
        noNotificationsTitle.setVisibility(View.VISIBLE);
        noNotificationsText.setVisibility(View.VISIBLE);
        noNotificationsImage.setVisibility(View.VISIBLE);
    }

    public void hideNoNotifications(){
        noNotificationsTitle.setVisibility(View.GONE);
        noNotificationsText.setVisibility(View.GONE);
        noNotificationsImage.setVisibility(View.GONE);
    }

}