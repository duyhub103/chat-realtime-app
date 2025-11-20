package com.example.chat_realtime_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.*;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ImageView;

import com.example.chat_realtime_app.model.UserModel;
import com.example.chat_realtime_app.utils.AndroidUtil;
import com.example.chat_realtime_app.utils.CloudinaryUtil;
import com.example.chat_realtime_app.utils.FirebaseUtil;
import com.example.chat_realtime_app.utils.KeywordUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    EditText emailInput;
    EditText birthdateInput;
    Spinner genderSpinner;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if(result.getResultCode() == Activity.RESULT_OK){
//                        Intent data = result.getData();
//                        if(data!=null && data.getData()!=null){
//                            selectedImageUri = data.getData();
//                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
//                        }
//                    }
//                }
//        );



        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);

                            setInProgress(true);

                            CloudinaryUtil.uploadAvatar(
                                    getContext(),
                                    selectedImageUri,
                                    "unsigned_chat_avatar",
                                    "dq6ygkf8k",
                                    FirebaseUtil.currentUserDetails(),
                                    () -> {
                                        getActivity().runOnUiThread(() -> {
                                            setInProgress(false);
                                            AndroidUtil.showToast(getContext(), "Updated avatar successfully!");
                                        });
                                    },
                                    () -> {
                                        getActivity().runOnUiThread(() -> {
                                            setInProgress(false);
                                            AndroidUtil.showToast(getContext(), "Upload thất bại!");
                                        });
                                    }
                            );
                        }
                    }
                }
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profle_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        emailInput = view.findViewById(R.id.profile_email);
        birthdateInput = view.findViewById(R.id.profile_birthdate);
        genderSpinner = view.findViewById(R.id.profile_gender);


        getUserData();

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Male", "Female"}
        );

        //thêm gender cho spinner
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick();
        });

        logoutBtn.setOnClickListener(v -> {
            //delete fcm token when logout
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(com.google.android.gms.tasks.Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Token deleted successfully
                        FirebaseUtil.logout();
                        Intent intent = new Intent(getContext(), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        AndroidUtil.showToast(getContext(), "Logged out successfully");
                        startActivity(intent);
                    }
                }
            });
        });

        profilePic.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });
        return view;
    }

    void updateBtnClick(){
        String newUsername = usernameInput.getText().toString().trim();
        String newPhone = phoneInput.getText().toString().trim();
        String newEmail = emailInput.getText().toString().trim();
        String newBirthday = birthdateInput.getText().toString().trim();
        String newGender = genderSpinner.getSelectedItem().toString();
        if(newUsername.isEmpty() || newUsername.length()<3){
            usernameInput.setError("Username must be at least 3 characters chars");
            usernameInput.requestFocus();
            return;
        }

        currentUserModel.setUsername(newUsername);
        currentUserModel.setPhone(newPhone);
        currentUserModel.setEmail(newEmail);
        currentUserModel.setBirthdate(newBirthday);
        currentUserModel.setGender(newGender);


        setInProgress(true);
        updateToFirestore();

    }

    //save to database
    void updateToFirestore(){
        String newEmail = normalizeString(currentUserModel.getEmail());

        // Nếu email trống thì update luôn
        if (newEmail.isEmpty()) {
            executeProfileUpdate();
            return;
        }
        // Nếu có email thì check trùng
        checkEmailAvailability(newEmail);
    }

    // check email trùng
    private void checkEmailAvailability(String email) {
        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        setInProgress(false);
                        AndroidUtil.showToast(getContext(), "Error checking email");
                        return;
                    }

                    if (isEmailTakenByOtherUser(task.getResult())) {
                        setInProgress(false);
                        AndroidUtil.showToast(getContext(), "This email is already exists");
                    } else {
                        executeProfileUpdate();
                    }
                });
    }

    private boolean isEmailTakenByOtherUser(QuerySnapshot result) {
        for (DocumentSnapshot doc : result) {
            String uid = doc.getId();
            if (!uid.equals(FirebaseUtil.currentUserId())) {
                return true;
            }
        }
        return false;
    }


    private String normalizeString(String value) {
        return value != null ? value.trim() : "";
    }


    //thực hiện update vào database
    private void executeProfileUpdate() {
        // Tạo keywords từ username
        String username = normalizeString(currentUserModel.getUsername());

        List<String> keywords = KeywordUtils.generateKeywords(username.toLowerCase());

        FirebaseUtil.currentUserDetails()
                .update(
                        "username", username,
                        "phone", normalizeString(currentUserModel.getPhone()),
                        "email", normalizeString(currentUserModel.getEmail()),
                        "birthdate", currentUserModel.getBirthdate(),
                        "gender", currentUserModel.getGender(),
                        "searchKeywords", keywords
                )
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(getContext(), "Updated Successfully");
                    }else{
                        AndroidUtil.showToast(getContext(), "Updated Failed");
                    }
                });
    }



    void getUserData(){

        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
           currentUserModel = task.getResult().toObject(UserModel.class);
           usernameInput.setText(currentUserModel.getUsername());
           phoneInput.setText(currentUserModel.getPhone());

           emailInput.setText(currentUserModel.getEmail());
           birthdateInput.setText(currentUserModel.getBirthdate());

           //gender
            if(currentUserModel.getGender() != null){
                if(currentUserModel.getGender().equalsIgnoreCase("Male")){
                    genderSpinner.setSelection(0);
                }else{
                    genderSpinner.setSelection(1);
                }
            }

           //get avatar
            if (currentUserModel.getAvatarUrl() != null && !currentUserModel.getAvatarUrl().isEmpty()) {
                AndroidUtil.setProfilePic(getContext(), Uri.parse(currentUserModel.getAvatarUrl()), profilePic);
            }

            loginProvider();
        });
    }

    private void loginProvider() {
        if (currentUserModel == null) return;

        String phone = currentUserModel.getPhone();
        String email = currentUserModel.getEmail();

        // Nếu user đăng ký bằng SĐT thì  cho phép sửa email, khóa phone
        if (phone != null && !phone.isEmpty()) {
            phoneInput.setEnabled(false);
            emailInput.setEnabled(true);
        }
        // Nếu user đăng ký bằng email thì cho sửa phone, khóa email
        else if (email != null && !email.isEmpty()) {
            phoneInput.setEnabled(true);
            emailInput.setEnabled(false);
        }
        else {
            phoneInput.setEnabled(true);
            emailInput.setEnabled(true);
        }
    }


    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}