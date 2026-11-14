/*
 * Archivo Agenda.java
 * Práctica 2. Gestión de una clínica veterinaria
 */

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * TAD Agenda adaptado: almacena citas ordenadas cronológicamente, sin solapes
 * y con un máximo de una cita diaria por cliente. Abre todos los días dentro
 * del horario indicado al construirla.
 */
public class Agenda implements Serializable {

    private LocalTime apertura;
    private LocalTime cierre;
    private ArrayList<Cita> citas;

    /**
     * Crea una agenda vacía con el horario indicado.
     *
     * @param apertura Hora de apertura.
     * @param cierre Hora de cierre.
     * @throws Exception Si alguna hora es nula o el cierre no es posterior.
     */
    public Agenda(LocalTime apertura, LocalTime cierre) throws Exception {
        if (apertura == null || cierre == null) {
            throw new Exception("La apertura y el cierre no pueden ser nulos.");
        }
        if (aMinutos(cierre) <= aMinutos(apertura)) {
            throw new Exception("El cierre debe ser posterior a la apertura.");
        }

        this.apertura = apertura;
        this.cierre = cierre;
        this.citas = new ArrayList<>();
    }

    /**
     * Devuelve la hora de apertura.
     *
     * @return Hora de apertura.
     */
    public LocalTime getApertura() {
        return apertura;
    }

    /**
     * Devuelve la hora de cierre.
     *
     * @return Hora de cierre.
     */
    public LocalTime getCierre() {
        return cierre;
    }

    /**
     * Devuelve la duración de la jornada en minutos.
     *
     * @return Minutos entre la apertura y el cierre.
     */
    public int getJornada() {
        return aMinutos(cierre) - aMinutos(apertura);
    }
    private int aMinutos(LocalTime hora) {
        String texto = hora.toString();
        int h = Integer.parseInt(texto.substring(0, 2));
        int m = Integer.parseInt(texto.substring(3, 5));
        return h * 60 + m;
    }
}