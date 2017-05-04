package apps.ahqmrf.mock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import apps.ahqmrf.mock.Confirmation;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lenovo on 5/3/2017.
 */

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int REQUEST      = 0;
    private final int NOTIFICATION = 1;

    public interface Callback {
        void onFullNameClick(User user);

        void onConfirmClick(User user);

        void onDeclineClick(User user);

        void onDeleteClick(Confirmation confirmation);
    }

    private Context           mContext;
    private Callback          mCallback;
    private ArrayList<Object> mItems;

    public NotificationListAdapter(Context mContext, Callback mCallback, ArrayList<Object> mItems) {
        this.mContext = mContext;
        this.mCallback = mCallback;
        this.mItems = mItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == REQUEST) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_request, parent, false);
            return new RequestViewHolder(view);
        }
        view = LayoutInflater.from(mContext).inflate(R.layout.list_item_notification, parent, false);
        return new ConfirmationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == REQUEST) {
            RequestViewHolder holder = (RequestViewHolder) rHolder;
            User user = (User) mItems.get(position);
            String text = user.getFullName() + " wants to be your friend.";
            holder.fullName.setText(text);
            Utility.loadImage(user.getImageUrl(), holder.imageView, holder.progressLayout);
        } else {
            ConfirmationViewHolder holder = (ConfirmationViewHolder) rHolder;
            Confirmation con = (Confirmation) mItems.get(position);
            String text = con.getFullName() + " accepted your friend request.";
            holder.textView.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void setFilter(ArrayList<Object> newItems) {
        mItems.clear();
        mItems.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = mItems.get(position);
        if (object instanceof User) return REQUEST;
        return NOTIFICATION;
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_main)     ImageView imageView;
        @BindView(R.id.text_full_name) TextView  fullName;
        @BindView(R.id.layout_progress) View progressLayout;

        RequestViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.button_confirm)
        void confirm() {
            if(mCallback != null) {
                int position = getAdapterPosition();
                mCallback.onConfirmClick((User) mItems.get(position));
                mItems.remove(position);
                notifyItemRemoved(position);
            }
        }

        @OnClick(R.id.button_decline)
        void decline() {
            if(mCallback != null) {
                int position = getAdapterPosition();
                mCallback.onDeclineClick((User) mItems.get(position));
                mItems.remove(position);
                notifyItemRemoved(position);
            }
        }

        @OnClick({R.id.text_full_name, R.id.image_main})
        void showProfile() {
            User user = (User) mItems.get(getAdapterPosition());
            if(mCallback != null) mCallback.onFullNameClick(user);
        }
    }

    public class ConfirmationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_text) TextView  textView;

        ConfirmationViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.image_delete)
        void removeConfirmation() {
            if(mCallback != null) {
                int position = getAdapterPosition();
                mCallback.onDeleteClick((Confirmation) mItems.get(position));
                mItems.remove(position);
                notifyItemRemoved(position);
            }
        }
    }
}
