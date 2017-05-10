package apps.ahqmrf.mock.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.binjar.prefsdroid.Preference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import apps.ahqmrf.mock.Message;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.Time;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.adapter.ChatListAdapter;
import apps.ahqmrf.mock.model.MessageBuilder;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity implements ChatListAdapter.Callback{

    @Nullable
    @BindView(R.id.app_toolbar)
    Toolbar toolbar;

    @BindView(R.id.input_text)
    EditText msgInput;

    @BindView(R.id.image_online_status)
    ImageView onlineStatus;

    @BindView(R.id.recycler_chats)
    RecyclerView chatView;

   /* @BindView(R.id.layout_progress)
    View progressLayout;*/


    private User self, user;
    private DatabaseReference  refOnlineStatus;
    private DatabaseReference  userOnlineStatus;
    private DatabaseReference  refType;
    private DatabaseReference  refTypeUser;
    private DatabaseReference  refMsg;
    private DatabaseReference  lastMsg;
    private ValueEventListener eventListener, typeListener;
    private ChildEventListener msgListener;
    private ChatListAdapter    adapter;
    private ArrayList<Object> msgs     = new ArrayList<>();
    private int               last     = -1;
    private int               sentLast = -1;
    private int               count    = -1;
    private Query query;
    private LinearLayoutManager manager;
    private Parcelable chatState;

    long    delay          = 1000; // 1 seconds after user stops typing
    long    last_text_edit = 0;
    Handler handler        = new Handler();

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                refType.setValue(true);
            }
        }
    };

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

        String path = Utility.getChatNode(user, self);

        lastMsg = FirebaseDatabase.getInstance().getReference(Const.Route.LAST_MESSAGE).child(user.getUsername()).child(self.getUsername());

        refOnlineStatus = FirebaseDatabase.getInstance().getReference(Const.Route.ONLINE_STATUS).child(self.getUsername()).child(user.getUsername());
        refOnlineStatus.setValue(Const.Keys.ONLINE);
        DatabaseReference refChat = FirebaseDatabase.getInstance().getReference(Const.Route.CHAT).child(path);
        refMsg = refChat.child(Const.Keys.MESSAGES);
        query = refMsg.limitToLast(100);
        refType = refChat.child(self.getUsername()).child(Const.Keys.TYPING_STATUS);
        refTypeUser = refChat.child(user.getUsername()).child(Const.Keys.TYPING_STATUS);
        userOnlineStatus = FirebaseDatabase.getInstance().getReference(Const.Route.ONLINE_STATUS).child(user.getUsername()).child(self.getUsername());
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        msgs.add(false);

        msgListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Message model = dataSnapshot.getValue(Message.class);
                    if (model.getId() == -1) {
                        model.setId(msgs.size() - 1);
                        count = model.getId();
                        dataSnapshot.getRef().setValue(model);
                    } else {
                        if(model.getId() <= count) return;
                        count = model.getId();
                    }
                    if (model.getSender().equals(user.getUsername())) {
                        model.setSeen(true);
                        Time time = Utility.getCurrentTime();
                        String day = time.getDate();
                        String stamp = Utility.get12HourTimeStamp(time);
                        model.setSeenDay(day);
                        model.setSeenTime(stamp);
                        dataSnapshot.getRef().setValue(model);
                        model.setLast(true);
                        int pos = msgs.size() - 1;
                        if (last != -1 && pos - last == 1) {
                            Message msg = (Message) msgs.get(last);
                            msg.setLast(false);
                            msgs.set(last, msg);
                            adapter.notifyItemChanged(last);
                        }
                        last = pos;
                    } else {
                        model.setLast(true);
                        int pos = msgs.size() - 1;
                        if (sentLast != -1) {
                            Message msg = (Message) msgs.get(sentLast);
                            msg.setLast(false);
                            msgs.set(sentLast, msg);
                            adapter.notifyItemChanged(sentLast);
                        }
                        sentLast = pos;
                    }
                    msgs.add(msgs.size() - 1, model);
                    chatView.scrollToPosition(msgs.size() - 2);
                    adapter.notifyItemInserted(msgs.size() - 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Message model = dataSnapshot.getValue(Message.class);
                    if (model.getSender().equals(self.getUsername())) {
                        msgs.set(model.getId(), model);
                        adapter.notifyItemChanged(model.getId());
                    }
                }
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
                        if (isTyping) {
                            msgs.set(msgs.size() - 1, true);
                            adapter.notifyItemChanged(msgs.size() - 1);
                            chatView.scrollToPosition(msgs.size() - 1);
                        } else {
                            msgs.set(msgs.size() - 1, false);
                            adapter.notifyItemChanged(msgs.size() - 1);
                        }
                    } else {
                        msgs.set(msgs.size() - 1, false);
                        adapter.notifyItemChanged(msgs.size() - 1);
                    }
                } else {
                    msgs.set(msgs.size() - 1, false);
                    adapter.notifyItemChanged(msgs.size() - 1);
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

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                } else refType.setValue(false);
            }
        });

        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        manager.setStackFromEnd(true);
        chatView.setLayoutManager(manager);
        adapter = new ChatListAdapter(this, msgs, user.getImageUrl());
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
        query.addChildEventListener(msgListener);
        refType.setValue(false);
        if(chatState != null) {
            manager.onRestoreInstanceState(chatState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        refOnlineStatus.setValue(Const.Keys.OFFLINE);
        userOnlineStatus.removeEventListener(eventListener);
        refTypeUser.removeEventListener(typeListener);
        query.removeEventListener(msgListener);
        refType.setValue(false);
    }

    @OnClick(R.id.image_send)
    void sendMessage() {
        refType.setValue(false);
        String text = msgInput.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            Time time = Utility.getCurrentTime();
            String day = time.getDate();
            String stamp = Utility.get12HourTimeStamp(time);
            Message msg =
                    new MessageBuilder()
                            .setText(text)
                            .setDay(day)
                            .setTime(stamp)
                            .setSender(self.getUsername())
                            .setReceiver(user.getUsername())
                            .setSeen(false)
                            .setType(Const.Keys.TEXT)
                            .setId(-1)
                            .setLast(true)
                            .setClicked(false)
                            .setImageUrl("")
                            .setSeenDay("")
                            .setSeenTime("")
                            .build();

            refMsg.push().setValue(msg);
            msgInput.setText("");
            lastMsg.setValue(msg);
        }
    }

    @OnClick(R.id.image_insert)
    public void checkReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Const.RequestCodes.READ_EXTERNAL_STORAGE);
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Const.RequestCodes.READ_EXTERNAL_STORAGE);
            }
        } else {

            openImageGallery();
        }
    }

    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        if (getPackageManager().resolveActivity(intent, 0) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Const.RequestCodes.REQUEST_BROWSE_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Const.RequestCodes.READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageGallery();

                } else {
                    Utility.showToast(this, "Permission required to select media");
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.RequestCodes.REQUEST_BROWSE_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (null != uri) {
                uploadImage(uri);
            }
        }
    }

    private void uploadImage(Uri uri) {
        String path = Utility.getChatNode(user, self);
        final StorageReference photoStorage = FirebaseStorage.getInstance().getReference().child(path).child(uri.getLastPathSegment());
        photoStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photoStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Utility.showToast(getApplicationContext(), "Uploaded successfully");
                        sendImage(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showToast(getApplicationContext(), "Failed to upload photo");
            }
        });
    }

    private void sendImage(String imageUrl) {
        Time time = Utility.getCurrentTime();
        String day = time.getDate();
        String stamp = Utility.get12HourTimeStamp(time);
        Message msg =
                new MessageBuilder()
                        .setText("")
                        .setDay(day)
                        .setTime(stamp)
                        .setSender(self.getUsername())
                        .setReceiver(user.getUsername())
                        .setSeen(false)
                        .setType(Const.Keys.PHOTO)
                        .setId(-1)
                        .setLast(true)
                        .setClicked(false)
                        .setImageUrl(imageUrl)
                        .setSeenDay("")
                        .setSeenTime("")
                        .build();

        refMsg.push().setValue(msg);
        lastMsg.setValue(msg);
    }

    @Override
    public void onImageClick(String imageUrl) {
        Intent intent = new Intent(this, ImageFullScreenActivity.class);
        intent.putExtra(Const.Keys.IMAGE_URL, imageUrl);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        chatState = manager.onSaveInstanceState();
        outState.putParcelable(Const.Keys.CHAT_LIST, chatState);
        outState.putInt(Const.Keys.LAST_COUNT, count);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            chatState = savedInstanceState.getParcelable(Const.Keys.CHAT_LIST);
            count = savedInstanceState.getInt(Const.Keys.LAST_COUNT);
        }
    }
}
