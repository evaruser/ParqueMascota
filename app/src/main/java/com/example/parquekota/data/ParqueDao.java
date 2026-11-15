package com.example.parquekota.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.parquekota.models.Dueno;
import com.example.parquekota.models.Mascota;

import java.util.ArrayList;
import java.util.List;

public final class ParqueDao {
    private final ParqueDbHelper dbHelper;

    public ParqueDao(Context context) {
        dbHelper = ParqueDbHelper.getInstance(context);
    }

    public long insertarDueno(Dueno dueno) {
        ContentValues values = new ContentValues();
        values.put("nombre", dueno.getNombre());
        values.put("telefono", dueno.getTelefono());
        putNullable(values, "email", dueno.getEmail());
        putNullable(values, "contacto_emergencia", dueno.getContactoEmergencia());
        return dbHelper.getWritableDatabase().insertOrThrow("duenos", null, values);
    }

    public List<Dueno> obtenerDuenos() {
        List<Dueno> duenios = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {
                "id_dueno", "nombre", "telefono", "email", "contacto_emergencia"
        };
        try (Cursor cursor = db.query("duenos", columns, null, null, null, null,
                "nombre COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                duenios.add(new Dueno(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_dueno")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("contacto_emergencia"))
                ));
            }
        }
        return duenios;
    }

    public long insertarMascota(Mascota mascota) {
        ContentValues values = new ContentValues();
        values.put("id_dueno", mascota.getIdDueno());
        values.put("nombre", mascota.getNombre());
        values.put("especie", mascota.getEspecie());
        putNullable(values, "raza", mascota.getRaza());
        if (mascota.getPesoKg() == null) {
            values.putNull("peso_kg");
        } else {
            values.put("peso_kg", mascota.getPesoKg());
        }
        values.put("tamano", mascota.getTamano());
        putNullable(values, "temperamento", mascota.getTemperamento());
        values.put("vacunas_al_dia", mascota.getVacunasAlDia());
        putNullable(values, "notas_medicas", mascota.getNotasMedicas());
        return dbHelper.getWritableDatabase().insertOrThrow("mascotas", null, values);
    }

    private void putNullable(ContentValues values, String column, String value) {
        if (value == null || value.isEmpty()) {
            values.putNull(column);
        } else {
            values.put(column, value);
        }
    }
}
