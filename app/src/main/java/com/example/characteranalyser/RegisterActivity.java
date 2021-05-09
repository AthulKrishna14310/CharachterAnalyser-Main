package com.example.characteranalyser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private EditText regEmailField;
    private EditText regPassField;
    private EditText regConfirmPassField;
    private TextView regBtn;
    private TextView regLoginBtn;
    private ProgressBar regProgress;
    private EditText regNameField;
    private EditText regSchoolField;
    private EditText regClassField;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        regEmailField = findViewById(R.id.reg_email);
        regPassField = findViewById(R.id.reg_pass);
        regNameField = findViewById(R.id.name);
        regConfirmPassField = findViewById(R.id.reg_confirm_pass);
        regBtn = findViewById(R.id.reg_btn);
        regLoginBtn = findViewById(R.id.reg_login_btn);
        regSchoolField = findViewById(R.id.school);
        regClassField = findViewById(R.id.class_of_student);
        regProgress = findViewById(R.id.reg_progress);

        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regLoginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(regLoginIntent);
                finish();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmailField.getText().toString();
                String pass = regPassField.getText().toString();
                String confirm_pass = regConfirmPassField.getText().toString();
                String Name = regNameField.getText().toString();
                String Class = regClassField.getText().toString();
                String school=regSchoolField.getText().toString();
                if (!TextUtils.isEmpty(email) &&
                        !TextUtils.isEmpty(pass) &&
                        !TextUtils.isEmpty(confirm_pass)) {
                    if (pass.equals(confirm_pass)) {
                        regProgress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    userId = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = db.collection("user").document(userId);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email" ,email);
                                    user.put("name" ,Name);
                                    user.put("class" ,Class);
                                    user.put("school",school);
                                    documentReference
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "onSuccess: user profile is created for" + userId);
                                                    Toast.makeText(RegisterActivity.this,"Success",Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    Intent setupIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(setupIntent);
                                    finish();
                                } else {
                                    String errorMessage  = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error : " + errorMessage,Toast.LENGTH_LONG).show();
                                }
                                regProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Password doesn't match",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}