package apps.ahqmrf.mock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import apps.ahqmrf.mock.BaseActivity;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserActivity extends BaseActivity {

    @BindView(R.id.text_full_name) TextView  fullName;
    @BindView(R.id.text_username)  TextView  username;
    @BindView(R.id.image_main)     ImageView imageView;
    @BindView(R.id.container)      View      rootLayout;
    @BindView(R.id.image_more)     ImageView more;
    @BindView(R.id.layout_progress) View progressLayout;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String    thisUserName;
    private String    thisUserFullName;
    private User      user, self;
    private String    userType;
    private PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        setToolbarWithBackArrow();
        user = getIntent().getParcelableExtra(Const.Keys.USER);
        self = new User(
                Utility.getString(this, Const.Keys.EMAIL),
                Utility.getString(this, Const.Keys.USERNAME),
                Utility.getString(this, Const.Keys.NAME),
                Utility.getString(this, Const.Keys.PROFILE_PIC)
        );
        thisUserName = user.getUsername();
        thisUserFullName = user.getFullName();
        userType = getIntent().getStringExtra(Const.Keys.USER_TYPE);
        popupMenu = new PopupMenu(this, more);
        if (userType.equals(Const.Keys.FRIEND)) {
            popupMenu.inflate(R.menu.friend_menu);

        } else if (userType.equals(Const.Keys.STRANGER)) {
            popupMenu.inflate(R.menu.stranger_menu);
        } else if (userType.equals(Const.Keys.REQUESTED)) {
            popupMenu.inflate(R.menu.requested_menu);
        } else popupMenu.inflate(R.menu.wanna_be_friend_menu);
    }

    @Override
    protected void setToolbarTitle() {
        setTitle(thisUserFullName);
    }

    @Override
    protected void onViewCreated() {
        username.setText(thisUserName);
        fullName.setText(thisUserFullName);
        if (!TextUtils.isEmpty(user.getImageUrl()))
            Utility.loadImage(user.getImageUrl(), imageView, progressLayout);
        else  progressLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.image_more)
    public void openOptionWindow() {
        popupMenu.show();
    }

    public void openMap(MenuItem item) {
        Intent intent = new Intent(this, TrackerActivity.class);
        intent.putExtra(Const.Keys.USER, user);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        item.collapseActionView();
    }

    public void unfriend(MenuItem item) {
        database.getReference(Const.Route.FRIEND).child(self.getUsername()).child(thisUserName).removeValue();
        database.getReference(Const.Route.FRIEND).child(thisUserName).child(self.getUsername()).removeValue();
        popupMenu = new PopupMenu(this, more);
        popupMenu.inflate(R.menu.stranger_menu);
    }

    public void cancelRequest(MenuItem item) {
        database.getReference(Const.Route.FRIEND).child(self.getUsername()).child(thisUserName).removeValue();
        database.getReference(Const.Route.FRIEND).child(thisUserName).child(self.getUsername()).removeValue();
        database.getReference(Const.Route.NOTIFICATION).child(thisUserName).child(self.getUsername()).removeValue();
        popupMenu = new PopupMenu(this, more);
        popupMenu.inflate(R.menu.stranger_menu);
    }

    public void sendFriendRequest(MenuItem item) {
        setValues(database.getReference(Const.Route.NOTIFICATION).child(thisUserName).child(self.getUsername()), self, Const.Keys.REQUESTED);
        setValues(database.getReference(Const.Route.FRIEND).child(thisUserName).child(self.getUsername()), self, Const.Keys.WANNABE);
        setValues(database.getReference(Const.Route.FRIEND).child(self.getUsername()).child(thisUserName), user, Const.Keys.REQUESTED);
        popupMenu = new PopupMenu(this, more);
        popupMenu.inflate(R.menu.requested_menu);
    }

    public void acceptRequest(MenuItem item) {
        setValues(database.getReference(Const.Route.FRIEND).child(self.getUsername()).child(thisUserName), user, Const.Keys.FRIEND);
        setValues(database.getReference(Const.Route.FRIEND).child(thisUserName).child(self.getUsername()), self, Const.Keys.FRIEND);
        database.getReference(Const.Route.NOTIFICATION).child(self.getUsername()).child(thisUserName).removeValue();
        setValues( database.getReference(Const.Route.NOTIFICATION).child(thisUserName).child(self.getUsername()), self, Const.Keys.ACCEPTED);
        popupMenu = new PopupMenu(this, more);
        popupMenu.inflate(R.menu.friend_menu);
    }

    private void setValues(DatabaseReference reference, User user, String status) {
        reference.child(Const.Keys.USERNAME).setValue(user.getUsername());
        reference.child(Const.Keys.STATUS).setValue(status);
        reference.child(Const.Keys.EMAIL).setValue(user.getEmail());
        reference.child(Const.Keys.NAME).setValue(user.getFullName());
        reference.child(Const.Keys.PROFILE_PIC).setValue(user.getImageUrl());
    }

    public void startChatting(MenuItem item) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Const.Keys.USER, user);
        startActivity(intent);
    }
}
