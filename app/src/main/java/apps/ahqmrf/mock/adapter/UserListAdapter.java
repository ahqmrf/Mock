package apps.ahqmrf.mock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;

/**
 * Created by bsse0 on 4/29/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    public interface UserClickCallback {
        void onUserClick(User user);
    }

    private Context mContext;
    private UserClickCallback mCallback;
    private ArrayList<User> mItems;

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
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.fullName.setText(mItems.get(position).getFullName());
        holder.username.setText("Username: " + mItems.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return mItems == null? 0 : mItems.size();
    }

    public void setFilter(ArrayList<User> newList) {
        mItems = new ArrayList<>();
        mItems.addAll(newList);
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, username;

        public UserViewHolder(View itemView) {
            super(itemView);
            fullName = (TextView) itemView.findViewById(R.id.text_full_name);
            username = (TextView) itemView.findViewById(R.id.text_username);
        }
    }
}
