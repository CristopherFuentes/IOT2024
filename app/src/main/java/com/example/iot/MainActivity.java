package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText usernameField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
    }

    public void login(View v) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingrese nombre de usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar el usuario en Firestore
        db.collection("usuarios").document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Verificar si la contraseña es correcta
                            String storedPassword = document.getString("password");
                            if (storedPassword.equals(password)) {
                                Intent i = new Intent(MainActivity.this, Principal.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void crearCuenta(View v) {
        Intent i = new Intent(MainActivity.this, Registrar.class);
        startActivity(i);
    }
}
