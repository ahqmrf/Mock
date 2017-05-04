package apps.ahqmrf.mock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
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
import apps.ahqmrf.mock.adapter.NotificationListAdapter;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends BaseActivity implements NotificationListAdapter.Callback {

    @BindView(R.id.recycler_requests) RecyclerView recyclerView;
    @BindView(R.id.text_no_notification)
    TextView noNotification;

    private NotificationListAdapter mAdapter;
    private LinearLayoutManager     mLayoutManager;
    private ArrayList<Object>       mItems;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private User self;
    private int size = 0;
    private DatabaseReference  ref;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        mImageNotification.setColorFilter(ContextCompat.getColor(this, R.color.black));
        self = new User(
                Utility.getString(this, Const.Keys.EMAIL),
                Utility.getString(this, Const.Keys.USERNAME),
                Utility.getString(this, Const.Keys.NAME),
                Utility.getString(this, Const.Keys.PROFILE_PIC)
        );
        noNotification.setVisibility(View.VISIBLE);
        setToolbarWithBackArrow();
        String username = Utility.getString(this, Const.Keys.USERNAME);
        ref = FirebaseDatabase.getInstance().getReference(Const.Route.REQUEST).child(username);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressList.setVisibility(View.VISIBLE);
                prepareRequestList((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                progressList.setVisibility(View.GONE);

            }
        };
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        mItems = new ArrayList<>();
        mAdapter = new NotificationListAdapter(this, this, mItems);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setToolbarTitle() {

    }

    @Override
    protected void onViewCreated() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(eventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ref.addValueEventListener(eventListener);
    }

    private void prepareRequestList(Map<String, Object> value) {
        if(value == null) {
            noNotification.setVisibility(View.VISIBLE);
            progressList.setVisibility(View.GONE);
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
            mItems.add(new User(email, username, fullName, imageUrl));
        }

        mAdapter.setFilter(mItems);
        progressList.setVisibility(View.GONE);
        size = mItems.size();
        if(size > 0) noNotification.setVisibility(View.GONE);
    }

    @Override
    public void onFullNameClick(User user) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(Const.Keys.USER, user);
        intent.putExtra(Const.Keys.USER_TYPE, Const.Keys.WANNABE);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onConfirmClick(User user) {
        setValues(database.getReference(Const.Route.FRIEND).child(self.getUsername()).child(user.getUsername()), user, Const.Keys.FRIEND);
        setValues(database.getReference(Const.Route.FRIEND).child(user.getUsername()).child(self.getUsername()), self, Const.Keys.FRIEND);
        database.getReference(Const.Route.REQUEST).child(self.getUsername()).child(user.getUsername()).removeValue();
        size--;
        if(size == 0) noNotification.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeclineClick(User user) {
        database.getReference(Const.Route.FRIEND).child(self.getUsername()).child(user.getUsername()).removeValue();
        database.getReference(Const.Route.FRIEND).child(user.getUsername()).child(self.getUsername()).removeValue();
        database.getReference(Const.Route.REQUEST).child(self.getUsername()).child(user.getUsername()).removeValue();
        size--;
        if(size == 0) noNotification.setVisibility(View.VISIBLE);
    }

    private void setValues(DatabaseReference reference, User user, String status) {
        reference.child(Const.Keys.USERNAME).setValue(user.getUsername());
        reference.child(Const.Keys.STATUS).setValue(status);
        reference.child(Const.Keys.EMAIL).setValue(user.getEmail());
        reference.child(Const.Keys.NAME).setValue(user.getFullName());
        reference.child(Const.Keys.PROFILE_PIC).setValue(user.getImageUrl());
    }
}
