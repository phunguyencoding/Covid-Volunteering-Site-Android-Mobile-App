package com.example.cosc2657_a2_s3811248_nguyentranphu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignupActivity extends AppCompatActivity {

    FirebaseAuth authenticator;
    FireBaseHandler fireBaseHandler = new FireBaseHandler();
    private TextInputEditText signupNameTextInputEditText,
            signupAddressTextInputEditText,
            signupEmailTextInputEditText,
            signupPasswordTextInputEditText;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //FireBase
        authenticator = FirebaseAuth.getInstance();

        // signup data
        signupNameTextInputEditText = (TextInputEditText) findViewById(R.id.signupNameTextInputEditText);
        signupAddressTextInputEditText = (TextInputEditText) findViewById(R.id.signupAddressTextInputEditText);
        signupEmailTextInputEditText = (TextInputEditText) findViewById(R.id.signupEmailTextInputEditText);
        signupPasswordTextInputEditText = (TextInputEditText) findViewById(R.id.signupPasswordTextInputEditText);

        //Signup Button
        signupBtn = (Button) findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signupNameTextInputEditText.getText().toString().trim();
                String address = signupAddressTextInputEditText.getText().toString();
                String email = signupEmailTextInputEditText.getText().toString().trim();
                String password = signupPasswordTextInputEditText.getText().toString();

                if (name.length() != 0 && email.length() != 0 && password.length() != 0 && address.length() != 0) {
                    signup(email, password, name, address);
                } else {
                    Toast.makeText(SignupActivity.this, "Fill empty blanks please!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signup(String email, String password, String name, String address) {
        authenticator.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    fireBaseHandler.addUser(name, email, address);
                    Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                    intent.putExtra("resMessage","User is created");
                    intent.putExtra("name",name);
                    intent.putExtra("email",email);
                    intent.putExtra("address",address);
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else {
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        signupPasswordTextInputEditText.setError("Password not strong enough!");
                        signupPasswordTextInputEditText.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        signupEmailTextInputEditText.setError("Invalid Email");
                        signupEmailTextInputEditText.requestFocus();
                    } catch(FirebaseAuthUserCollisionException e) {
                        signupEmailTextInputEditText.setError("User already exists");
                        signupEmailTextInputEditText.requestFocus();
                    } catch(Exception e) {
                        Log.e("Other Exceptions", e.getMessage());
                    }
                }
            }
        });
    }
}