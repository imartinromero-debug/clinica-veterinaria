/*
 * Archivo Especie.java
 * Práctica 2. Gestión de una clínica veterinaria
 */

/**
 * Especies de mascotas admitidas por la clínica, junto con la duración y la
 * especialidad que exige su consulta.
 */
public enum Especie {
    PERRO("Perro", 30, Especialidad.GENERAL),
    GATO("Gato", 30, Especialidad.GENERAL),
    EXOTICO("Exótico", 45, Especialidad.EXOTICOS);

    private String nombre;
    private int duracionConsulta;
    private Especialidad especialidad;

    Especie(String nombre, int duracionConsulta, Especialidad especialidad) {
        this.nombre = nombre;
        this.duracionConsulta = duracionConsulta;
        this.especialidad = especialidad;
    }

    /**
     * Devuelve el nombre legible de la especie.
     *
     * @return Nombre de la especie.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la duración en minutos de una consulta para esta especie.
     *
     * @return Duración de la consulta en minutos.
     */
    public int getDuracionConsulta() {
        return duracionConsulta;
    }

    /**
     * Devuelve la especialidad exigida para atender una consulta de esta especie.
     *
     * @return Especialidad requerida.
     */
    public Especialidad getEspecialidad() {
        return especialidad;
    }

    @Override
    public String toString() {
        return nombre;
    }
}