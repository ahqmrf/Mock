package apps.ahqmrf.mock.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import apps.ahqmrf.mock.Message;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.Time;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.adapter.ChatListAdapter;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.app_toolbar)
    Toolbar toolbar;

    @BindView(R.id.input_text)
    EditText msgInput;

    @BindView(R.id.image_online_status)
    ImageView onlineStatus;

    @BindView(R.id.card_typing)
    ImageView typing;

    @BindView(R.id.recycler_chats)
    RecyclerView chatView;

    private User self, user;
    private DatabaseReference refOnlineStatus, userOnlineStatus, refChat, refType, refTypeUser, refMsg;
    private ValueEventListener eventListener, typeListener;
    private ChildEventListener msgListener;
    private ChatListAdapter adapter;
    private ArrayList<Message> msgs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        user = getIntent().getParcelableExtra(Const.Keys.USER);
        self = new User(
                Utility.getString(this, Const.Keys.EMAIL),
                Utility.getString(this, Const.Keys.USERNAME),
                Utility.getString(this, Const.Keys.NAME),
                Utility.getString(this, Const.Keys.PROFILE_PIC)
        );

        String path = self.getUsername().compareTo(user.getUsername()) > 0 ? user.getUsername() + "_" + self.getUsername()
                : self.getUsername() + "_" + user.getUsername();

        refOnlineStatus = FirebaseDatabase.getInstance().getReference(Const.Route.ONLINE_STATUS).child(self.getUsername());
        refChat = FirebaseDatabase.getInstance().getReference(Const.Route.CHAT).child(path);
        refMsg = refChat.child(Const.Keys.MESSAGES);
        refType = refChat.child(self.getUsername()).child(Const.Keys.TYPING_STATUS);
        refTypeUser = refChat.child(user.getUsername()).child(Const.Keys.TYPING_STATUS);
        userOnlineStatus = FirebaseDatabase.getInstance().getReference(Const.Route.ONLINE_STATUS).child(user.getUsername());
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String status = (String) dataSnapshot.getValue();
                    if (status != null && status.equals(Const.Keys.ONLINE))
                        onlineStatus.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    else {
                        onlineStatus.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.whitey_grey));
                        userOnlineStatus.setValue(Const.Keys.OFFLINE);
                    }
                } else {
                    userOnlineStatus.setValue(Const.Keys.OFFLINE);
                    onlineStatus.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.whitey_grey));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        msgListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Message model = dataSnapshot.getValue(Message.class);
                    msgs.add(model);
                    chatView.scrollToPosition(msgs.size() - 1);
                    adapter.notifyItemInserted(msgs.size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        typeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.getValue() != null) {
                        boolean isTyping = (boolean) dataSnapshot.getValue();
                        if (isTyping) typing.setVisibility(View.VISIBLE);
                        else typing.setVisibility(View.INVISIBLE);
                    } else typing.setVisibility(View.INVISIBLE);
                } else {
                    typing.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle(user.getFullName());
        }

        msgInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                refType.setValue(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s.toString())) refType.setValue(false);
                else refType.setValue(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s.toString())) refType.setValue(false);
            }
        });

        chatView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ChatListAdapter(this, msgs);
        chatView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refOnlineStatus.setValue(Const.Keys.ONLINE);
        userOnlineStatus.addValueEventListener(eventListener);
        refTypeUser.addValueEventListener(typeListener);
        refMsg.addChildEventListener(msgListener);
        refType.setValue(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        userOnlineStatus.removeEventListener(eventListener);
        refTypeUser.removeEventListener(typeListener);
        refMsg.removeEventListener(msgListener);
        refType.setValue(false);
    }

    @OnClick(R.id.image_send)
    void sendMessage() {
        refType.setValue(false);
        String text = msgInput.getText().toString();
        if(!TextUtils.isEmpty(text)) {
            Time time = Utility.getCurrentTime();
            String day = time.getDate();
            String stamp = Utility.get12HourTimeStamp(time);
            Message message = new Message(self.getUsername(), user.getUsername(), day, stamp, text);
            refMsg.push().setValue(message);
            msgInput.setText("");
        }
    }
}
