package apps.ahqmrf.mock.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;

/**
 * Created by bsse0 on 4/29/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    public interface UserClickCallback {
        void onUserClick(User user);
    }

    private Context           mContext;
    private UserClickCallback mCallback;
    private ArrayList<User>   mItems;

    public UserListAdapter(Context mContext, UserClickCallback mCallback, ArrayList<User> mItems) {
        this.mContext = mContext;
        this.mCallback = mCallback;
        this.mItems = mItems;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
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

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, username;
        View layout, progressLayout;
        ImageView imageView;

        public UserViewHolder(View itemView) {
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
                        mCallback.onUserClick(mItems.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
