/*
 * Archivo ArgumentoException.java
 * Práctica 2. Gestión de una clínica veterinaria
 */

/**
 * Excepción comprobada que indica que un argumento vale null o tiene un valor
 * imposible.
 */
public class ArgumentoException extends Exception {

    /**
     * Construye la excepción con el mensaje descriptivo del fallo.
     *
     * @param mensaje Explicación del argumento inválido.
     */
    public ArgumentoException(String mensaje) {
        super(mensaje);
    }
}
