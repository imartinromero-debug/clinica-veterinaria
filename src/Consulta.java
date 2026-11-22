/*
 * Archivo Consulta.java
 * Práctica 2. Gestión de una clínica veterinaria
 */

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Cita de consulta veterinaria. Su duración y especialidad dependen de la
 * especie de la mascota.
 */
public class Consulta extends CitaVeterinaria {

    public static final int PRECIO = 3500;

    private String motivo;

    /**
     * Crea una cita de consulta.
     *
     * @param mascota Mascota citada.
     * @param veterinario Veterinario asignado.
     * @param fecha Fecha de la consulta.
     * @param inicio Hora de inicio.
     * @param motivo Motivo de la consulta.
     * @throws ArgumentoException Si algún argumento es nulo o vacío.
     */
    public Consulta(Mascota mascota, Veterinario veterinario,
                    LocalDate fecha, LocalTime inicio, String motivo)
            throws ArgumentoException {
        super(mascota, veterinario, fecha, inicio,
                duracionDeMascota(mascota));
        this.motivo = texto(motivo, "El motivo", false);
    }

    private static int duracionDeMascota(Mascota mascota) {
        if (mascota == null) {
            return 1;
        }
        return mascota.getEspecie().getDuracionConsulta();
    }

    /**
     * Devuelve el motivo de la consulta.
     *
     * @return Motivo de la consulta.
     */
    public String getMotivo() {
        return motivo;
    }

    @Override
    public int precio() {
        return PRECIO;
    }

    @Override
    public String getTipo() {
        return "Consulta";
    }

    @Override
    public Especialidad especialidadRequerida() {
        return getMascota().getEspecie().getEspecialidad();
    }

    @Override
    public String descripcion() {
        return motivo;
    }

    @Override
    public Consulta conInicio(LocalTime nuevoInicio) throws ArgumentoException {
        return new Consulta(getMascota(), getVeterinario(), getFecha(),
                nuevoInicio, motivo);
    }
}