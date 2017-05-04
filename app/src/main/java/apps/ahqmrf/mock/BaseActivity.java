package apps.ahqmrf.mock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import apps.ahqmrf.mock.activity.FriendsListActivity;
import apps.ahqmrf.mock.activity.MyLocationActivity;
import apps.ahqmrf.mock.activity.NotificationActivity;
import apps.ahqmrf.mock.activity.SettingsActivity;
import apps.ahqmrf.mock.activity.SignInActivity;
import apps.ahqmrf.mock.activity.UserActivity;
import apps.ahqmrf.mock.adapter.UserListAdapter;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by bsse0 on 4/22/2017.
 */

public abstract class BaseActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, UserListAdapter.UserClickCallback {

    @Nullable
    @BindView(R.id.app_toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.layout_progress_recycler)
    protected View progressList;

    @Nullable
    @BindView(R.id.fab_check_in)
    protected FloatingActionButton fab;

    @BindView(R.id.text_notification)
    protected TextView notificationIcon;

    @BindView(R.id.userList)
    protected RecyclerView recyclerView;

    @BindView(R.id.image_my_location)
    protected ImageView mImageLocation;

    @OnClick(R.id.image_my_location)
    public void showMyLocation() {
        trigger(MyLocationActivity.class);
    }

    @BindView(R.id.image_map)
    protected ImageView mImageMap;

    @OnClick(R.id.image_map)
    public void showMap() {
    }

    @BindView(R.id.image_friends)
    protected ImageView mImageFriends;

    @OnClick(R.id.image_friends)
    public void showFriends() {
        trigger(FriendsListActivity.class);
    }

    @BindView(R.id.image_notification)
    protected ImageView mImageNotification;

    @BindView(R.id.view_notification)
    protected View mViewNotification;

    @OnClick(R.id.view_notification)
    public void showNotification() {
        trigger(NotificationActivity.class);
    }

    protected ArrayList<User> userList;
    protected ArrayList<User> searchResultList;
    protected UserListAdapter mAdapter;
    protected MenuItem        item;
    protected boolean         wasSearchClicked;
    protected DatabaseReference tempRef;
    protected ValueEventListener notificationListener;
    protected DatabaseReference refNotification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        String username = Utility.getString(this, Const.Keys.USERNAME);
        refNotification = FirebaseDatabase.getInstance().getReference(Const.Route.REQUEST).child(username);
        notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if(dataSnapshot.getChildrenCount() > 0) {
                        notificationIcon.setText("" + dataSnapshot.getChildrenCount());
                        notificationIcon.setVisibility(View.VISIBLE);
                        createNotification();
                    } else {
                        notificationIcon.setVisibility(View.GONE);
                    }
                }
                else {
                    notificationIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                notificationIcon.setVisibility(View.GONE);
                Utility.showToast(getApplicationContext(), "Failed to retrieve notification!");
            }
        };
    }

    protected void createNotification() {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra(Const.Keys.NOTIFICATION_ID, 121);
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(this, requestID, intent, flags);
        Notification mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Mock")
                        .setContentText("You have new notifications")
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .build();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(121, mBuilder);
    }

    protected void getAllUsers(final String searchKey) {
        tempRef = FirebaseDatabase.getInstance().getReference(Const.Route.USER_REF);
        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prepareUserList((Map<String, Object>) dataSnapshot.getValue(), searchKey);
                tempRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                progressList.setVisibility(View.GONE);
                tempRef.removeEventListener(this);
            }
        });
    }

    protected void prepareUserList(Map<String, Object> value, String query) {
        userList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            //Get user map
            Map singleUser = (Map) entry.getValue();
            String email = (String) singleUser.get(Const.Keys.EMAIL);
            String username = (String) singleUser.get(Const.Keys.USERNAME);
            String fullName = (String) singleUser.get(Const.Keys.NAME);
            String imageUrl = (String) singleUser.get(Const.Keys.PROFILE_PIC);
            userList.add(new User(email, username, fullName, imageUrl));

        }
        findResult(query);
    }

    protected void setToolbarWithBackArrow() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        item = menu.findItem(R.id.menu_item_search);

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                recyclerView.setVisibility(View.VISIBLE);
                if (fab != null) fab.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                recyclerView.setVisibility(View.GONE);
                if (fab != null) fab.setVisibility(View.VISIBLE);
                return true;
            }
        });
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarTitle();
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        mAdapter = new UserListAdapter(this, this, userList);
        recyclerView.setAdapter(mAdapter);
        wasSearchClicked = false;
        userList = new ArrayList<>();
        onViewCreated();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        if (itemId == R.id.menu_item_logout) {
            Utility.put(this, Const.Keys.LOGGED_IN, false);
            finishAffinity();
            trigger(SignInActivity.class);
        }

        if (itemId == R.id.menu_item_settings) {
            trigger(SettingsActivity.class);
            item.collapseActionView();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void trigger(Class<?> activityClass) {
        if (!this.getClass().equals(activityClass)) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refNotification.addValueEventListener(notificationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        progressList.setVisibility(View.VISIBLE);
        query = query.toLowerCase();
        if (wasSearchClicked) findResult(query);
        else {
            wasSearchClicked = true;
            getAllUsers(query);
        }
        return true;
    }

    protected void findResult(String query) {
        searchResultList = new ArrayList<>();
        for (User user : userList) {
            if (user.getFullName().toLowerCase().contains(query) || user.getUsername().toLowerCase().contains(query)) {
                searchResultList.add(user);
            }
        }
        if (searchResultList.isEmpty())
            Utility.showToast(this, "No search result found for " + query);
        if (mAdapter != null) mAdapter.setFilter(searchResultList);
        progressList.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    protected abstract void setToolbarTitle();

    protected abstract void onViewCreated();

    @Override
    public void onUserClick(final User user) {
        String username = Utility.getString(this, Const.Keys.USERNAME);
        if (!TextUtils.isEmpty(username)) {
            if (username.toLowerCase().contains(user.getUsername())) {
                trigger(SettingsActivity.class);
                item.collapseActionView();
            }
            else {
                final Intent intent = new Intent(this, UserActivity.class);
                intent.putExtra(Const.Keys.USER, user);
                progressList.setVisibility(View.VISIBLE);
                tempRef = FirebaseDatabase.getInstance().getReference(Const.Route.FRIEND).child(username);
                tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressList.setVisibility(View.GONE);
                        if (dataSnapshot.hasChild(user.getUsername())) {
                            String val = dataSnapshot.child(user.getUsername()).child(Const.Keys.STATUS).getValue(String.class);
                            if(val.equals(Const.Keys.FRIEND))
                                intent.putExtra(Const.Keys.USER_TYPE, Const.Keys.FRIEND);
                            else if (val.equals(Const.Keys.REQUESTED)) intent.putExtra(Const.Keys.USER_TYPE, Const.Keys.REQUESTED);
                            else intent.putExtra(Const.Keys.USER_TYPE, Const.Keys.WANNABE);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            item.collapseActionView();
                        }
                        else {
                            intent.putExtra(Const.Keys.USER_TYPE, Const.Keys.STRANGER);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            item.collapseActionView();
                        }
                        tempRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressList.setVisibility(View.GONE);
                        intent.putExtra(Const.Keys.USER_TYPE, Const.Keys.STRANGER);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        item.collapseActionView();
                        tempRef.removeEventListener(this);
                    }
                });
            }
            return;
        }

        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(Const.Keys.USER, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        item.collapseActionView();
    }
}
