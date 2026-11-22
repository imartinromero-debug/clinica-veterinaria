import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa una mascota registrada en la clínica veterinaria.
 * Sus datos no cambian una vez dada de alta.
 */
public class Mascota implements Serializable {

    private String id;
    private String nombre;
    private String propietario;
    private Especie especie;
    private LocalDate nacimiento;
    private int peso;

    /**
     * Crea una mascota con todos sus datos.
     *
     * @param id Identificador único (por ejemplo, M001).
     * @param nombre Nombre de la mascota.
     * @param propietario Nombre del propietario.
     * @param especie Especie de la mascota.
     * @param nacimiento Fecha de nacimiento.
     * @param peso Peso en gramos.
     * @throws ArgumentoException Si algún argumento es nulo, vacío o el peso no es positivo.
     */
    public Mascota(String id, String nombre, String propietario,
                   Especie especie, LocalDate nacimiento, int peso)
            throws ArgumentoException {
        if (id == null || id.isBlank()) {
            throw new ArgumentoException(
                    "El identificador de la mascota no puede ser nulo ni vacío.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ArgumentoException(
                    "El nombre de la mascota no puede ser nulo ni vacío.");
        }
        if (propietario == null || propietario.isBlank()) {
            throw new ArgumentoException(
                    "El propietario no puede ser nulo ni vacío.");
        }
        if (especie == null) {
            throw new ArgumentoException("La especie no puede ser nula.");
        }
        if (nacimiento == null) {
            throw new ArgumentoException(
                    "La fecha de nacimiento no puede ser nula.");
        }
        if (peso <= 0) {
            throw new ArgumentoException("El peso debe ser mayor que cero.");
        }

        this.id = id;
        this.nombre = nombre;
        this.propietario = propietario;
        this.especie = especie;
        this.nacimiento = nacimiento;
        this.peso = peso;
    }

    /**
     * Devuelve el identificador de la mascota.
     *
     * @return Identificador de la mascota.
     */
    public String getId() {
        return id;
    }

    /**
     * Devuelve el nombre de la mascota.
     *
     * @return Nombre de la mascota.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el nombre del propietario.
     *
     * @return Nombre del propietario.
     */
    public String getPropietario() {
        return propietario;
    }

    /**
     * Devuelve la especie de la mascota.
     *
     * @return Especie de la mascota.
     */
    public Especie getEspecie() {
        return especie;
    }

    /**
     * Devuelve la fecha de nacimiento.
     *
     * @return Fecha de nacimiento.
     */
    public LocalDate getNacimiento() {
        return nacimiento;
    }

    /**
     * Devuelve el peso en gramos.
     *
     * @return Peso en gramos.
     */
    public int getPeso() {
        return peso;
    }

    /**
     * Calcula la edad en años cumplidos en la fecha indicada.
     *
     * @param hoy Fecha de referencia.
     * @return Edad en años completos.
     * @throws ArgumentoException Si la fecha es nula o anterior al nacimiento.
     */
    public int getEdad(LocalDate hoy) throws ArgumentoException {
        if (hoy == null) {
            throw new ArgumentoException("La fecha actual no puede ser nula.");
        }

        int diaHoy = Integer.parseInt(String.format("%1$td", hoy));
        int mesHoy = Integer.parseInt(String.format("%1$tm", hoy));
        int annoHoy = Integer.parseInt(String.format("%1$tY", hoy));

        int diaNac = Integer.parseInt(String.format("%1$td", nacimiento));
        int mesNac = Integer.parseInt(String.format("%1$tm", nacimiento));
        int annoNac = Integer.parseInt(String.format("%1$tY", nacimiento));

        int numHoy = annoHoy * 10000 + mesHoy * 100 + diaHoy;
        int numNac = annoNac * 10000 + mesNac * 100 + diaNac;
        if (numHoy < numNac) {
            throw new ArgumentoException(
                    "La fecha actual es anterior a la fecha de nacimiento.");
        }

        int edad = annoHoy - annoNac;
        if (mesHoy < mesNac || (mesHoy == mesNac && diaHoy < diaNac)) {
            edad--;
        }
        return edad;
    }

    @Override
    public String toString() {
        return id + " " + nombre + " (" + especie + ") - " + propietario;
    }
}