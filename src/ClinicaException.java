/*
 * Archivo ClinicaException.java
 * Práctica 2. Gestión de una clínica veterinaria
 */

/**
 * Excepción comprobada que indica que una operación no está permitida por las
 * normas de la clínica o por el estado de las agendas.
 */
public class ClinicaException extends Exception {

    /**
     * Construye la excepción con el mensaje que explica el motivo del rechazo.
     *
     * @param mensaje Explicación del incumplimiento de la norma.
     */
    public ClinicaException(String mensaje) {
        super(mensaje);
    }
}
