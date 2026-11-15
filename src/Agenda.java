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

    /**
     * Devuelve el número total de citas de la agenda.
     *
     * @return Número de citas.
     */
    public int getNumCitas() {
        return citas.size();
    }

    /**
     * Devuelve la cita situada en una posición cronológica.
     *
     * @param i Posición solicitada (desde 0).
     * @return Cita en esa posición.
     * @throws Exception Si la posición está fuera de rango.
     */
    public Cita getCitaEn(int i) throws Exception {
        if (i < 0 || i >= citas.size()) {
            throw new Exception("La posición no pertenece a la agenda.");
        }
        return citas.get(i);
    }
    /**
     * Comprueba si un cliente tiene cita en una fecha.
     *
     * @param cliente Identificador del cliente.
     * @param fecha Fecha consultada.
     * @return true si tiene cita ese día, false en caso contrario.
     * @throws Exception Si el cliente o la fecha son inválidos.
     */
    public boolean tieneCita(String cliente, LocalDate fecha) throws Exception {
        if (cliente == null || cliente.isBlank()) {
            throw new Exception("El cliente no puede ser nulo ni vacío.");
        }
        if (fecha == null) {
            throw new Exception("La fecha no puede ser nula.");
        }
        return buscarPosicion(cliente, fecha) >= 0;
    }

    private boolean cabeEnHorario(LocalTime inicio, int duracion) {
        int minInicio = aMinutos(inicio);
        int minFin = minInicio + duracion;
        return minInicio >= aMinutos(apertura) && minFin <= aMinutos(cierre);
    }


    private int buscarPosicion(String cliente, LocalDate fecha) {
        for (int i = 0; i < citas.size(); i++) {
            Cita cita = citas.get(i);
            if (cita.getFecha().equals(fecha)
                    && cita.getCliente().equalsIgnoreCase(cliente)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Reserva una cita ya creada manteniéndola en orden cronológico.
     *
     * @param c Cita que se desea reservar.
     * @throws Exception Si es nula, sale del horario, duplica cliente o solapa.
     */
    public void reservar(Cita c) throws Exception {
        if (c == null) {
            throw new Exception("La cita no puede ser nula.");
        }
        if (!cabeEnHorario(c.getInicio(), c.getDuracion())) {
            throw new Exception("La cita queda fuera del horario de la agenda.");
        }
        if (tieneCita(c.getCliente(), c.getFecha())) {
            throw new Exception("El cliente ya tiene una cita ese día.");
        }
        if (!estaLibre(c.getFecha(), c.getInicio(), c.getDuracion())) {
            throw new Exception("El intervalo solicitado no está libre.");
        }

        citas.add(posicionDeInsercion(c), c);
    }

    /**
     * Devuelve la cita de un cliente en una fecha.
     *
     * @param cliente Identificador del cliente.
     * @param fecha Fecha consultada.
     * @return Cita correspondiente.
     * @throws Exception Si los datos son inválidos o no existe la cita.
     */
    public Cita getCitaDe(String cliente, LocalDate fecha) throws Exception {
        if (cliente == null || cliente.isBlank()) {
            throw new Exception("El cliente no puede ser nulo ni vacío.");
        }
        if (fecha == null) {
            throw new Exception("La fecha no puede ser nula.");
        }
        int posicion = buscarPosicion(cliente, fecha);
        if (posicion < 0) {
            throw new Exception("El cliente no tiene cita en esa fecha.");
        }
        return citas.get(posicion);
    }
    public void cancelar(String cliente, LocalDate fecha) throws Exception {
        if (cliente == null || cliente.isBlank()) {
            throw new Exception("El cliente no puede ser nulo ni vacío.");
        }
        if (fecha == null) {
            throw new Exception("La fecha no puede ser nula.");
        }
        int posicion = buscarPosicion(cliente, fecha);
        if (posicion < 0) {
            throw new Exception(
                    "No existe una cita de ese cliente en esa fecha.");
        }

        citas.remove(posicion);
    }
    private int aMinutos(LocalTime hora) {
        String texto = hora.toString();
        int h = Integer.parseInt(texto.substring(0, 2));
        int m = Integer.parseInt(texto.substring(3, 5));
        return h * 60 + m;
    }

    /**
     * Indica si un intervalo está dentro del horario y no se solapa con otra cita.
     *
     * @param fecha Fecha consultada.
     * @param h Hora de inicio.
     * @param d Duración en minutos.
     * @return true si el intervalo está disponible.
     * @throws Exception Si algún argumento es nulo o la duración no es positiva.
     */
    public boolean estaLibre(LocalDate fecha, LocalTime h, int d)
            throws Exception {
        if (fecha == null) {
            throw new Exception("La fecha no puede ser nula.");
        }
        if (h == null) {
            throw new Exception("La hora de inicio no puede ser nula.");
        }
        if (d <= 0) {
            throw new Exception("La duración debe ser positiva.");
        }
        if (!cabeEnHorario(h, d)) {
            return false;
        }

        int ini = aMinutos(h);
        int fin = ini + d;
        for (Cita cita : citas) {
            if (cita.getFecha().equals(fecha)
                    && seSolapan(ini, fin,
                    aMinutos(cita.getInicio()), aMinutos(cita.getFin()))) {
                return false;
            }
        }
        return true;
    }
    private int posicionDeInsercion(Cita nueva) {
        int fechaNueva = numeroFecha(nueva.getFecha());
        int inicioNuevo = aMinutos(nueva.getInicio());

        int posicion = 0;
        while (posicion < citas.size()) {
            Cita actual = citas.get(posicion);
            int fechaActual = numeroFecha(actual.getFecha());
            if (fechaActual > fechaNueva) {
                break;
            }
            if (fechaActual == fechaNueva
                    && aMinutos(actual.getInicio()) > inicioNuevo) {
                break;
            }
            posicion++;
        }
        return posicion;
    }
    private boolean seSolapan(int inicio1, int fin1, int inicio2, int fin2) {
        return inicio1 < fin2 && inicio2 < fin1;
    }
}