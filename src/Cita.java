/*
 * Archivo Cita.java
 * Práctica 2. Gestión de una clínica veterinaria
 */

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase abstracta que representa una cita dentro de una agenda.
 * Sus observadoras son final para conservar el invariante en las subclases, y
 * cada subclase redefine conInicio para copiar su propio tipo.
 */
public abstract class Cita implements Serializable {

    private String cliente;
    private LocalDate fecha;
    private LocalTime inicio;
    private int duracion;

    /**
     * Construye una cita comprobando que todos sus datos sean válidos.
     *
     * @param cliente Identificador del cliente de la cita.
     * @param fecha Fecha de la cita.
     * @param inicio Hora de inicio de la cita.
     * @param duracion Duración en minutos.
     * @throws ArgumentoException Si algún dato es nulo, vacío o supera el día.
     */
    protected Cita(String cliente, LocalDate fecha, LocalTime inicio,
                   int duracion) throws ArgumentoException {
        if (cliente == null || cliente.isBlank()) {
            throw new ArgumentoException(
                    "El cliente no puede ser nulo ni vacío.");
        }
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        if (inicio == null) {
            throw new ArgumentoException(
                    "La hora de inicio no puede ser nula.");
        }
        if (duracion <= 0) {
            throw new ArgumentoException("La duración debe ser positiva.");
        }

        String textoHora = inicio.toString();
        int h = Integer.parseInt(textoHora.substring(0, 2));
        int m = Integer.parseInt(textoHora.substring(3, 5));
        if (h * 60 + m + duracion > 24 * 60) {
            throw new ArgumentoException("La cita supera el final del día.");
        }

        this.cliente = cliente;
        this.fecha = fecha;
        this.inicio = inicio;
        this.duracion = duracion;
    }

    /**
     * Devuelve el cliente de la cita.
     *
     * @return Identificador del cliente.
     */
    public final String getCliente() {
        return cliente;
    }

    /**
     * Devuelve la fecha de la cita.
     *
     * @return Fecha de la cita.
     */
    public final LocalDate getFecha() {
        return fecha;
    }

    /**
     * Devuelve la hora de inicio.
     *
     * @return Hora de inicio.
     */
    public final LocalTime getInicio() {
        return inicio;
    }

    /**
     * Devuelve la duración en minutos.
     *
     * @return Duración en minutos.
     */
    public final int getDuracion() {
        return duracion;
    }

    /**
     * Devuelve la hora de finalización sumando la duración al inicio.
     *
     * @return Hora de fin de la cita.
     */
    public final LocalTime getFin() {
        String textoHora = inicio.toString();
        int h = Integer.parseInt(textoHora.substring(0, 2));
        int m = Integer.parseInt(textoHora.substring(3, 5));
        int total = h * 60 + m + duracion;
        if (total == 24 * 60) {
            return LocalTime.of(0, 0);
        }
        return LocalTime.of(total / 60, total % 60);
    }

    /**
     * Crea una copia de esta cita del mismo subtipo que comienza a otra hora.
     *
     * @param nuevoInicio Nueva hora de inicio.
     * @return Nueva cita con la hora actualizada.
     * @throws ArgumentoException Si la nueva hora es nula o inválida.
     */
    public abstract Cita conInicio(LocalTime nuevoInicio)
            throws ArgumentoException;

    @Override
    public String toString() {
        return inicio + "-" + getFin() + " " + cliente
                + " (" + duracion + " min)";
    }
}
