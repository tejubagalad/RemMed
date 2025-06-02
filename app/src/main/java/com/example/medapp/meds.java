package com.example.medapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.List;

public class meds extends AppCompatActivity {

    private TextView conditionTitle;
    private ListView tabletsListView, remediesListView;
    private Button addTabletButton, addRemedyButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;
    private String conditionName;

    private ArrayAdapter<String> tabletsAdapter, remediesAdapter;
    private List<String> tabletsList, remediesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meds);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.meds), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = user.getUid();
        db = FirebaseFirestore.getInstance();

        // Get condition name from intent
        conditionName = getIntent().getStringExtra("conditionName");

        // Instantiate UI
        conditionTitle = findViewById(R.id.conditionTitle);
        tabletsListView = findViewById(R.id.tabletsListView);
        remediesListView = findViewById(R.id.remediesListView);
        addTabletButton = findViewById(R.id.addTabletButton);
        addRemedyButton = findViewById(R.id.addRemedyButton);

        conditionTitle.setText(conditionName);

        // Initialize Lists & Adapters
        tabletsList = new ArrayList<>();
        remediesList = new ArrayList<>();

        tabletsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tabletsList);
        remediesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, remediesList);

        tabletsListView.setAdapter(tabletsAdapter);
        remediesListView.setAdapter(remediesAdapter);

        loadMedsData();

        addTabletButton.setOnClickListener(v -> addNewItem("tablets"));
        addRemedyButton.setOnClickListener(v -> addNewItem("remedies"));

        // Set Listeners for Editing/Deleting Items
        tabletsListView.setOnItemClickListener((parent, view, position, id) -> showEditDeleteDialog("tablets", tabletsList.get(position)));
        remediesListView.setOnItemClickListener((parent, view, position, id) -> showEditDeleteDialog("remedies", remediesList.get(position)));
    }

    private void loadMedsData() {
        db.collection("users").document(userId).collection("conditions").document(conditionName)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        tabletsList.clear();
                        remediesList.clear();

                        if (document.contains("tablets")) {
                            tabletsList.addAll((List<String>) document.get("tablets"));
                        }
                        if (document.contains("remedies")) {
                            remediesList.addAll((List<String>) document.get("remedies"));
                        }

                        tabletsAdapter.notifyDataSetChanged();
                        remediesAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void addNewItem(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new " + type.substring(0, type.length() - 1));

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newItem = input.getText().toString().trim();
            if (!newItem.isEmpty()) {
                db.collection("users").document(userId).collection("conditions").document(conditionName)
                        .update(type, FieldValue.arrayUnion(newItem))
                        .addOnSuccessListener(aVoid -> loadMedsData());
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showEditDeleteDialog(String type, String item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete?");

        final EditText input = new EditText(this);
        input.setText(item);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedItem = input.getText().toString().trim();
            if (!updatedItem.isEmpty() && !updatedItem.equals(item)) {
                db.collection("users").document(userId).collection("conditions").document(conditionName)
                        .update(type, FieldValue.arrayRemove(item))
                        .addOnSuccessListener(aVoid ->
                                db.collection("users").document(userId).collection("conditions").document(conditionName)
                                        .update(type, FieldValue.arrayUnion(updatedItem))
                                        .addOnSuccessListener(aVoid1 -> loadMedsData())
                        );
            }
        });

        builder.setNeutralButton("Delete", (dialog, which) -> {
            db.collection("users").document(userId).collection("conditions").document(conditionName)
                    .update(type, FieldValue.arrayRemove(item))
                    .addOnSuccessListener(aVoid -> loadMedsData());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
