package apps.ahqmrf.mock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import apps.ahqmrf.mock.LastMessage;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lenovo on 5/7/2017.
 */

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.Holder> {

    public interface Callback {
        void onConversationClick(LastMessage message);
    }

    private Context mContext;
    private Callback mCallback;
    private ArrayList<LastMessage> mItems;

    public ConversationListAdapter(Context mContext, Callback mCallback, ArrayList<LastMessage> mItems) {
        this.mContext = mContext;
        this.mCallback = mCallback;
        this.mItems = mItems;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_conversation, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        LastMessage message = mItems.get(position);
        Utility.loadImage(message.getImageUrl(), holder.imageView);
        holder.text.setText(message.getText());
        holder.time.setText(message.getTime());
        holder.name.setText(message.getFullName());
    }

    @Override
    public int getItemCount() {
        return mItems == null? 0 : mItems.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_text) TextView text;
        @BindView(R.id.text_name) TextView name;
        @BindView(R.id.text_time) TextView time;
        @BindView(R.id.image_main) ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.layout)
        public void openChat() {
            LastMessage message = mItems.get(getAdapterPosition());
            if(mCallback != null) {
                mCallback.onConversationClick(message);
            }
        }
    }
}
