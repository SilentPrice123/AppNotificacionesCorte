package com.example.appnotificacionescorte;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs;
    private static final String PREFS_NAME = "MisPreferencias";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private EditText usernameEditText;
    private EditText passwordEditText;

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        // Recuperar credenciales almacenadas si existen
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUsername = prefs.getString(PREF_USERNAME, null);
        String savedPassword = prefs.getString(PREF_PASSWORD, null);

        if (!TextUtils.isEmpty(savedUsername) && !TextUtils.isEmpty(savedPassword)) {
            // Si se han guardado credenciales, llenar automáticamente los campos y validar
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
            validateAndLogin();
        }

        MaterialButton loginButton = findViewById(R.id.funcionPrincipal);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndLogin();
            }
        });
    }
    private void validateAndLogin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            // Campos vacíos, muestra un mensaje
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidUsername(username)) {
            // Usuario contiene caracteres no permitidos
            Toast.makeText(this, "Nombre de usuario no válido. Solo letras son permitidas.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Realiza la consulta a Firebase Firestore
        db.collection("usuarios")
                .document(username)
                .get()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && document.getString("password").equals(password)) {
                            String userType = document.getString("tipo");
                            if (TextUtils.equals(userType, "usuario")) {
                                String usernameToSave = username;
                                String passwordToSave = password;

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString(PREF_USERNAME, usernameToSave);
                                editor.putString(PREF_PASSWORD, passwordToSave);
                                editor.apply();
                                // Usuario y contraseña coinciden, acceso permitido para usuario
                                Intent usuario = new Intent(MainActivity.this,principal_usuario.class);
                                startActivity(usuario);
                            } else if (TextUtils.equals(userType, "administrador")) {
                                // Usuario y contraseña coinciden, acceso permitido para administrador
                                Intent administrador = new Intent(MainActivity.this,principal_admin.class);
                                startActivity(administrador);
                            } else {
                                // Tipo de usuario no reconocido
                                Toast.makeText(this, "Tipo de usuario no válido", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Usuario o contraseña incorrectos
                            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error en la consulta
                        Toast.makeText(this, "Error al verificar credenciales", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}