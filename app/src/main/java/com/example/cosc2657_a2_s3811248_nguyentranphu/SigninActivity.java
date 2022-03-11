package com.example.cosc2657_a2_s3811248_nguyentranphu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingService;


public class SigninActivity extends AppCompatActivity {

    FirebaseAuth authenticator;
    FireBaseHandler fireBaseHandler = new FireBaseHandler();;
    private Button signinBtn, signupBtn;
    private TextView skipTextView;
    private TextInputEditText signinEmailTextInputEditText, signinPasswordTextInputEditText;
//    sends listener an initial snapshot of the data,
//    and then another snapshot each time the document changes.
    ArrayList<Task<QuerySnapshot>> data = fireBaseHandler.retrieveUserData();
    HashMap<String,User> userMap = new HashMap<String,User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Notification
//        FirebaseMessaging.getInstance().subscribeToTopic("notification");

        //FireBase Authenticator
        authenticator = FirebaseAuth.getInstance();
        data.get(0).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean isNewUser = true;
                String name ="", email ="", address ="";
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    name = Objects.requireNonNull(documentSnapshot.getData().get("name")).toString();
                    address = Objects.requireNonNull(documentSnapshot.getData().get("address")).toString();
                    email = Objects.requireNonNull(documentSnapshot.getData().get("email")).toString();
                    isNewUser = Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.getData().get("isNewUser")).toString());

                    User user = new User(name,email,address);
                    user.setIsNewUser(isNewUser);
                    userMap.put(email,user);
                }
            }
        });

        //Buttons
        signinBtn = (Button) findViewById(R.id.signinBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        skipTextView = (TextView) findViewById(R.id.skipTextView);

        //Data
        signinEmailTextInputEditText = (TextInputEditText) findViewById(R.id.signinEmailTextInputEditText);
        signinPasswordTextInputEditText = (TextInputEditText) findViewById(R.id.signinPasswordTextInputEditText);

        /** Login Button*/
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String email = signinEmailTextInputEditText.getText().toString().trim();
                String password = signinPasswordTextInputEditText.getText().toString();
                if (!email.equals("") && !password.equals("")) {
                    signin(email, password);
                } else {
                    Toast.makeText(SigninActivity.this, "Please enter user information", Toast.LENGTH_SHORT).show();
                }
            }
        });


        /** Register Button*/
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        /** Skip TextView*/
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                changeMapMode();
            }
        });
    }

    private void changeMapMode() {
        Intent intent = new Intent(SigninActivity.this, MapsActivity.class);
        //kill previous activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(SigninActivity.this, "Welcome to the Covid Free App!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveUserName(String name) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(SigninActivity.this);
        sPref.edit().putString("name",name).apply();
    }

    private void signin(String email, String password) {
        authenticator.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // if new user signs in first time => show MapActivity to choose volunteer place
                if (task.isSuccessful()){
                    if (userMap.get(email).getIsNewUser()) {
                        changeMapMode();
                        fireBaseHandler.updateIsNewUser(email);
                        saveUserName(userMap.get(email).getName());
                        // Otherwise, go to MainActivity to keep track location list
                    } else {
                        // change to Main Activity
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(SigninActivity.this, "Welcome to Covid Free App!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else {
                    Toast.makeText(SigninActivity.this, "Wrong Email or Password. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // After finishing the activity and returning, receive the result with appropriate requestCode
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 100) {
            if (resultCode==RESULT_OK) {
                String res = "", name = "", address = "", email = "";
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    res = (String) bundle.get("resMessage");
                    name = (String) bundle.get("name");
                    address = (String) bundle.get("address");
                    email = (String) bundle.get("email");

                    userMap.put(email,new User(name,email,address));
                }
                Toast.makeText(SigninActivity.this, res, Toast.LENGTH_SHORT).show();
            }
        }
    }
}