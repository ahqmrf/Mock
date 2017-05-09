package apps.ahqmrf.mock.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import apps.ahqmrf.mock.Message;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lenovo on 5/4/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context           mContext;
    private ArrayList<Object> mItems;
    private String            username;
    private String            imageUrl;
    private int prev = -1;

    private final int SENDER_TEXT    = 1;
    private final int SENDER_PHOTO   = 2;
    private final int RECEIVER_TEXT  = 3;
    private final int RECEIVER_PHOTO = 4;
    private final int TYPING         = 5;

    private int sent = -1;

    public ChatListAdapter(Context mContext, ArrayList<Object> mItems, String imageUrl) {
        this.mContext = mContext;
        this.mItems = mItems;
        this.username = Utility.getString(mContext, Const.Keys.USERNAME);
        this.imageUrl = imageUrl;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.typing, parent, false);
            return new TypingViewHolder(view);
        }
        if (viewType == SENDER_TEXT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_chat_sender, parent, false);
            return new MessageViewHolder(view);
        }
        if (RECEIVER_TEXT == viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_chat_receive, parent, false);
            return new MessageViewHolder(view);
        }
        if (viewType == SENDER_PHOTO) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_photo_send, parent, false);
            return new PhotoViewHolder(view);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_photo_receive, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPING) {
            Boolean isTyping = (Boolean) mItems.get(position);
            onBindTypingViewHolder((TypingViewHolder) viewHolder, isTyping);
            return;
        }

        if (viewType == RECEIVER_TEXT) {
            onBindReceiveTextViewHolder((MessageViewHolder) viewHolder, (Message) mItems.get(position));
            return;
        }

        if (viewType == SENDER_TEXT) {
            onBindSendTextViewHolder((MessageViewHolder) viewHolder, (Message) mItems.get(position));
            return;
        }

        if (viewType == SENDER_PHOTO) {
            onBindSendPhotoViewHolder((PhotoViewHolder) viewHolder, (Message) mItems.get(position));
            return;
        }

        onBindReceivePhotoViewHolder((PhotoViewHolder) viewHolder, (Message) mItems.get(position));
    }

    private void onBindTypingViewHolder(TypingViewHolder holder, boolean isTyping) {
        Utility.loadImage(imageUrl, holder.imageView);
        if (isTyping) holder.typing.setVisibility(View.VISIBLE);
        else holder.typing.setVisibility(View.GONE);
    }

    private void onBindSendTextViewHolder(MessageViewHolder holder, Message message) {
        holder.msg.setText(message.getText());
        holder.time.setText(message.getTime());
        if (message.isLast() && message.getId() >= sent) {
            holder.imageView.setVisibility(View.VISIBLE);
            sent = message.getId();
        } else holder.imageView.setVisibility(View.INVISIBLE);

        if (message.isSeen()) {
            Utility.loadImage(imageUrl, holder.imageView);
            holder.seenTime.setText("Seen at " + message.getSeenTime());
        } else {
            holder.imageView.setImageResource(R.drawable.ic_check_black_24dp);
            holder.seenTime.setText("Not seen yet");
            holder.imageView.setVisibility(View.VISIBLE);
        }

        if (message.isClicked()) {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.time.setVisibility(View.VISIBLE);
            holder.seenTime.setVisibility(View.VISIBLE);
        } else {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            holder.time.setVisibility(View.GONE);
            holder.seenTime.setVisibility(View.GONE);
        }
    }

    private void onBindSendPhotoViewHolder(PhotoViewHolder holder, Message message) {
        Utility.loadImage(message.getImageUrl(), holder.photo, holder.progress);
        holder.time.setText(message.getTime());

        if (message.isLast() && message.getId() >= sent) {
            holder.imageView.setVisibility(View.VISIBLE);
            sent = message.getId();
        } else holder.imageView.setVisibility(View.INVISIBLE);


        if (message.isSeen()) {
            Utility.loadImage(imageUrl, holder.imageView);
            holder.seenTime.setText("Seen at " + message.getSeenTime());
        } else {
            holder.imageView.setImageResource(R.drawable.ic_check_black_24dp);
            holder.seenTime.setText("Not seen yet");
            holder.imageView.setVisibility(View.VISIBLE);
        }


        if (message.isClicked()) {
            holder.time.setVisibility(View.VISIBLE);
            holder.seenTime.setVisibility(View.VISIBLE);
        } else {
            holder.time.setVisibility(View.GONE);
            holder.seenTime.setVisibility(View.GONE);
        }
    }

    private void onBindReceiveTextViewHolder(MessageViewHolder holder, Message message) {
        holder.msg.setText(message.getText());
        holder.time.setText(message.getTime());
        holder.seenTime.setText("Seen");
        Utility.loadImage(imageUrl, holder.imageView);

        if (message.isLast()) {
            holder.imageView.setVisibility(View.VISIBLE);
        } else holder.imageView.setVisibility(View.INVISIBLE);

        if (message.isClicked()) {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
            holder.time.setVisibility(View.VISIBLE);
            holder.seenTime.setVisibility(View.VISIBLE);
        } else {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.whitey_grey));
            holder.time.setVisibility(View.GONE);
            holder.seenTime.setVisibility(View.GONE);
        }
    }

    private void onBindReceivePhotoViewHolder(PhotoViewHolder holder, Message message) {
        Utility.loadImage(message.getImageUrl(), holder.photo, holder.progress);
        holder.time.setText(message.getTime());
        holder.seenTime.setText("Seen");
        Utility.loadImage(imageUrl, holder.imageView);

        if (message.isLast()) {
            holder.imageView.setVisibility(View.VISIBLE);
        } else holder.imageView.setVisibility(View.INVISIBLE);


        if (message.isClicked()) {
            holder.time.setVisibility(View.VISIBLE);
            holder.seenTime.setVisibility(View.VISIBLE);
        } else {
            holder.time.setVisibility(View.GONE);
            holder.seenTime.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = mItems.get(position);
        if (object instanceof Boolean) return TYPING;
        Message message = (Message) object;
        if (message.getSender().equals(username)) {
            if (message.getType().equals(Const.Keys.PHOTO)) return SENDER_PHOTO;
            return SENDER_TEXT;
        }

        if (message.getType().equals(Const.Keys.PHOTO)) return RECEIVER_PHOTO;
        else return RECEIVER_TEXT;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_text)
        TextView  msg;
        @BindView(R.id.text_time)
        TextView  time;
        @BindView(R.id.text_time_seen)
        TextView  seenTime;
        @BindView(R.id.image_main)
        ImageView imageView;
        @BindView(R.id.card_text)
        CardView  card;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.card_text)
        void onCardClick() {
            int pos = getAdapterPosition();
            Message msg = (Message) mItems.get(pos);
            msg.setClicked(!msg.isClicked());
            notifyItemChanged(pos);
            if (prev != -1 && prev != pos) {
                msg = (Message) mItems.get(prev);
                msg.setClicked(false);
                notifyItemChanged(prev);
            }
            prev = pos;
        }
    }

    public class TypingViewHolder extends RecyclerView.ViewHolder {

        View      typing;
        ImageView imageView;

        public TypingViewHolder(View view) {
            super(view);
            typing = view.findViewById(R.id.typing);
            imageView = (ImageView) view.findViewById(R.id.image_main);
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_photo)
        ImageView photo;
        @BindView(R.id.text_time)
        TextView  time;
        @BindView(R.id.text_time_seen)
        TextView  seenTime;
        @BindView(R.id.image_main)
        ImageView imageView;
        @BindView(R.id.layout_progress)
        View      progress;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.image_photo)
        void onPhotoClick() {
            int pos = getAdapterPosition();
            Message msg = (Message) mItems.get(pos);
            msg.setClicked(!msg.isClicked());
            notifyItemChanged(pos);
            if (prev != -1 && prev != pos) {
                msg = (Message) mItems.get(prev);
                msg.setClicked(false);
                notifyItemChanged(prev);
            }
            prev = pos;
        }
    }
}