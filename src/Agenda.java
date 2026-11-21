

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
     * @throws ArgumentoException Si alguna hora es nula o el cierre no es posterior.
     */
    public Agenda(LocalTime apertura, LocalTime cierre)
            throws ArgumentoException {
        if (apertura == null || cierre == null) {
            throw new ArgumentoException(
                    "La apertura y el cierre no pueden ser nulos.");
        }
        if (aMinutos(cierre) <= aMinutos(apertura)) {
            throw new ArgumentoException(
                    "El cierre debe ser posterior a la apertura.");
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
     * Reserva una cita ya creada manteniéndola en orden cronológico.
     *
     * @param c Cita que se desea reservar.
     * @throws ArgumentoException Si es nula, sale del horario, duplica cliente o solapa.
     */
    public void reservar(Cita c) throws ArgumentoException {
        if (c == null) {
            throw new ArgumentoException("La cita no puede ser nula.");
        }
        if (!cabeEnHorario(c.getInicio(), c.getDuracion())) {
            throw new ArgumentoException(
                    "La cita queda fuera del horario de la agenda.");
        }
        if (tieneCita(c.getCliente(), c.getFecha())) {
            throw new ArgumentoException(
                    "El cliente ya tiene una cita ese día.");
        }
        if (!estaLibre(c.getFecha(), c.getInicio(), c.getDuracion())) {
            throw new ArgumentoException(
                    "El intervalo solicitado no está libre.");
        }

        citas.add(posicionDeInsercion(c), c);
    }

    /**
     * Cancela la cita de un cliente en una fecha concreta.
     *
     * @param cliente Identificador del cliente.
     * @param fecha Fecha de la cita.
     * @throws ArgumentoException Si algún dato es inválido o no existe la cita.
     */
    public void cancelar(String cliente, LocalDate fecha)
            throws ArgumentoException {
        if (cliente == null || cliente.isBlank()) {
            throw new ArgumentoException(
                    "El cliente no puede ser nulo ni vacío.");
        }
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        int posicion = buscarPosicion(cliente, fecha);
        if (posicion < 0) {
            throw new ArgumentoException(
                    "No existe una cita de ese cliente en esa fecha.");
        }

        citas.remove(posicion);
    }

    /**
     * Retrasa una cita el número de minutos indicado conservando su tipo.
     *
     * @param cliente Identificador del cliente.
     * @param fecha Fecha de la cita.
     * @param minutos Minutos de retraso.
     * @throws ArgumentoException Si los argumentos son inválidos, no existe o solapa.
     */
    public void retrasar(String cliente, LocalDate fecha, int minutos)
            throws ArgumentoException {
        if (cliente == null || cliente.isBlank()) {
            throw new ArgumentoException(
                    "El cliente no puede ser nulo ni vacío.");
        }
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        if (minutos <= 0) {
            throw new ArgumentoException("El retraso debe ser positivo.");
        }
        int posicion = buscarPosicion(cliente, fecha);
        if (posicion < 0) {
            throw new ArgumentoException(
                    "No existe una cita de ese cliente en esa fecha.");
        }

        Cita anterior = citas.get(posicion);
        int nuevoInicioMin = aMinutos(anterior.getInicio()) + minutos;
        int nuevoFinMin = nuevoInicioMin + anterior.getDuracion();
        if (nuevoFinMin > aMinutos(cierre)) {
            throw new ArgumentoException(
                    "La cita retrasada terminaría después del cierre.");
        }

        Cita retrasada = anterior.conInicio(deMinutos(nuevoInicioMin));
        if (!libreExcepto(retrasada, posicion)) {
            throw new ArgumentoException(
                    "La cita retrasada se solaparía con otra cita.");
        }

        citas.remove(posicion);
        citas.add(posicionDeInsercion(retrasada), retrasada);
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
     * @throws ArgumentoException Si la posición está fuera de rango.
     */
    public Cita getCitaEn(int i) throws ArgumentoException {
        if (i < 0 || i >= citas.size()) {
            throw new ArgumentoException(
                    "La posición no pertenece a la agenda.");
        }
        return citas.get(i);
    }

    /**
     * Devuelve una lista nueva con las citas de una fecha concreta.
     *
     * @param fecha Fecha consultada.
     * @return Lista con las citas de ese día en orden.
     * @throws ArgumentoException Si la fecha es nula.
     */
    public ArrayList<Cita> getCitasDel(LocalDate fecha)
            throws ArgumentoException {
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }

        ArrayList<Cita> resultado = new ArrayList<>();
        for (Cita cita : citas) {
            if (cita.getFecha().equals(fecha)) {
                resultado.add(cita);
            }
        }
        return resultado;
    }

    /**
     * Devuelve una lista nueva con las citas entre dos fechas, ambas incluidas.
     *
     * @param desde Fecha inicial.
     * @param hasta Fecha final.
     * @return Lista ordenada con las citas del periodo.
     * @throws ArgumentoException Si alguna fecha es nula o desde es posterior a hasta.
     */
    public ArrayList<Cita> getCitasEntre(LocalDate desde, LocalDate hasta)
            throws ArgumentoException {
        if (desde == null || hasta == null) {
            throw new ArgumentoException("Las fechas no pueden ser nulas.");
        }
        int numDesde = numeroFecha(desde);
        int numHasta = numeroFecha(hasta);
        if (numDesde > numHasta) {
            throw new ArgumentoException(
                    "La fecha inicial no puede ser posterior a la final.");
        }

        ArrayList<Cita> resultado = new ArrayList<>();
        for (Cita cita : citas) {
            int numCita = numeroFecha(cita.getFecha());
            if (numCita >= numDesde && numCita <= numHasta) {
                resultado.add(cita);
            }
        }
        return resultado;
    }

    /**
     * Comprueba si un cliente tiene cita en una fecha.
     *
     * @param cliente Identificador del cliente.
     * @param fecha Fecha consultada.
     * @return true si tiene cita ese día, false en caso contrario.
     * @throws ArgumentoException Si el cliente o la fecha son inválidos.
     */
    public boolean tieneCita(String cliente, LocalDate fecha)
            throws ArgumentoException {
        if (cliente == null || cliente.isBlank()) {
            throw new ArgumentoException(
                    "El cliente no puede ser nulo ni vacío.");
        }
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        return buscarPosicion(cliente, fecha) >= 0;
    }

    /**
     * Devuelve la cita de un cliente en una fecha.
     *
     * @param cliente Identificador del cliente.
     * @param fecha Fecha consultada.
     * @return Cita correspondiente.
     * @throws ArgumentoException Si los datos son inválidos o no existe la cita.
     */
    public Cita getCitaDe(String cliente, LocalDate fecha)
            throws ArgumentoException {
        if (cliente == null || cliente.isBlank()) {
            throw new ArgumentoException(
                    "El cliente no puede ser nulo ni vacío.");
        }
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        int posicion = buscarPosicion(cliente, fecha);
        if (posicion < 0) {
            throw new ArgumentoException(
                    "El cliente no tiene cita en esa fecha.");
        }
        return citas.get(posicion);
    }

    /**
     * Indica si un intervalo está dentro del horario y no se solapa con otra cita.
     *
     * @param fecha Fecha consultada.
     * @param h Hora de inicio.
     * @param d Duración en minutos.
     * @return true si el intervalo está disponible.
     * @throws ArgumentoException Si algún argumento es nulo o la duración no es positiva.
     */
    public boolean estaLibre(LocalDate fecha, LocalTime h, int d)
            throws ArgumentoException {
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        if (h == null) {
            throw new ArgumentoException(
                    "La hora de inicio no puede ser nula.");
        }
        if (d <= 0) {
            throw new ArgumentoException("La duración debe ser positiva.");
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

    /**
     * Indica si en una fecha existe algún hueco de al menos la duración dada.
     *
     * @param fecha Fecha consultada.
     * @param d Duración en minutos.
     * @return true si hay hueco suficiente.
     * @throws ArgumentoException Si la fecha es nula o la duración no es positiva.
     */
    public boolean hayHueco(LocalDate fecha, int d) throws ArgumentoException {
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        if (d <= 0) {
            throw new ArgumentoException("La duración debe ser positiva.");
        }
        return buscarPrimerHueco(fecha, d) != null;
    }

    /**
     * Devuelve la primera hora disponible de un día para una duración.
     *
     * @param fecha Fecha consultada.
     * @param d Duración en minutos.
     * @return Hora de inicio del primer hueco.
     * @throws ArgumentoException Si los datos son inválidos o no hay hueco.
     */
    public LocalTime primerHueco(LocalDate fecha, int d)
            throws ArgumentoException {
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        if (d <= 0) {
            throw new ArgumentoException("La duración debe ser positiva.");
        }
        LocalTime hueco = buscarPrimerHueco(fecha, d);
        if (hueco == null) {
            throw new ArgumentoException(
                    "No existe un hueco de esa duración.");
        }
        return hueco;
    }

    /**
     * Busca la primera fecha, a partir de la indicada inclusive, con hueco suficiente.
     *
     * @param desde Fecha inicial de búsqueda.
     * @param d Duración en minutos.
     * @return Primera fecha con hueco.
     * @throws ArgumentoException Si desde es nula o la duración no cabe en una jornada.
     */
    public LocalDate primerDiaConHueco(LocalDate desde, int d)
            throws ArgumentoException {
        if (desde == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        if (d <= 0) {
            throw new ArgumentoException("La duración debe ser positiva.");
        }
        if (d > getJornada()) {
            throw new ArgumentoException(
                    "La duración no puede superar la jornada.");
        }

        LocalDate fecha = desde;
        while (buscarPrimerHueco(fecha, d) == null) {
            fecha = siguienteDia(fecha);
        }
        return fecha;
    }

    /**
     * Devuelve los minutos libres que quedan en una fecha.
     *
     * @param fecha Fecha consultada.
     * @return Minutos libres en la jornada de ese día.
     * @throws ArgumentoException Si la fecha es nula.
     */
    public int getTiempoLibre(LocalDate fecha) throws ArgumentoException {
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }

        int ocupado = 0;
        for (Cita cita : citas) {
            if (cita.getFecha().equals(fecha)) {
                ocupado += cita.getDuracion();
            }
        }
        return getJornada() - ocupado;
    }

    @Override
    public String toString() {
        if (citas.size() == 0) {
            return "Agenda sin citas.";
        }

        String texto = "";
        LocalDate anterior = null;
        for (Cita cita : citas) {
            if (anterior == null || !cita.getFecha().equals(anterior)) {
                if (anterior != null) {
                    texto += "\n";
                }
                texto += String.format("%1$td/%1$tm/%1$tY", cita.getFecha())
                        + "\n";
                anterior = cita.getFecha();
            }
            texto += "  " + cita + "\n";
        }
        return texto;
    }

    private int aMinutos(LocalTime hora) {
        String texto = hora.toString();
        int h = Integer.parseInt(texto.substring(0, 2));
        int m = Integer.parseInt(texto.substring(3, 5));
        return h * 60 + m;
    }

    private LocalTime deMinutos(int totalMinutos) {
        return LocalTime.of(totalMinutos / 60, totalMinutos % 60);
    }

    private int numeroFecha(LocalDate fecha) {
        int dia = Integer.parseInt(String.format("%1$td", fecha));
        int mes = Integer.parseInt(String.format("%1$tm", fecha));
        int anno = Integer.parseInt(String.format("%1$tY", fecha));
        return anno * 10000 + mes * 100 + dia;
    }

    private LocalDate siguienteDia(LocalDate fecha) {
        int dia = Integer.parseInt(String.format("%1$td", fecha));
        int mes = Integer.parseInt(String.format("%1$tm", fecha));
        int anno = Integer.parseInt(String.format("%1$tY", fecha));

        dia++;
        if (dia > diasDelMes(mes, anno)) {
            dia = 1;
            mes++;
            if (mes > 12) {
                mes = 1;
                anno++;
            }
        }
        return LocalDate.of(anno, mes, dia);
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

    private boolean libreExcepto(Cita propuesta, int ignorada) {
        int iniPropuesta = aMinutos(propuesta.getInicio());
        int finPropuesta = aMinutos(propuesta.getFin());

        for (int i = 0; i < citas.size(); i++) {
            if (i != ignorada) {
                Cita existente = citas.get(i);
                if (existente.getFecha().equals(propuesta.getFecha())
                        && seSolapan(iniPropuesta, finPropuesta,
                        aMinutos(existente.getInicio()),
                        aMinutos(existente.getFin()))) {
                    return false;
                }
            }
        }
        return true;
    }

    private LocalTime buscarPrimerHueco(LocalDate fecha, int duracion) {
        if (duracion > getJornada()) {
            return null;
        }

        int numFecha = numeroFecha(fecha);
        int candidato = aMinutos(apertura);
        for (Cita cita : citas) {
            int fechaCita = numeroFecha(cita.getFecha());
            if (fechaCita < numFecha) {
                continue;
            }
            if (fechaCita > numFecha) {
                break;
            }
            int inicioCita = aMinutos(cita.getInicio());
            if (inicioCita - candidato >= duracion) {
                return deMinutos(candidato);
            }
            int finCita = aMinutos(cita.getFin());
            if (finCita > candidato) {
                candidato = finCita;
            }
        }
        if (aMinutos(cierre) - candidato >= duracion) {
            return deMinutos(candidato);
        }
        return null;
    }

    private boolean seSolapan(int inicio1, int fin1, int inicio2, int fin2) {
        return inicio1 < fin2 && inicio2 < fin1;
    }
}
