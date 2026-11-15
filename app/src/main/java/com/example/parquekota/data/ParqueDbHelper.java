package com.example.parquekota.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class ParqueDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "parque_mascotas.db";
    private static final int DATABASE_VERSION = 1;
    private static volatile ParqueDbHelper instance;

    public static ParqueDbHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (ParqueDbHelper.class) {
                if (instance == null) {
                    instance = new ParqueDbHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private ParqueDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE duenos ("
                + "id_dueno INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL, "
                + "telefono TEXT NOT NULL, "
                + "email TEXT, "
                + "contacto_emergencia TEXT, "
                + "fecha_registro TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))"
                + ")");

        db.execSQL("CREATE TABLE areas ("
                + "id_area INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL UNIQUE, "
                + "capacidad_maxima INTEGER NOT NULL DEFAULT 20 CHECK(capacidad_maxima > 0), "
                + "reglas_restricciones TEXT, "
                + "activa INTEGER NOT NULL DEFAULT 1, "
                + "horario_apertura TEXT DEFAULT '07:00', "
                + "horario_cierre TEXT DEFAULT '19:00'"
                + ")");

        db.execSQL("CREATE TABLE mascotas ("
                + "id_mascota INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "id_dueno INTEGER NOT NULL, "
                + "nombre TEXT NOT NULL, "
                + "especie TEXT NOT NULL DEFAULT 'Perro', "
                + "raza TEXT, "
                + "peso_kg REAL CHECK(peso_kg IS NULL OR peso_kg > 0), "
                + "tamano TEXT CHECK(tamano IS NULL OR tamano IN ('Chico', 'Mediano', 'Grande', 'Gigante')), "
                + "temperamento TEXT, "
                + "vacunas_al_dia INTEGER NOT NULL DEFAULT 1 CHECK(vacunas_al_dia IN (0, 1)), "
                + "notas_medicas TEXT, "
                + "FOREIGN KEY(id_dueno) REFERENCES duenos(id_dueno) ON DELETE CASCADE"
                + ")");

        db.execSQL("CREATE TABLE visitas ("
                + "id_visita INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "id_mascota INTEGER NOT NULL, "
                + "id_area INTEGER NOT NULL, "
                + "fecha_entrada TEXT NOT NULL DEFAULT (datetime('now', 'localtime')), "
                + "fecha_salida TEXT, "
                + "observaciones_entrada TEXT, "
                + "observaciones_salida TEXT, "
                + "FOREIGN KEY(id_mascota) REFERENCES mascotas(id_mascota) ON DELETE RESTRICT, "
                + "FOREIGN KEY(id_area) REFERENCES areas(id_area) ON DELETE RESTRICT"
                + ")");

        db.execSQL("CREATE INDEX idx_visitas_mascota ON visitas(id_mascota)");
        db.execSQL("CREATE INDEX idx_visitas_area ON visitas(id_area)");
        db.execSQL("CREATE INDEX idx_visitas_fechas ON visitas(fecha_entrada, fecha_salida)");
        insertarAreaInicial(db, "Agilidad", 10, "Solo sociables con arnés", "07:00", "19:00");
        insertarAreaInicial(db, "Perros Pequeños", 15, "Razas menores a 10 kg", "08:00", "20:00");
        insertarAreaInicial(db, "Alberca Canina", 6, "Chaleco salvavidas obligatorio si aplica", "09:00", "18:00");
    }

    private void insertarAreaInicial(SQLiteDatabase db, String nombre, int capacidad,
                                     String reglas, String apertura, String cierre) {
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("capacidad_maxima", capacidad);
        values.put("reglas_restricciones", reglas);
        values.put("activa", 1);
        values.put("horario_apertura", apertura);
        values.put("horario_cierre", cierre);
        db.insertOrThrow("areas", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Las futuras versiones deben migrar las tablas sin eliminar registros existentes.
    }
}
