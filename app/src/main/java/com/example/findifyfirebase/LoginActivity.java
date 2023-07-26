package com.example.findifyfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.regex.PatternSyntaxException;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextloginEmail,editTextloginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "Login Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");
        editTextloginEmail = findViewById(R.id.editText_login_email);
        editTextloginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progressBar);
        // if user logs in and then leaves the app we don't want the user to jump back into the app to again log in
        authProfile = FirebaseAuth.getInstance(); // later

        //login user
        Button buttonlogin = findViewById((R.id.button_login));
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //storing the details entered in the strings
                String textEmail = editTextloginEmail.getText().toString();
                String textPwd =    editTextloginPwd.getText().toString();

                //if left blank
                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please enter your Email ", Toast.LENGTH_SHORT).show();
                    editTextloginEmail.setError("Email  is Required");
                    editTextloginEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Please re-enter your Email ", Toast.LENGTH_SHORT).show();
                    editTextloginEmail.setError("Valid email  is Required");
                    editTextloginEmail.requestFocus();

                } else  if(TextUtils.isEmpty(textPwd)){
                        Toast.makeText(LoginActivity.this, "Please enter your Password ", Toast.LENGTH_SHORT).show();
                        editTextloginPwd.setError("Password is Required");
                        editTextloginPwd.requestFocus();

                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });
    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "You are Logged in now ", Toast.LENGTH_SHORT).show();



                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){ //acount deleted
                        editTextloginEmail.setError("User Does not exist  or  is no longer Valid.Please Register Again ");
                        editTextloginEmail.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){ //when user give wrong details
                        editTextloginEmail.setError("Invalid Credentials.Kindly, check and re-enter. ");
                        editTextloginEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginActivity.this, "Something went Wrong! ", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }


        });

        // check if  User is already Logged in.In such case, Straightaway take the user to the user


    }
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!= null){
            Toast.makeText(LoginActivity.this, "Already Logged in  ", Toast.LENGTH_SHORT).show();

            //Start the page right after loggin in the home page in this case
        }
        else {
            Toast.makeText(LoginActivity.this, "You can login now! ", Toast.LENGTH_SHORT).show();
        }
    }
}