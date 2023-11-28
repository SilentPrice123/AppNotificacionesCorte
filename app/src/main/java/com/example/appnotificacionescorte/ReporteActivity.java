package com.example.appnotificacionescorte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReporteActivity extends AppCompatActivity {

    private Spinner tipoProblemaSpinner;
    private EditText descripcionEditText;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        db = FirebaseFirestore.getInstance();

        tipoProblemaSpinner = findViewById(R.id.tipoProblemaSpinner);
        descripcionEditText = findViewById(R.id.descripcionEditText);

        Button enviarButton = findViewById(R.id.enviar_button);
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarReporte();
            }
        });
    }

    private void enviarReporte() {
        String tipoProblema = tipoProblemaSpinner.getSelectedItem().toString();
        String descripcionProblema = descripcionEditText.getText().toString();

        if (TextUtils.isEmpty(descripcionProblema)) {
            Toast.makeText(this, "Por favor, completa la descripción del problema", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tipoProblema.equals("SELECCIONA")) {
            Toast.makeText(this, "Por favor, selecciona un tipo de problema válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un nuevo documento en la colección "reportes" en Firestore
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("tipoProblema", tipoProblema);
        reporte.put("descripcionProblema", descripcionProblema);

        // Puedes agregar más información según tus necesidades

        db.collection("reportes")
                .add(reporte)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ReporteActivity.this, "Reporte enviado con éxito", Toast.LENGTH_SHORT).show();
                        // Puedes realizar más acciones aquí, como volver a la actividad anterior
                        // o navegar a otra actividad.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReporteActivity.this, "Error al enviar el reporte", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}