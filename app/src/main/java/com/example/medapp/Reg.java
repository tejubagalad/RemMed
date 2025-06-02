package com.example.medapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Reg extends AppCompatActivity {

    EditText uemail, upasswd, uname;
    Button ureg, login;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reg);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reg), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();   // instantiate firebaseauth
        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        uemail = findViewById(R.id.email);
        upasswd = findViewById(R.id.passwd);
        uname = findViewById(R.id.name);
        ureg = findViewById(R.id.signr);
        login = findViewById(R.id.login);

        login.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        });

        ureg.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String email = uemail.getText().toString().trim();
        String password = upasswd.getText().toString().trim();
        String name = uname.getText().toString().trim(); // Read Name

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(Reg.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user.getUid(), name, email);
                        }
                    } else {
                        Toast.makeText(Reg.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("userId", userId);

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Reg.this, "User data saved!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Reg.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                });
    }
}
