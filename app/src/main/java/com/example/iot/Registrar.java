package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registrar extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText usernameField, passwordField, confirmPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        db = FirebaseFirestore.getInstance();

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
    }

    public void cuentaCrear(View v) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa con los datos del usuario
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);  // En producción, asegúrate de encriptar las contraseñas

        // Guardar el usuario en Firestore
        db.collection("usuarios")
                .document(username)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Registrar.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Registrar.this, MainActivity.class);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Registrar.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                });
    }
}
