/*
 * Archivo CitaVeterinaria.java
 * Práctica 2. Gestión de una clínica veterinaria
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Clase abstracta base para las citas de la clínica veterinaria.
 * En la agenda, el cliente de la cita es el identificador de la mascota.
 */
public abstract class CitaVeterinaria extends Cita {

    private static final LocalTime APERTURA_CLINICA = LocalTime.of(9, 0);
    private static final LocalTime CIERRE_CLINICA = LocalTime.of(20, 0);

    private Mascota mascota;
    private Veterinario veterinario;
    private Resultado resultado;
    private String notas;

    /**
     * Construye una cita veterinaria pendiente (con resultado a null).
     *
     * @param mascota Mascota citada.
     * @param veterinario Veterinario asignado.
     * @param fecha Fecha de la cita.
     * @param inicio Hora de inicio.
     * @param duracion Duración en minutos.
     * @throws ArgumentoException Si algún dato es nulo o inválido.
     */
    protected CitaVeterinaria(Mascota mascota, Veterinario veterinario,
                              LocalDate fecha, LocalTime inicio, int duracion)
            throws ArgumentoException {
        super(idDeMascota(mascota), fecha, inicio, duracion);
        if (veterinario == null) {
            throw new ArgumentoException("El veterinario no puede ser nulo.");
        }

        this.mascota = mascota;
        this.veterinario = veterinario;
        this.resultado = null;
        this.notas = "";
    }

    private static String idDeMascota(Mascota mascota) {
        if (mascota == null) {
            return null;
        }
        return mascota.getId();
    }

    /**
     * Valida un texto comprobando que no sea nulo y, si no se permite vacío,
     * que contenga caracteres distintos de espacios.
     *
     * @param s Texto que se valida.
     * @param que Nombre del dato para el mensaje de error.
     * @param vacioPermitido true si se admite cadena vacía o en blanco.
     * @return El texto validado.
     * @throws ArgumentoException Si el texto incumple la validación.
     */
    protected static String texto(String s, String que, boolean vacioPermitido)
            throws ArgumentoException {
        if (s == null) {
            throw new ArgumentoException(que + " no puede ser nulo.");
        }
        if (!vacioPermitido && s.isBlank()) {
            throw new ArgumentoException(que + " no puede estar vacío.");
        }
        return s;
    }

    /**
     * Devuelve la mascota asociada a la cita.
     *
     * @return Mascota de la cita.
     */
    public Mascota getMascota() {
        return mascota;
    }

    /**
     * Devuelve el veterinario asignado a la cita.
     *
     * @return Veterinario de la cita.
     */
    public Veterinario getVeterinario() {
        return veterinario;
    }

    /**
     * Devuelve el resultado de la cita, o null si sigue pendiente.
     *
     * @return Resultado de la cita o null.
     */
    public Resultado getResultado() {
        return resultado;
    }

    /**
     * Devuelve las notas registradas al terminar la cita.
     *
     * @return Notas de la cita.
     */
    public String getNotas() {
        return notas;
    }

    /**
     * Calcula el precio de la cita en céntimos.
     *
     * @return Precio en céntimos.
     */
    public abstract int precio();

    /**
     * Devuelve el nombre del tipo de cita («Consulta» o «Cirugía»).
     *
     * @return Nombre del tipo de cita.
     */
    public abstract String getTipo();

    /**
     * Devuelve la especialidad que exige esta cita.
     *
     * @return Especialidad requerida.
     */
    public abstract Especialidad especialidadRequerida();

    /**
     * Devuelve la descripción específica de la cita (motivo o intervención).
     *
     * @return Descripción de la cita.
     */
    public abstract String descripcion();

