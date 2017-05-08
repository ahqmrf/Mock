package apps.ahqmrf.mock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.util.Utility;

/**
 * Created by bsse0 on 4/29/2017.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    public interface FriendClickCallback {
        void onFriendClick(User user);
        void onChatClick(User user);
        void onMapClick(User user);
    }

    private Context           mContext;
    private FriendClickCallback mCallback;
    private ArrayList<User>   mItems;

    public FriendListAdapter(Context mContext, FriendClickCallback mCallback, ArrayList<User> mItems) {
        this.mContext = mContext;
        this.mCallback = mCallback;
        this.mItems = mItems;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendViewHolder holder, int position) {
        User user = mItems.get(position);
        holder.fullName.setText(user.getFullName());
        holder.username.setText(user.getUsername());
        Utility.loadImage(user.getImageUrl(), holder.imageView, holder.progressLayout);
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void setFilter(ArrayList<User> newList) {
        mItems = new ArrayList<>();
        mItems.addAll(newList);
        notifyDataSetChanged();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, username;
        View layout, progressLayout;
        ImageView imageView, chatView, mapView;

        public FriendViewHolder(View itemView) {
            super(itemView);
            fullName = (TextView) itemView.findViewById(R.id.text_full_name);
            username = (TextView) itemView.findViewById(R.id.text_username);
            imageView = (ImageView) itemView.findViewById(R.id.image_main);
            progressLayout = itemView.findViewById(R.id.layout_progress);
            layout = itemView.findViewById(R.id.item_list_user);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onFriendClick(mItems.get(getAdapterPosition()));
                    }
                }
            });
            chatView = (ImageView) itemView.findViewById(R.id.image_chat);
            chatView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCallback != null) mCallback.onChatClick(mItems.get(getAdapterPosition()));
                }
            });

            mapView = (ImageView) itemView.findViewById(R.id.image_map);
            mapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCallback != null) mCallback.onMapClick(mItems.get(getAdapterPosition()));
                }
            });
        }
    }
}
