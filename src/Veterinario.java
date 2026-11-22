import java.io.Serializable;
import java.time.LocalTime;

/**
 * Representa un veterinario de la clínica con su especialidad y su agenda.
 */
public class Veterinario implements Serializable {

    private static final LocalTime APERTURA_CLINICA = LocalTime.of(9, 0);
    private static final LocalTime CIERRE_CLINICA = LocalTime.of(20, 0);

    private String codigo;
    private String nombre;
    private Especialidad especialidad;
    private Agenda agenda;

    /**
     * Crea un veterinario con su propia agenda abierta en el horario de la clínica.
     *
     * @param codigo Código identificativo (por ejemplo, V1).
     * @param nombre Nombre completo del veterinario.
     * @param especialidad Especialidad del veterinario.
     * @throws ArgumentoException Si algún argumento es nulo o vacío.
     */
    public Veterinario(String codigo, String nombre, Especialidad especialidad)
            throws ArgumentoException {
        if (codigo == null || codigo.isBlank()) {
            throw new ArgumentoException(
                    "El código del veterinario no puede ser nulo ni vacío.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ArgumentoException(
                    "El nombre del veterinario no puede ser nulo ni vacío.");
        }
        if (especialidad == null) {
            throw new ArgumentoException("La especialidad no puede ser nula.");
        }

        this.codigo = codigo;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.agenda = new Agenda(APERTURA_CLINICA, CIERRE_CLINICA);
    }

    /**
     * Devuelve el código del veterinario.
     *
     * @return Código del veterinario.
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Devuelve el nombre del veterinario.
     *
     * @return Nombre del veterinario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la especialidad del veterinario.
     *
     * @return Especialidad del veterinario.
     */
    public Especialidad getEspecialidad() {
        return especialidad;
    }

    /**
     * Devuelve la agenda personal del veterinario.
     *
     * @return Agenda del veterinario.
     */
    public Agenda getAgenda() {
        return agenda;
    }

    /**
     * Indica si el veterinario puede atender una cita que exige cierta especialidad.
     * Todos los veterinarios pueden atender la especialidad GENERAL, además de la suya.
     *
     * @param requerida Especialidad exigida por la cita.
     * @return true si puede atenderla, false en caso contrario.
     * @throws ArgumentoException Si la especialidad requerida es nula.
     */
    public boolean puedeAtender(Especialidad requerida)
            throws ArgumentoException {
        if (requerida == null) {
            throw new ArgumentoException(
                    "La especialidad requerida no puede ser nula.");
        }
        return requerida == Especialidad.GENERAL || especialidad == requerida;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + especialidad + ")";
    }
}