    /**
     * Método plantilla que comprueba las normas comunes en orden (domingo,
     * horario, pasado y especialidad) y finalmente las normas propias del subtipo.
     *
     * @param ahora Fecha y hora actuales de referencia.
     * @throws ArgumentoException Si ahora es nulo.
     * @throws ClinicaException Si incumple alguna norma de programación.
     */
    public final void comprobarNormas(LocalDateTime ahora)
            throws ArgumentoException, ClinicaException {
        if (ahora == null) {
            throw new ArgumentoException(
                    "La fecha y hora actuales no pueden ser nulas.");
        }
        if (esDomingo(getFecha())) {
            throw new ClinicaException("La clínica cierra los domingos.");
        }

        int minInicio = minutosHora(getInicio());
        int minFin = minInicio + getDuracion();
        int minApertura = minutosHora(APERTURA_CLINICA);
        int minCierre = minutosHora(CIERRE_CLINICA);
        if (minInicio < minApertura || minFin > minCierre) {
            throw new ClinicaException(
                    "La cita debe empezar y terminar entre las 9:00 y las 20:00.");
        }

        int diaCita = Integer.parseInt(String.format("%1$td", getFecha()));
        int mesCita = Integer.parseInt(String.format("%1$tm", getFecha()));
        int annoCita = Integer.parseInt(String.format("%1$tY", getFecha()));
        int fechaCita = annoCita * 10000 + mesCita * 100 + diaCita;

        String textoAhora = ahora.toString();
        int annoAhora = Integer.parseInt(textoAhora.substring(0, 4));
        int mesAhora = Integer.parseInt(textoAhora.substring(5, 7));
        int diaAhora = Integer.parseInt(textoAhora.substring(8, 10));
        int hAhora = Integer.parseInt(textoAhora.substring(11, 13));
        int mAhora = Integer.parseInt(textoAhora.substring(14, 16));
        int fechaAhora = annoAhora * 10000 + mesAhora * 100 + diaAhora;
        int minAhora = hAhora * 60 + mAhora;

        if (fechaCita < fechaAhora
                || (fechaCita == fechaAhora && minInicio < minAhora)) {
            throw new ClinicaException(
                    "No se puede programar una cita en el pasado.");
        }

        if (!veterinario.puedeAtender(especialidadRequerida())) {
            throw new ClinicaException(
                    "El veterinario no tiene la especialidad exigida.");
        }

        comprobarNormasPropias(ahora);
    }

    private boolean esDomingo(LocalDate fecha) {
        int dia = Integer.parseInt(String.format("%1$td", fecha));
        int mes = Integer.parseInt(String.format("%1$tm", fecha));
        int anno = Integer.parseInt(String.format("%1$tY", fecha));

        int dias = 0;
        for (int a = 1900; a < anno; a++) {
            if ((a % 4 == 0 && a % 100 != 0) || (a % 400 == 0)) {
                dias += 366;
            } else {
                dias += 365;
            }
        }
        for (int m = 1; m < mes; m++) {
            dias += diasDelMes(m, anno);
        }
        dias += dia - 1;
        return dias % 7 == 6;
    }

    private int diasDelMes(int mes, int anno) {
        if (mes == 2) {
            if ((anno % 4 == 0 && anno % 100 != 0) || (anno % 400 == 0)) {
                return 29;
            }
            return 28;
        }
        if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
            return 30;
        }
        return 31;
    }

    private int minutosHora(LocalTime hora) {
        String texto = hora.toString();
        int h = Integer.parseInt(texto.substring(0, 2));
        int m = Integer.parseInt(texto.substring(3, 5));
        return h * 60 + m;
    }

    /**
     * Comprueba las normas adicionales propias de cada subclase.
     * Por defecto no hace nada.
     *
     * @param ahora Fecha y hora actuales de referencia.
     * @throws ClinicaException Si incumple alguna norma propia.
     */
    protected void comprobarNormasPropias(LocalDateTime ahora)
            throws ClinicaException {
    }

    /**
     * Finaliza una cita pendiente registrando su resultado y sus notas.
     *
     * @param resultado Desenlace de la cita.
     * @param notas Notas asociadas (pueden estar vacías).
     * @throws ArgumentoException Si resultado o notas son nulos o ya había terminado.
     */
    public void terminar(Resultado resultado, String notas)
            throws ArgumentoException {
        if (resultado == null) {
            throw new ArgumentoException("El resultado no puede ser nulo.");
        }
        if (this.resultado != null) {
            throw new ArgumentoException("La cita ya estaba terminada.");
        }

        String notasValidadas = texto(notas, "Las notas", true);
        this.resultado = resultado;
        this.notas = notasValidadas;
    }

    /**
     * Devuelve el ingreso generado por la cita en céntimos: su precio si fue
     * atendida y 0 en cualquier otro caso.
     *
     * @return Importe cobrado en céntimos.
     */
    public int ingreso() {
        if (resultado == Resultado.ATENDIDA) {
            return precio();
        }
        return 0;
    }

    /**
     * Construye una cadena con los datos comunes de la cita veterinaria.
     *
     * @return Texto con fecha, tramo horario, tipo, mascota, veterinario y estado.
     */
    protected String camposComunes() {
        int centimos = precio();
        String textoPrecio = String.format("%d,%02d €",
                centimos / 100, centimos % 100);
        String base = String.format("%1$td/%1$tm/%1$tY", getFecha())
                + " " + getInicio() + "-" + getFin()
                + " [" + getTipo() + "] "
                + mascota.getId() + " " + mascota.getNombre()
                + " (" + veterinario.getCodigo() + " " + veterinario.getNombre()
                + ") - " + textoPrecio;
        if (resultado != null) {
            base += " [" + resultado + "]";
            if (!notas.isBlank()) {
                base += " Notas: " + notas;
            }
        }
        return base;
    }

    @Override
    public String toString() {
        return camposComunes() + " - " + descripcion();
    }
}
