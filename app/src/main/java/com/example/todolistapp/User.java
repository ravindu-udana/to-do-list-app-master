package com.example.todolistapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolistapp.Utils.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class User extends AppCompatActivity {

    private ImageButton arrow;
    private Button logout;
    private TextView nameText;
    private Button changeNameButton;
    private DatabaseHandler dbHandler;
    private int userId = 1; // Example user ID, you may get it dynamically

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Navigate to home page
        arrow = findViewById(R.id.arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Logout
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nameText = findViewById(R.id.name_text);
        changeNameButton = findViewById(R.id.change_name);
        dbHandler = new DatabaseHandler(User.this);
        dbHandler.openDatabase();

        // Ensure a default user entry exists
        ensureDefaultUser();

        // Load the current user name and display it
        loadUserName();

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeNameDialog();
            }
        });
    }

    private void ensureDefaultUser() {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE user_id = ?", new String[]{String.valueOf(userId)});
        if (cursor == null || !cursor.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put("user_id", userId);
            cv.put("name", "Ravindu Udana"); // Default name
            cv.put("user_status", 1); // Example status
            db.insert("user", null, cv);
            Log.d("DatabaseHandler", "Default user inserted");
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void loadUserName() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.query("user", new String[]{"name"}, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            nameText.setText(name);
            Log.d("DatabaseHandler", "User name loaded: " + name);
            cursor.close();
        } else {
            Log.d("DatabaseHandler", "No user found with user_id: " + userId);
        }
    }

    private void showChangeNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Name");

        final EditText input = new EditText(this);
        input.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                updateUserName(newName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateUserName(String newName) {
        ContentValues cv = new ContentValues();
        cv.put("name", newName);

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.update("user", cv, "user_id=?", new String[]{String.valueOf(userId)});

        // Update the TextView
        nameText.setText(newName);
        Log.d("DatabaseHandler", "User name updated: " + newName);
    }
}
