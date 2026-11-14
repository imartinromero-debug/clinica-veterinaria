import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Cita implements Serializable {

    private String cliente;
    private LocalDate fecha;
    private LocalTime inicio;
    private int duracion;

    public Cita(String cliente, LocalDate fecha, LocalTime inicio, int duracion)
            throws Exception {
        if (cliente == null || cliente.isBlank()) {
            throw new Exception("El cliente no puede ser nulo ni vacío.");
        }
        if (fecha == null) {
            throw new Exception("La fecha no puede ser nula.");
        }
        if (inicio == null) {
            throw new Exception("La hora de inicio no puede ser nula.");
        }
        if (duracion <= 0) {
            throw new Exception("La duración debe ser positiva.");
        }

        String textoHora = inicio.toString();
        int h = Integer.parseInt(textoHora.substring(0, 2));
        int m = Integer.parseInt(textoHora.substring(3, 5));
        if (h * 60 + m + duracion > 24 * 60) {
            throw new Exception("La cita supera el final del día.");
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
    public String getCliente() {
        return cliente;
    }

    /**
     * Devuelve la fecha de la cita.
     *
     * @return Fecha de la cita.
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Devuelve la hora de inicio.
     *
     * @return Hora de inicio.
     */
    public LocalTime getInicio() {
        return inicio;
    }

    /**
     * Devuelve la duración en minutos.
     *
     * @return Duración en minutos.
     */
    public int getDuracion() {
        return duracion;
    }

    /**
     * Devuelve la hora de finalización sumando la duración al inicio.
     *
     * @return Hora de fin de la cita.
     */
    public LocalTime getFin() {
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
     * Crea una copia de esta cita que comienza a otra hora.
     *
     * @param nuevoInicio Nueva hora de inicio.
     * @return Nueva cita con la hora actualizada.
     * @throws Exception Si la nueva hora es nula o inválida.
     */
    public Cita conInicio(LocalTime nuevoInicio) throws Exception {
        return new Cita(cliente, fecha, nuevoInicio, duracion);
    }

    @Override
    public String toString() {
        return inicio + "-" + getFin() + " " + cliente
                + " (" + duracion + " min)";
    }
}
