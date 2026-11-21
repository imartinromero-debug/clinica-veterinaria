/*
 * Archivo Main.java
 * Práctica 2. Gestión de una clínica veterinaria — Pruebas de Hito 1
 */

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

class CitaPrueba extends Cita {

    public CitaPrueba(String cliente, LocalDate fecha, LocalTime inicio,
                      int duracion) throws ArgumentoException {
        super(cliente, fecha, inicio, duracion);
    }

    @Override
    public CitaPrueba conInicio(LocalTime nuevoInicio)
            throws ArgumentoException {
        return new CitaPrueba(getCliente(), getFecha(), nuevoInicio,
                getDuracion());
    }
}

int pruebasCorrectas = 0;
int pruebasFallidas = 0;

void main() {
    LocalDate lunes = LocalDate.of(2026, 10, 19);
    LocalDate martes = LocalDate.of(2026, 10, 20);
    LocalDate miercoles = LocalDate.of(2026, 10, 21);
    LocalDate sabado = LocalDate.of(2026, 10, 24);
    LocalDate domingo = LocalDate.of(2026, 10, 25);

    try {
        Agenda agenda = new Agenda(LocalTime.of(9, 0), LocalTime.of(14, 0));
        comprobar(agenda.getJornada() == 300,
                "Agenda de 9:00 a 14:00 tiene jornada de 300 min");

        agenda.reservar(new CitaPrueba("Bruno", martes, LocalTime.of(10, 0), 60));
        agenda.reservar(new CitaPrueba("Ana", martes, LocalTime.of(9, 0), 40));
        agenda.reservar(new CitaPrueba("David", martes, LocalTime.of(12, 30), 60));
        agenda.reservar(new CitaPrueba("Carmen", martes, LocalTime.of(11, 15), 45));
        agenda.reservar(new CitaPrueba("Ana", lunes, LocalTime.of(13, 0), 30));
        agenda.reservar(new CitaPrueba("Elena", miercoles, LocalTime.of(9, 0), 300));
        agenda.reservar(new CitaPrueba("Nora", sabado, LocalTime.of(9, 0), 30));
        agenda.reservar(new CitaPrueba("Pablo", domingo, LocalTime.of(11, 0), 45));

        comprobar(agenda.getNumCitas() == 8,
                "Se reservan 8 citas incluidos sábado y domingo");
        comprobar(agenda.getCitaEn(0).getCliente().equals("Ana")
                        && agenda.getCitaEn(0).getFecha().equals(lunes),
                "Primera cita en orden cronológico es Ana el lunes");
        comprobar(agenda.tieneCita("ana", martes),
                "tieneCita no distingue mayúsculas");
        comprobar(agenda.getTiempoLibre(martes) == 95
                        && agenda.getTiempoLibre(miercoles) == 0,
                "getTiempoLibre calcula 95 min el martes y 0 el miércoles");
        comprobar(agenda.primerHueco(martes, 30).equals(LocalTime.of(12, 0)),
                "primerHueco de 30 min el martes es 12:00");

        ArrayList<Cita> entreFechas = agenda.getCitasEntre(lunes, martes);
        comprobar(entreFechas.size() == 5,
                "getCitasEntre incluye ambos extremos (5 citas)");

        agenda.retrasar("Nora", sabado, 30);
        Cita retrasada = agenda.getCitaDe("Nora", sabado);
        comprobar(retrasada instanceof CitaPrueba
                        && retrasada.getInicio().equals(LocalTime.of(9, 30)),
                "retrasar usa conInicio y conserva el subtipo");

        agenda.cancelar("Bruno", martes);
        comprobar(!agenda.tieneCita("Bruno", martes)
                        && agenda.primerHueco(martes, 45).equals(LocalTime.of(9, 40)),
                "Tras cancelar a Bruno, el primer hueco de 45 min es 9:40");

        int antes = agenda.getNumCitas();
        try {
            new CitaPrueba("   ", lunes, LocalTime.of(9, 0), 30);
            comprobar(false, "Cliente vacío debe lanzar ArgumentoException");
        } catch (ArgumentoException e) {
            comprobar(true, "Cliente vacío lanza ArgumentoException");
        }

        try {
            agenda.reservar(new CitaPrueba("ANA", martes, LocalTime.of(13, 30), 20));
            comprobar(false, "Cita duplicada debe lanzar ArgumentoException");
        } catch (ArgumentoException e) {
            comprobar(agenda.getNumCitas() == antes,
                    "Cita duplicada lanza ArgumentoException sin cambiar la agenda");
        }
    } catch (Exception e) {
        comprobar(false, "Error inesperado: " + e.getMessage());
    }

    IO.println("Pruebas correctas: " + pruebasCorrectas
            + " | Fallidas: " + pruebasFallidas);
}

void comprobar(boolean condicion, String descripcion) {
    if (condicion) {
        pruebasCorrectas++;
        IO.println("  [OK] " + descripcion);
    } else {
        pruebasFallidas++;
        IO.println("  [FALLO] " + descripcion);
    }
}