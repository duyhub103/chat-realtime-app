package com.example.chat_realtime_app.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.example.chat_realtime_app.ChatActivity;
import com.example.chat_realtime_app.GroupChatActivity;
import com.example.chat_realtime_app.R;
import com.example.chat_realtime_app.model.ChatroomModel;
import com.example.chat_realtime_app.model.GroupModel;
import com.example.chat_realtime_app.model.UserModel;
import com.example.chat_realtime_app.utils.AndroidUtil;
import com.example.chat_realtime_app.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
public class GroupListRecyclerAdapter extends FirestoreRecyclerAdapter<GroupModel, GroupListRecyclerAdapter.GroupViewHolder> {

    Context context;

    public GroupListRecyclerAdapter(@NonNull FirestoreRecyclerOptions<GroupModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupViewHolder holder, int position, @NonNull GroupModel model) {

        try {
            holder.groupName.setText(model.getName());
            if (model.getMembers() != null) {
                holder.memberCount.setText(model.getMembers().size() + " members");
            } else {
                holder.memberCount.setText("0 members");
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId", model.getGroupId());
                context.startActivity(intent);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_list_row, parent, false);
        return new GroupViewHolder(view);
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, memberCount;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name_text);
            memberCount = itemView.findViewById(R.id.group_member_count);
        }
    }
}

