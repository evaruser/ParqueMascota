package com.example.parquekota;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parquekota.data.ParqueDao;
import com.example.parquekota.models.Dueno;
import com.example.parquekota.models.Mascota;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public final class RegistroMascotaActivity extends AppCompatActivity {
    private final List<Dueno> duenios = new ArrayList<>();
    private final List<String> opcionesDuenos = new ArrayList<>();

    private ParqueDao parqueDao;
    private ArrayAdapter<String> adapterDuenos;
    private Spinner spDueno;
    private Spinner spTamano;
    private TextView tvErrorDueno;
    private TextView tvErrorTamano;
    private TextInputLayout tilNombre;
    private TextInputLayout tilPeso;
    private TextInputEditText etNombre;
    private TextInputEditText etEspecie;
    private TextInputEditText etRaza;
    private TextInputEditText etPeso;
    private TextInputEditText etTemperamento;
    private TextInputEditText etNotasMedicas;
    private RadioButton rbVacunasSi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_mascota);

        parqueDao = new ParqueDao(getApplicationContext());
        spDueno = findViewById(R.id.spDueno);
        spTamano = findViewById(R.id.spTamano);
        tvErrorDueno = findViewById(R.id.tvErrorDueno);
        tvErrorTamano = findViewById(R.id.tvErrorTamano);
        tilNombre = findViewById(R.id.tilNombreMascota);
        tilPeso = findViewById(R.id.tilPeso);
        etNombre = findViewById(R.id.etNombreMascota);
        etEspecie = findViewById(R.id.etEspecie);
        etRaza = findViewById(R.id.etRaza);
        etPeso = findViewById(R.id.etPeso);
        etTemperamento = findViewById(R.id.etTemperamento);
        etNotasMedicas = findViewById(R.id.etNotasMedicas);
        rbVacunasSi = findViewById(R.id.rbVacunasSi);

        adapterDuenos = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesDuenos);
        adapterDuenos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDueno.setAdapter(adapterDuenos);

        Button nuevoDueno = findViewById(R.id.btnNuevoDueno);
        nuevoDueno.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroDuenoActivity.class)));
        Button guardar = findViewById(R.id.btnGuardarMascota);
        guardar.setOnClickListener(v -> guardarMascota());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDuenos();
    }

    private void cargarDuenos() {
        try {
            duenios.clear();
            duenios.addAll(parqueDao.obtenerDuenos());
            opcionesDuenos.clear();
            opcionesDuenos.add(duenios.isEmpty()
                    ? getString(R.string.sin_duenos)
                    : getString(R.string.selecciona_dueno));
            for (Dueno dueno : duenios) {
                opcionesDuenos.add(dueno.toString());
            }
            adapterDuenos.notifyDataSetChanged();
            spDueno.setSelection(0);
        } catch (SQLiteException exception) {
            Toast.makeText(this, "No se pudo cargar el catálogo de dueños.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void guardarMascota() {
        tilNombre.setError(null);
        tilPeso.setError(null);
        tvErrorDueno.setVisibility(View.GONE);
        tvErrorTamano.setVisibility(View.GONE);

        boolean valido = true;
        int posicionDueno = spDueno.getSelectedItemPosition();
        Dueno duenoSeleccionado = posicionDueno > 0 && posicionDueno <= duenios.size()
                ? duenios.get(posicionDueno - 1) : null;
        if (duenoSeleccionado == null) {
            tvErrorDueno.setText(getString(R.string.error_dueno_requerido));
            tvErrorDueno.setVisibility(View.VISIBLE);
            valido = false;
        }

        String nombre = normalizarEspacios(texto(etNombre));
        if (nombre.isEmpty()) {
            tilNombre.setError(getString(R.string.error_mascota_requerida));
            valido = false;
        }

        int posicionTamano = spTamano.getSelectedItemPosition();
        String tamano = posicionTamano > 0 && posicionTamano < spTamano.getCount()
                ? spTamano.getSelectedItem().toString() : null;
        if (tamano == null) {
            tvErrorTamano.setText(getString(R.string.error_tamano_requerido));
            tvErrorTamano.setVisibility(View.VISIBLE);
            valido = false;
        }

        Double pesoKg = null;
        String pesoTexto = texto(etPeso).trim().replace(',', '.');
        if (!pesoTexto.isEmpty()) {
            try {
                pesoKg = Double.parseDouble(pesoTexto);
                if (Double.isNaN(pesoKg) || Double.isInfinite(pesoKg) || pesoKg <= 0) {
                    pesoKg = null;
                    tilPeso.setError(getString(R.string.error_peso_invalido));
                    valido = false;
                }
            } catch (NumberFormatException exception) {
                tilPeso.setError(getString(R.string.error_peso_invalido));
                valido = false;
            }
        }

        if (!valido) {
            return;
        }

        String especie = normalizarEspacios(texto(etEspecie));
        if (especie.isEmpty()) {
            especie = "Perro";
        }
        Mascota mascota = new Mascota(
                duenoSeleccionado.getIdDueno(),
                nombre,
                especie,
                normalizarOpcional(texto(etRaza)),
                pesoKg,
                tamano,
                normalizarOpcional(texto(etTemperamento)),
                rbVacunasSi.isChecked() ? 1 : 0,
                normalizarOpcional(texto(etNotasMedicas))
        );

        try {
            long idMascota = parqueDao.insertarMascota(mascota);
            Toast.makeText(this,
                    "Mascota registrada con ID " + idMascota + ". Ya puedes generar su QR.",
                    Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } catch (SQLiteException exception) {
            Toast.makeText(this, "No se pudo guardar la mascota. Revisa el dueño y los datos.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private String texto(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString();
    }

    private String normalizarEspacios(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private String normalizarOpcional(String value) {
        String normalizado = normalizarEspacios(value);
        return normalizado.isEmpty() ? null : normalizado;
    }
}
