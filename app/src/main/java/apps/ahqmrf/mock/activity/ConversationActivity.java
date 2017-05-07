package apps.ahqmrf.mock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import apps.ahqmrf.mock.LastMessage;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.adapter.ConversationListAdapter;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationActivity extends BaseActivity implements ConversationListAdapter.Callback {

    @BindView(R.id.recycler_conversation)
    RecyclerView recyclerConversation;

    @BindView(R.id.text_no_conversation)
    TextView textNoConversation;

    private DatabaseReference  ref;
    private ValueEventListener eventListener;
    private ArrayList<LastMessage> items;
    private ConversationListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);
        mImageChat.setColorFilter(ContextCompat.getColor(this, R.color.black));
        setToolbarWithBackArrow();
    }

    @Override
    protected void setToolbarTitle() {
        setTitle("Conversations");
    }

    @Override
    protected void onViewCreated() {
        String username = Utility.getString(this, Const.Keys.USERNAME);
        ref = FirebaseDatabase.getInstance().getReference(Const.Route.LAST_MESSAGE).child(username);
        items = new ArrayList<>();
        adapter = new ConversationListAdapter(this, this, items);
        recyclerConversation.setAdapter(adapter);
        recyclerConversation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getConversationList((Map<String, Object>) dataSnapshot.getValue());
                progressList.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressList.setVisibility(View.GONE);
            }
        };

    }

    private void getConversationList(Map<String, Object> value) {

        if(value == null) return;
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            //Get user map
            Map object = (Map) entry.getValue();
            String email = (String) object.get(Const.Keys.EMAIL);
            String username = (String) object.get(Const.Keys.USERNAME);
            String fullName = (String) object.get(Const.Keys.NAME);
            String imageUrl = (String) object.get(Const.Keys.PROFILE_PIC);
            String day = (String) object.get(Const.Keys.DATE);
            String time = (String) object.get(Const.Keys.TIME);
            String sender = (String) object.get(Const.Keys.SENDER);
            String receiver = (String) object.get(Const.Keys.RECEIVER);
            String text = (String) object.get(Const.Keys.TEXT);
            items.add(
                    new LastMessage(
                            email, username, fullName, imageUrl, sender, receiver, day, time, text
                    )
            );
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressList.setVisibility(View.VISIBLE);
        ref.addValueEventListener(eventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        items.clear();
        adapter.notifyDataSetChanged();
        ref.removeEventListener(eventListener);
    }

    @Override
    public void onConversationClick(LastMessage message) {
        User user = new User(message.getEmail(), message.getUsername(), message.getFullName(), message.getImageUrl());
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Const.Keys.USER, user);
        startActivity(intent);
    }
}
