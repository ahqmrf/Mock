package apps.ahqmrf.mock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import apps.ahqmrf.mock.activity.FriendsListActivity;
import apps.ahqmrf.mock.activity.MyLocationActivity;
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
    }

    protected ArrayList<User> userList;
    protected ArrayList<User> searchResultList;
    protected UserListAdapter mAdapter;
    protected MenuItem        item;
    protected boolean         wasSearchClicked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    protected void getAllUsers(final String searchKey) {
        FirebaseDatabase.getInstance().getReference(Const.Route.USER_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prepareUserList((Map<String, Object>) dataSnapshot.getValue(), searchKey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                progressList.setVisibility(View.GONE);
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
    public void onUserClick(User user) {
        String username = Utility.getString(this, Const.Keys.USERNAME);
        if (!TextUtils.isEmpty(username) && username.toLowerCase().contains(user.getUsername())) {
            trigger(SettingsActivity.class);
            item.collapseActionView();
            return;
        }
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(Const.Keys.USER, user);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        item.collapseActionView();
    }
}
