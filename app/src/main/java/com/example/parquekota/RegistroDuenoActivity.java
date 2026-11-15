package com.example.parquekota;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parquekota.data.ParqueDao;
import com.example.parquekota.models.Dueno;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public final class RegistroDuenoActivity extends AppCompatActivity {
    private TextInputLayout tilNombre;
    private TextInputLayout tilTelefono;
    private TextInputLayout tilEmail;
    private TextInputLayout tilContactoEmergencia;
    private TextInputEditText etNombre;
    private TextInputEditText etTelefono;
    private TextInputEditText etEmail;
    private TextInputEditText etContactoEmergencia;
    private ParqueDao parqueDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_dueno);

        parqueDao = new ParqueDao(getApplicationContext());
        tilNombre = findViewById(R.id.tilNombreDueno);
        tilTelefono = findViewById(R.id.tilTelefonoDueno);
        tilEmail = findViewById(R.id.tilEmailDueno);
        tilContactoEmergencia = findViewById(R.id.tilContactoEmergencia);
        etNombre = findViewById(R.id.etNombreDueno);
        etTelefono = findViewById(R.id.etTelefonoDueno);
        etEmail = findViewById(R.id.etEmailDueno);
        etContactoEmergencia = findViewById(R.id.etContactoEmergencia);

        Button guardar = findViewById(R.id.btnGuardarDueno);
        guardar.setOnClickListener(v -> guardarDueno());
    }

    private void guardarDueno() {
        limpiarErrores();

        String nombre = normalizarEspacios(texto(etNombre));
        String telefono = normalizarTelefono(texto(etTelefono));
        String email = texto(etEmail).trim().toLowerCase(Locale.ROOT);
        String contactoEmergencia = normalizarTelefono(texto(etContactoEmergencia));
        boolean valido = true;

        if (nombre.isEmpty()) {
            tilNombre.setError(getString(R.string.error_nombre_requerido));
            valido = false;
        }
        if (telefono.isEmpty()) {
            tilTelefono.setError(getString(R.string.error_telefono_requerido));
            valido = false;
        } else if (!telefono.matches("\\+?\\d{10,15}")) {
            tilTelefono.setError(getString(R.string.error_telefono_invalido));
            valido = false;
        }
        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_invalido));
            valido = false;
        }
        if (!contactoEmergencia.isEmpty()
                && !contactoEmergencia.matches("\\+?\\d{10,15}")) {
            tilContactoEmergencia.setError(getString(R.string.error_telefono_invalido));
            valido = false;
        }
        if (!valido) {
            return;
        }

        Dueno dueno = new Dueno(0, nombre, telefono,
                email.isEmpty() ? null : email,
                contactoEmergencia.isEmpty() ? null : contactoEmergencia);
        try {
            long idDueno = parqueDao.insertarDueno(dueno);
            Toast.makeText(this, "Dueño registrado con ID " + idDueno, Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } catch (SQLiteException exception) {
            Toast.makeText(this, "No se pudo guardar el dueño. Intenta de nuevo.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarErrores() {
        tilNombre.setError(null);
        tilTelefono.setError(null);
        tilEmail.setError(null);
        tilContactoEmergencia.setError(null);
    }

    private String texto(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString();
    }

    private String normalizarEspacios(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private String normalizarTelefono(String value) {
        return value.trim().replaceAll("[\\s().-]", "");
    }
}
