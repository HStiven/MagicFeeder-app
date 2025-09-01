package com.example.singuploginfirebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private EditText signup_name, signup_lastname, inputUsuario, signupEmail, signupPassword;
    private Button signungButton;
    AppCompatButton btnCancelar;
    private TextView loginRedirectText;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        signup_name = findViewById(R.id.signup_name);
        signup_lastname = findViewById(R.id.signup_Lastname);
        inputUsuario = findViewById(R.id.inputUsuario);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signungButton = findViewById(R.id.signup_button);
        btnCancelar = findViewById(R.id.btnCancelar);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signungButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signup_name.getText().toString().trim();
                String lastname = signup_lastname.getText().toString().trim();
                String nickname = inputUsuario.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();

                if (name.isEmpty()) {
                    signup_name.setError("El nombre está vacío");
                    return;
                }
                if (lastname.isEmpty()) {
                    signup_lastname.setError("El apellido está vacío");
                    return;
                }
                if (nickname.isEmpty()) {
                    inputUsuario.setError("El apellido está vacío");
                    return;
                }
                if (email.isEmpty()) {
                    signupEmail.setError("El correo electrónico no puede estar vacío");
                    return;
                }
                if (pass.isEmpty()) {
                    signupPassword.setError("La contraseña no puede estar vacía");
                    return;
                }

                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                writeNewUser(userId, name, lastname, nickname, email);
                            }
                            Toast.makeText(SingUpActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SingUpActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SingUpActivity.this, "Error de registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

        private void writeNewUser(String userId, String name, String lastname,String nickname, String email) {
            DatabaseReference userRef = mDatabase.child("users").child(userId);

            userRef.child("nombre").setValue(name);
            userRef.child("apellido").setValue(lastname);
            userRef.child("usuario").setValue(nickname);
            userRef.child("email").setValue(email);
            userRef.child("numero").setValue("");
            userRef.child("edad").setValue("");
            userRef.child("nombreMascota").setValue("");
            userRef.child("razaMascota").setValue("");
            userRef.child("tipoMascota").setValue("");
        }
}
