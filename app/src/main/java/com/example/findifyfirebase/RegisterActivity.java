package com.example.findifyfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    //defining edit text variables so that we can take the input from the user along with progressbar radio group and radio buttom
    private EditText    editTextRegisterFullName,editTextRegisterEmail,editTextRegisterDoB,editTextRegisterMobile,editTextRegisterPwd,editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;

    private static final String  TAG= "RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //title of the activity
        getSupportActionBar().setTitle("Register");


        Toast.makeText(RegisterActivity.this,"you can register here ", Toast.LENGTH_LONG).show();
        progressBar = findViewById(R.id.progressBar);

        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail= findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);


        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
         radioGroupRegisterGender.clearCheck();

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);



                String textFullName = editTextRegisterFullName.getText().toString(); //extract data edit text variable. gets the text. to convert it into string
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB= editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                String textGender;


                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this, "Please enter your full name ", Toast.LENGTH_SHORT).show();
                    editTextRegisterFullName.setError("Full Name  is Required");
                    editTextRegisterFullName.requestFocus();
                }else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this, "Please enter your email ", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Email is Required");
                    editTextRegisterEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email ", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Valid email is Required");
                    editTextRegisterEmail.requestFocus();
                }else if(TextUtils.isEmpty(textDoB)){
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth  ", Toast.LENGTH_SHORT).show();
                    editTextRegisterDoB.setError("Date of Birth is required");
                    editTextRegisterDoB.requestFocus();
                }else if(radioGroupRegisterGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegisterActivity.this, "Please select your gender  ", Toast.LENGTH_SHORT).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }else if (TextUtils.isEmpty(textMobile)){
                    Toast.makeText(RegisterActivity.this, "Please select your mobile no ", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile no is required");
                    editTextRegisterMobile.requestFocus();
                }else if(textMobile.length() != 10){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile no  ", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile no should be 10 digits ");
                    editTextRegisterMobile.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your password  ", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                }else if(textPwd.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits  ", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password is too weak");
                    editTextRegisterPwd.requestFocus();
                }else if(TextUtils.isEmpty(textConfirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Please  confirm your password  ", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                }else if(!textPwd.equals(textConfirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Please enter the same password ", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.setError("Password Confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                    // clear the entered passwords
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                }else{
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textDoB,textGender,textMobile,textPwd);
                }
            }
        });

    }
        //register user using the credentials given
        //create user profile
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth  = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //this means user is created


                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        //update display name  of  user

                        UserProfileChangeRequest profileChangeRequest  = new UserProfileChangeRequest.Builder()
                                .setDisplayName(textFullName)
                                .build();
                        firebaseUser.updateProfile(profileChangeRequest);


                        ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB,textGender,textMobile);



                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                        referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){
                                   //send verification email
                                   firebaseUser.sendEmailVerification();

                                   Toast.makeText(RegisterActivity.this, "User registered successfully. Please Verify your email ",
                                           Toast.LENGTH_LONG).show();



                                  /*        //open user profile after successful registration
                                    Intent intent = new Intent(RegisterActivity.this, UserProfileChangeRequest.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    //using these 3 flags so that the user cant return to the register activity after successful registration
                                    startActivity(intent);
                                    finish();// to close register activity */

                               }else {
                                   Toast.makeText(RegisterActivity.this, "User registration failed. Please try again later",
                                           Toast.LENGTH_LONG).show();

                               }
                                //hide the progress bar regardless of whether user successfully registered or not
                                progressBar.setVisibility(View.GONE);


                            }
                        }); //


                    }else{
                        try{
                            throw task.getException();
                        }catch (FirebaseAuthWeakPasswordException e){
                            editTextRegisterPwd.setError("Your password is too weak. Kindly use  a mix of alphabets,numbers and special characters");
                            editTextRegisterPwd.requestFocus();
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            editTextRegisterPwd.setError("Your email is invalid or already in use. Kindly re-enter");
                            editTextRegisterPwd.requestFocus();
                        }catch (FirebaseAuthUserCollisionException e){
                            editTextRegisterPwd.setError("User is already Registered with this email . Kindly use another one");
                            editTextRegisterPwd.requestFocus();
                        }catch(Exception e ){
                            Log.e(TAG,e.getMessage());

                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        progressBar.setVisibility(View.GONE);
                    }

                }
            });
    }
}