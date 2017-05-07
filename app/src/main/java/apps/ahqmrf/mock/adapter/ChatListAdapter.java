package apps.ahqmrf.mock.adapter;

import android.content.Context;
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

/**
 * Created by Lenovo on 5/4/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Message> mItems;
    private String username;
    private String imageUrl;

    private int SENDER = 0;
    private int last = -1;

    public ChatListAdapter(Context mContext, ArrayList<Message> mItems, String imageUrl) {
        this.mContext = mContext;
        this.mItems = mItems;
        this.username = Utility.getString(mContext, Const.Keys.USERNAME);
        this.imageUrl = imageUrl;
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == SENDER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_chat_sender, parent, false);
            return new ViewHolder(view, imageUrl);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_chat_receive, parent, false);
        return new ViewHolder(view, imageUrl);
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ViewHolder holder, int position) {
        Message msg = mItems.get(position);
        holder.msg.setText(msg.getText());
        holder.time.setText(msg.getTime());

        if(msg.isLast()) {
            holder.imageView.setVisibility(View.VISIBLE);
        } else holder.imageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mItems == null? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = mItems.get(position);
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

        public ViewHolder(View itemView, String imageUrl) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Utility.loadImage(imageUrl, imageView);
        }
    }
}