/*
 * Archivo Clinica.java
 * Práctica 2. Gestión de una clínica veterinaria
 */

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Gestiona los veterinarios, las mascotas, las citas pendientes y el historial
 * clínico de una clínica veterinaria.
 */
public class Clinica {

    public static final LocalTime APERTURA = LocalTime.of(9, 0);
    public static final LocalTime CIERRE = LocalTime.of(20, 0);

    private String nombre;
    private ArrayList<Veterinario> veterinarios;
    private ArrayList<Mascota> mascotas;
    private ArrayList<CitaVeterinaria> historial;

    /**
     * Crea una clínica sin veterinarios, mascotas ni historial.
     *
     * @param nombre Nombre de la clínica.
     * @throws ArgumentoException Si el nombre es nulo o vacío.
     */
    public Clinica(String nombre) throws ArgumentoException {
        if (nombre == null || nombre.isBlank()) {
            throw new ArgumentoException(
                    "El nombre de la clínica no puede ser nulo ni vacío.");
        }

        this.nombre = nombre;
        this.veterinarios = new ArrayList<>();
        this.mascotas = new ArrayList<>();
        this.historial = new ArrayList<>();
    }

    /**
     * Devuelve el nombre de la clínica.
     *
     * @return Nombre de la clínica.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Indica si la clínica abre en la fecha indicada (de lunes a sábado).
     *
     * @param fecha Fecha consultada.
     * @return false si es domingo, true de lunes a sábado.
     * @throws ArgumentoException Si la fecha es nula.
     */
    public static boolean abre(LocalDate fecha) throws ArgumentoException {
        if (fecha == null) {
            throw new ArgumentoException("La fecha no puede ser nula.");
        }
        int dia = Integer.parseInt(String.format("%1$td", fecha));
        int mes = Integer.parseInt(String.format("%1$tm", fecha));
        int anno = Integer.parseInt(String.format("%1$tY", fecha));

        int dias = 0;
        for (int a = 1; a < anno; a++) {
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
        return dias % 7 != 6;
    }

    private static int diasDelMes(int mes, int anno) {
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

    /**
     * Registra un nuevo veterinario en la clínica.
     *
     * @param v Veterinario que se añade.
     * @throws ArgumentoException Si el veterinario es nulo.
     * @throws ClinicaException Si su código ya existe.
     */
    public void addVeterinario(Veterinario v)
            throws ArgumentoException, ClinicaException {
        if (v == null) {
            throw new ArgumentoException("El veterinario no puede ser nulo.");
        }
        if (buscarVeterinario(v.getCodigo()) != null) {
            throw new ClinicaException(
                    "Ya existe un veterinario con el código "
                            + v.getCodigo() + ".");
        }
        veterinarios.add(v);
    }

    /**
     * Da de alta una nueva mascota en la clínica.
     *
     * @param m Mascota que se añade.
     * @throws ArgumentoException Si la mascota es nula.
     * @throws ClinicaException Si su identificador ya existe.
     */
    public void addMascota(Mascota m)
            throws ArgumentoException, ClinicaException {
        if (m == null) {
            throw new ArgumentoException("La mascota no puede ser nula.");
        }
        if (buscarMascota(m.getId()) != null) {
            throw new ClinicaException(
                    "Ya existe una mascota con el identificador "
                            + m.getId() + ".");
        }
        mascotas.add(m);
    }

    /**
     * Añade directamente al historial una cita ya terminada, sin comprobar nada.
     * Se emplea en la carga de datos y en escenarios de prueba.
     *
     * @param c Cita terminada que se incorpora al historial.
     * @throws ArgumentoException Si la cita es nula.
     */
    public void addHistorial(CitaVeterinaria c) throws ArgumentoException {
        if (c == null) {
            throw new ArgumentoException("La cita no puede ser nula.");
        }
        historial.add(c);
    }

    /**
     * Busca un veterinario por su código sin distinguir mayúsculas.
     *
     * @param codigo Código buscado.
     * @return Veterinario encontrado, o null si no existe.
     * @throws ArgumentoException Si el código es nulo o vacío.
     */
    public Veterinario buscarVeterinario(String codigo)
            throws ArgumentoException {
        if (codigo == null || codigo.isBlank()) {
            throw new ArgumentoException(
                    "El código del veterinario no puede ser nulo ni vacío.");
        }
        for (Veterinario v : veterinarios) {
            if (v.getCodigo().equalsIgnoreCase(codigo)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Busca una mascota por su identificador sin distinguir mayúsculas.
     *
     * @param id Identificador buscado.
     * @return Mascota encontrada, o null si no existe.
     * @throws ArgumentoException Si el identificador es nulo o vacío.
     */
    public Mascota buscarMascota(String id) throws ArgumentoException {
        if (id == null || id.isBlank()) {
            throw new ArgumentoException(
                    "El identificador de la mascota no puede ser nulo ni vacío.");
        }
        for (Mascota m : mascotas) {
            if (m.getId().equalsIgnoreCase(id)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Devuelve una copia de la lista de veterinarios.
     *
     * @return Copia de los veterinarios registrados.
     */
    public ArrayList<Veterinario> getVeterinarios() {
        ArrayList<Veterinario> copia = new ArrayList<>();
        for (Veterinario v : veterinarios) {
            copia.add(v);
        }
        return copia;
    }

    /**
     * Devuelve una copia de la lista de mascotas.
     *
     * @return Copia de las mascotas registradas.
     */
    public ArrayList<Mascota> getMascotas() {
        ArrayList<Mascota> copia = new ArrayList<>();
        for (Mascota m : mascotas) {
            copia.add(m);
        }
        return copia;
    }

    /**
     * Devuelve una copia del historial de citas terminadas.
     *
     * @return Copia del historial.
     */
    public ArrayList<CitaVeterinaria> getHistorial() {
        ArrayList<CitaVeterinaria> copia = new ArrayList<>();
        for (CitaVeterinaria c : historial) {
            copia.add(c);
        }
        return copia;
    }

    /**
     * Da formato en euros a una cantidad expresada en céntimos (1250 -> "12,50 €").
     *
     * @param centimos Importe en céntimos.
     * @return Cadena formateada en euros.
     */
    public static String euros(int centimos) {
        if (centimos < 0) {
            int positivo = -centimos;
            return String.format("-%d,%02d €", positivo / 100, positivo % 100);
        }
        return String.format("%d,%02d €", centimos / 100, centimos % 100);
    }
}
