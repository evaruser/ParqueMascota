package com.example.parquekota.models;

public final class Mascota {
    private final int idDueno;
    private final String nombre;
    private final String especie;
    private final String raza;
    private final Double pesoKg;
    private final String tamano;
    private final String temperamento;
    private final int vacunasAlDia;
    private final String notasMedicas;

    public Mascota(int idDueno, String nombre, String especie, String raza, Double pesoKg,
                   String tamano, String temperamento, int vacunasAlDia,
                   String notasMedicas) {
        this.idDueno = idDueno;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.pesoKg = pesoKg;
        this.tamano = tamano;
        this.temperamento = temperamento;
        this.vacunasAlDia = vacunasAlDia;
        this.notasMedicas = notasMedicas;
    }

    public int getIdDueno() {
        return idDueno;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public String getRaza() {
        return raza;
    }

    public Double getPesoKg() {
        return pesoKg;
    }

    public String getTamano() {
        return tamano;
    }

    public String getTemperamento() {
        return temperamento;
    }

    public int getVacunasAlDia() {
        return vacunasAlDia;
    }

    public String getNotasMedicas() {
        return notasMedicas;
    }
}
