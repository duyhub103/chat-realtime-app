package com.example.chat_realtime_app;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_realtime_app.adapter.ChatRecyclerAdapter;
import com.example.chat_realtime_app.model.ChatMessageModel;
import com.example.chat_realtime_app.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

public class GroupChatActivity extends AppCompatActivity {

    String groupId;
    String groupName;

    EditText messageInput;
    ImageButton sendBtn, backBtn;
    TextView groupNameText;
    RecyclerView recyclerView;

    ChatRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");

        messageInput = findViewById(R.id.group_chat_message_input);
        sendBtn = findViewById(R.id.group_message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        groupNameText = findViewById(R.id.group_name_text);
        recyclerView = findViewById(R.id.group_chat_recycler_view);

        groupNameText.setText(groupName != null ? groupName : "Group chat");

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        setupRecyclerView();

        sendBtn.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (msg.isEmpty()) return;
            sendMessage(msg);
        });
    }

    private void setupRecyclerView() {
        Query query = FirebaseUtil.getGroupMessageReference(groupId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options =
                new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                        .setQuery(query, ChatMessageModel.class)
                        .build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessage(String message) {
        ChatMessageModel model = new ChatMessageModel(
                message,
                FirebaseUtil.currentUserId(),
                Timestamp.now()
        );

        // Lưu message vào subcollection messages
        FirebaseUtil.getGroupMessageReference(groupId)
                .add(model)
                .addOnSuccessListener(docRef -> {
                    messageInput.setText("");
                    // update last message cho group
                    FirebaseUtil.getGroupReference(groupId)
                            .update(
                                    "lastMessage", message,
                                    "lastMessageTimestamp", Timestamp.now()
                            );
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        if (adapter != null) adapter.stopListening();
        super.onStop();
    }
}
