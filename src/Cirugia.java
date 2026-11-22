/*
 * Archivo Cirugia.java
 * Práctica 2. Gestión de una clínica veterinaria
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Cita de cirugía veterinaria. Exige especialidad de cirugía, reserva al menos
 * el día anterior, horario de mañana y duración en tramos de 30 minutos.
 */
public class Cirugia extends CitaVeterinaria {

    public static final int PRECIO_HORA = 9000;
    public static final int ANESTESIA_POR_KG = 150;
    public static final LocalTime FIN_MANANA = LocalTime.of(14, 0);
    public static final int DURACION_MINIMA = 30;
    public static final int DURACION_MAXIMA = 240;

    private String intervencion;

    /**
     * Crea una cita de cirugía.
     *
     * @param mascota Mascota intervenida.
     * @param veterinario Veterinario asignado.
     * @param fecha Fecha de la cirugía.
     * @param inicio Hora de inicio.
     * @param duracion Duración en minutos.
     * @param intervencion Descripción de la intervención.
     * @throws ArgumentoException Si algún argumento es nulo, vacío o no positivo.
     */
    public Cirugia(Mascota mascota, Veterinario veterinario, LocalDate fecha,
                   LocalTime inicio, int duracion, String intervencion)
            throws ArgumentoException {
        super(mascota, veterinario, fecha, inicio, duracion);
        this.intervencion = texto(intervencion, "La intervención", false);
    }

    /**
     * Devuelve la descripción de la intervención quirúrgica.
     *
     * @return Intervención quirúrgica.
     */
    public String getIntervencion() {
        return intervencion;
    }

    @Override
    public int precio() {
        return PRECIO_HORA * getDuracion() / 60
                + ANESTESIA_POR_KG * getMascota().getPeso() / 1000;
    }

    @Override
    public String getTipo() {
        return "Cirugía";
    }

    @Override
    public Especialidad especialidadRequerida() {
        return Especialidad.CIRUGIA;
    }

    @Override
    public String descripcion() {
        return intervencion;
    }

    @Override
    protected void comprobarNormasPropias(LocalDateTime ahora)
            throws ClinicaException {
        int diaCita = Integer.parseInt(String.format("%1$td", getFecha()));
        int mesCita = Integer.parseInt(String.format("%1$tm", getFecha()));
        int annoCita = Integer.parseInt(String.format("%1$tY", getFecha()));
        int fechaCita = annoCita * 10000 + mesCita * 100 + diaCita;

        String textoAhora = ahora.toString();
        int annoAhora = Integer.parseInt(textoAhora.substring(0, 4));
        int mesAhora = Integer.parseInt(textoAhora.substring(5, 7));
        int diaAhora = Integer.parseInt(textoAhora.substring(8, 10));
        int fechaAhora = annoAhora * 10000 + mesAhora * 100 + diaAhora;

        if (fechaCita <= fechaAhora) {
            throw new ClinicaException(
                    "La cirugía debe programarse al menos el día anterior.");
        }

        String textoIni = getInicio().toString();
        int hIni = Integer.parseInt(textoIni.substring(0, 2));
        int mIni = Integer.parseInt(textoIni.substring(3, 5));
        int minFin = hIni * 60 + mIni + getDuracion();

        String textoManana = FIN_MANANA.toString();
        int hMax = Integer.parseInt(textoManana.substring(0, 2));
        int mMax = Integer.parseInt(textoManana.substring(3, 5));
        int maxManana = hMax * 60 + mMax;

        if (minFin > maxManana) {
            throw new ClinicaException(
                    "La cirugía debe terminar como muy tarde a las 14:00.");
        }

        int dur = getDuracion();
        if (dur < DURACION_MINIMA || dur > DURACION_MAXIMA || dur % 30 != 0) {
            throw new ClinicaException(
                    "La cirugía debe durar de 30 a 240 minutos en tramos de 30.");
        }
    }

    @Override
    public Cirugia conInicio(LocalTime nuevoInicio) throws ArgumentoException {
        return new Cirugia(getMascota(), getVeterinario(), getFecha(),
                nuevoInicio, getDuracion(), intervencion);
    }
}