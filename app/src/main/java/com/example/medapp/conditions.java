package com.example.medapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class conditions extends AppCompatActivity {
    private ListView conditionsListView;
    private Button addConditionButton,logout;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CollectionReference conditionsRef;
    private String userId;

    private ArrayAdapter<String> conditionsAdapter;
    private List<String> conditionsList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conditions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.conditions), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity(); // Exits the app completely
            }
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(conditions.this, MainActivity.class);
                startActivity(i);
            }
        });


        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Intent i = new Intent(conditions.this,MainActivity.class);
            startActivity((i));
            finish();
            return;
        }


        userId = user.getUid();
        db = FirebaseFirestore.getInstance();
        conditionsRef = db.collection("users").document(userId).collection("conditions");

        // Initialize UI
        conditionsListView = findViewById(R.id.conditionsListView);
        addConditionButton = findViewById(R.id.addc);

        conditionsList = new ArrayList<>();
        conditionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, conditionsList);
        conditionsListView.setAdapter(conditionsAdapter);

        loadConditions();

        addConditionButton.setOnClickListener(v -> addNewCondition());

        conditionsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCondition = conditionsList.get(position);
            Intent intent = new Intent(conditions.this, meds.class);
            intent.putExtra("conditionName", selectedCondition);
            startActivity(intent);
        });
    }

    private void loadConditions() {
        conditionsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                conditionsList.clear();
                for (var document : task.getResult()) {
                    conditionsList.add(document.getId());
                }
                conditionsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addNewCondition() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Condition Name");

        final android.widget.EditText input = new android.widget.EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String conditionName = input.getText().toString().trim();
            if (!conditionName.isEmpty()) {
                conditionsRef.document(conditionName).set(new java.util.HashMap<>())
                        .addOnSuccessListener(aVoid -> {
                            loadConditions();
                            Toast.makeText(this, "Condition added!", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}