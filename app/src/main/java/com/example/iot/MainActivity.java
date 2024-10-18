package com.example.iot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View; // Asegúrate de tener esta importación
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText usernameField, passwordField;
    private ImageView showPasswordButton;
    private boolean isPasswordVisible = false;  // Estado de visibilidad de la contraseña

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        showPasswordButton = findViewById(R.id.showPasswordButton);

        // Configuración inicial: la contraseña está oculta y el icono es de ojo cerrado
        passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        showPasswordButton.setImageResource(R.drawable.ic_eye_off);
        isPasswordVisible = false;

        // Configuración del botón para mostrar u ocultar la contraseña
        showPasswordButton.setOnClickListener(v -> togglePasswordVisibility(v));
    }

    public void togglePasswordVisibility(View view) {
        if (isPasswordVisible) {
            // Si la contraseña es visible, la ocultamos y cambiamos el icono a ojo cerrado
            passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showPasswordButton.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = false;
        } else {
            // Si la contraseña está oculta, la mostramos y cambiamos el icono a ojo abierto
            passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            showPasswordButton.setImageResource(R.drawable.ic_eye);
            isPasswordVisible = true;
        }

        // Mover el cursor al final del texto
        passwordField.setSelection(passwordField.getText().length());
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
