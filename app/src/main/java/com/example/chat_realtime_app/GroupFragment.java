package com.example.chat_realtime_app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_realtime_app.model.GroupModel;
import com.example.chat_realtime_app.utils.FirebaseUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    RecyclerView groupRecyclerView; // sau này dùng
    FloatingActionButton fabCreateGroup;

    public GroupFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);

        groupRecyclerView = view.findViewById(R.id.group_recycler_view);
        fabCreateGroup = view.findViewById(R.id.fab_create_group);

        // TODO: sau này setup RecyclerView hiển thị list group

        fabCreateGroup.setOnClickListener(v -> showCreateGroupDialog());

        return view;
    }

    private void showCreateGroupDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_create_group, null, false);

        EditText groupNameInput = dialogView.findViewById(R.id.et_group_name);
        EditText maxMembersInput = dialogView.findViewById(R.id.et_max_members);
        ProgressBar progressBar = dialogView.findViewById(R.id.progress_create_group);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Create new group")
                .setView(dialogView)
                .setPositiveButton("Create", null) // gắn listener sau để control validate
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = groupNameInput.getText().toString().trim();
                String maxMembersStr = maxMembersInput.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    groupNameInput.setError("Group name is required");
                    return;
                }
                if (TextUtils.isEmpty(maxMembersStr)) {
                    maxMembersInput.setError("Max members is required");
                    return;
                }

                long maxMembers;
                try {
                    maxMembers = Long.parseLong(maxMembersStr);
                } catch (NumberFormatException e) {
                    maxMembersInput.setError("Invalid number");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                createGroupOnFirestore(name, maxMembers,
                        () -> {
                            progressBar.setVisibility(View.GONE);
                            dialog.dismiss();
                            Toast.makeText(getContext(),
                                    "Group created successfully",
                                    Toast.LENGTH_SHORT).show();
                        },
                        errorMsg -> {
                            progressBar.setVisibility(View.GONE);
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            Toast.makeText(getContext(),
                                    "Failed: " + errorMsg,
                                    Toast.LENGTH_SHORT).show();
                        });
            });
        });

        dialog.show();
    }

    private interface OnGroupCreateSuccess {
        void onSuccess();
    }

    private interface OnGroupCreateError {
        void onError(String message);
    }

    private void createGroupOnFirestore(String name, long maxMembers,
                                        OnGroupCreateSuccess onSuccess,
                                        OnGroupCreateError onError) {

        String currentUserId = FirebaseUtil.currentUserId();
        if (currentUserId == null) {
            onError.onError("User not logged in");
            return;
        }

        DocumentReference docRef = FirebaseUtil.allGroupsCollectionReference().document();
        String groupId = docRef.getId();

        List<String> members = new ArrayList<>();
        members.add(currentUserId);

        GroupModel group = new GroupModel(
                groupId,
                name,
                currentUserId,
                Timestamp.now(),
                true,      // isPublic
                maxMembers,
                1,         // memberCount
                members
        );

        docRef.set(group)
                .addOnSuccessListener(unused -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onError.onError(e.getMessage()));
    }
}
