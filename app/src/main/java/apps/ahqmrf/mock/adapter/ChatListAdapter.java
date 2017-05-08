package apps.ahqmrf.mock.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
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

    private static final int TYPING = 3;
    private Context mContext;
    private ArrayList<Object> mItems;
    private String username;
    private String imageUrl;
    private int prev = -1;

    private int SENDER = 0;

    public ChatListAdapter(Context mContext, ArrayList<Object> mItems, String imageUrl) {
        this.mContext = mContext;
        this.mItems = mItems;
        this.username = Utility.getString(mContext, Const.Keys.USERNAME);
        this.imageUrl = imageUrl;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.typing, parent, false);
            return new TypingView(view);
        }
        if(viewType == SENDER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_chat_sender, parent, false);
            return new ViewHolder(view);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_chat_receive, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == TYPING) {
            TypingView holder = (TypingView) viewHolder;
            Utility.loadImage(imageUrl, holder.imageView);
            Boolean val = (Boolean) mItems.get(position);
            if(val) holder.typing.setVisibility(View.VISIBLE);
            else holder.typing.setVisibility(View.GONE);
            return;
        }
        ViewHolder holder = (ViewHolder) viewHolder;
        Message msg = (Message) mItems.get(position);
        holder.msg.setText(msg.getText());
        holder.time.setText(msg.getTime());
        if(getItemViewType(position) == SENDER) {
            if(msg.isSeen()) Utility.loadImage(imageUrl, holder.imageView);
            else holder.imageView.setImageResource(R.drawable.ic_check_black_24dp);
        } else Utility.loadImage(imageUrl, holder.imageView);

        if(msg.isLast()) {
            holder.imageView.setVisibility(View.VISIBLE);
        } else holder.imageView.setVisibility(View.INVISIBLE);
        if(msg.isClicked()) {
            if(getItemViewType(position) == SENDER) holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            else holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
            holder.time.setVisibility(View.VISIBLE);
        } else {
            if(getItemViewType(position) == SENDER) holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            else holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.whitey_grey));
            holder.time.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mItems.get(position) instanceof Boolean) return TYPING;
        Message msg = (Message)mItems.get(position);
        if(username.equals(msg.getSender())) return SENDER;
        else return 1;
    }

    public class  ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_text)
        TextView msg;
        @BindView(R.id.text_time)
        TextView time;
        @BindView(R.id.image_main)
        ImageView imageView;
        @BindView(R.id.card_text)
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.card_text)
        void onCardClick() {
            int pos = getAdapterPosition();
            Message msg = (Message) mItems.get(pos);
            msg.setClicked(!msg.isClicked());
            notifyItemChanged(pos);
            if(prev != -1 && prev != pos) {
                msg = (Message) mItems.get(prev);
                msg.setClicked(false);
                notifyItemChanged(prev);
            }
            prev = pos;
        }
    }

    public class TypingView extends RecyclerView.ViewHolder {

        View typing;
        ImageView imageView;

        public TypingView(View view) {
            super(view);
            typing = view.findViewById(R.id.typing);
            imageView = (ImageView) view.findViewById(R.id.image_main);
        }
    }
}
