package com.example.parquekota.models;

public final class Dueno {
    private final int idDueno;
    private final String nombre;
    private final String telefono;
    private final String email;
    private final String contactoEmergencia;

    public Dueno(int idDueno, String nombre, String telefono, String email,
                 String contactoEmergencia) {
        this.idDueno = idDueno;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.contactoEmergencia = contactoEmergencia;
    }

    public int getIdDueno() {
        return idDueno;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    @Override
    public String toString() {
        return nombre + " (" + telefono + ")";
    }
}
