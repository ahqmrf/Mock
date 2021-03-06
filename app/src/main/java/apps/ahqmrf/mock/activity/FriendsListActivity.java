package apps.ahqmrf.mock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import apps.ahqmrf.mock.BaseActivity;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.adapter.FriendListAdapter;
import apps.ahqmrf.mock.adapter.NotificationListAdapter;
import apps.ahqmrf.mock.adapter.UserListAdapter;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsListActivity extends BaseActivity implements FriendListAdapter.FriendClickCallback {
    @BindString(R.string.title_my_friends)
    String title;
    private ArrayList<User> mItems;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference ref;

    @BindView(R.id.recycler_friends) RecyclerView recyclerView;
    @BindView(R.id.text_no_friends)
    TextView noFriends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);
        mImageFriends.setColorFilter(ContextCompat.getColor(this, R.color.black));
        setToolbarWithBackArrow();
    }

    @Override
    protected void setToolbarTitle() {
        setTitle(title);
    }

    @Override
    protected void onViewCreated() {
        String username = Utility.getString(this, Const.Keys.USERNAME);
        progressList.setVisibility(View.VISIBLE);
        ref = FirebaseDatabase.getInstance().getReference(Const.Route.FRIEND).child(username);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressList.setVisibility(View.GONE);
                prepareFriendList((Map<String, Object>) dataSnapshot.getValue());
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                progressList.setVisibility(View.GONE);
                ref.removeEventListener(this);
            }
        });
    }

    private void prepareFriendList(Map<String, Object> value) {
        if(value == null) {
            noFriends.setVisibility(View.VISIBLE);
            return;
        }
        mItems = new ArrayList<>();
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            //Get user map
            Map singleUser = (Map) entry.getValue();
            String email = (String) singleUser.get(Const.Keys.EMAIL);
            String username = (String) singleUser.get(Const.Keys.USERNAME);
            String fullName = (String) singleUser.get(Const.Keys.NAME);
            String imageUrl = (String) singleUser.get(Const.Keys.PROFILE_PIC);
            String status = (String) singleUser.get(Const.Keys.STATUS);
            if(status.equals(Const.Keys.FRIEND)) mItems.add(new User(email, username, fullName, imageUrl));
        }

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        FriendListAdapter adapter = new FriendListAdapter(this, this, mItems);
        recyclerView.setAdapter(adapter);
        if(mItems.isEmpty()) noFriends.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFriendClick(User user) {
        onUserClick(user);
    }

    @Override
    public void onChatClick(User user) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Const.Keys.USER, user);
        startActivity(intent);
    }

    @Override
    public void onMapClick(User user) {
        Intent intent = new Intent(this, TrackerActivity.class);
        intent.putExtra(Const.Keys.USER, user);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
