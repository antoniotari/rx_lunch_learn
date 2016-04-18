package com.antonitoari.rxlearning.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.antonitoari.rxlearning.R;
import com.antonitoari.rxlearning.models.UserProperties;
import com.antonitoari.rxlearning.ui.UserListAdapter.UserRowHolder;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserListAdapter extends RecyclerView.Adapter<UserRowHolder> {
    private OnUserClickListener onUserClickListener;
    private List<UserProperties> mContactList;

    public interface OnUserClickListener {
        void onUserClick(View view, UserProperties userProperties);
    }

    public UserListAdapter(List<UserProperties> contactList) {
        mContactList = contactList;
    }

    public void setOnUserClickListener(final OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    @Override
    public UserRowHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_user, null);
        UserRowHolder viewHolder = new UserRowHolder(view, onUserClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserRowHolder viewHolder, final int position) {
        final UserProperties data = mContactList.get(position);
        Picasso.with(viewHolder.mImageViewUser.getContext())
                .load(data.getUrl())
                .into(viewHolder.mImageViewUser);
        viewHolder.mName.setText(data.getName());
        viewHolder.mUsername.setText(data.getName());

        viewHolder.mImageViewUser.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(v, data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContactList != null ? mContactList.size() : 0;
    }

    public void setContactList(final List<UserProperties> contactList) {
        mContactList = contactList;
    }

    public static class UserRowHolder extends RecyclerView.ViewHolder {
        @Bind (R.id.text_name) TextView mName;
        @Bind (R.id.text_username) TextView mUsername;
        @Bind (R.id.imageViewUsername) ImageView mImageViewUser;

        public UserRowHolder(View rootView, OnUserClickListener onUserClickListener) {
            super(rootView);
            ButterKnife.bind(this, rootView);
        }
    }
}
