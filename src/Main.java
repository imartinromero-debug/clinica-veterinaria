import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

void main() {
    Agenda agenda = null;

    LocalDate lunes = LocalDate.of(2026, 10, 19);
    LocalDate martes = LocalDate.of(2026, 10, 20);
    LocalDate miercoles = LocalDate.of(2026, 10, 21);
    LocalDate sabado = LocalDate.of(2026, 10, 24);
    LocalDate domingo = LocalDate.of(2026, 10, 25);

    try {
        agenda = new Agenda(LocalTime.of(9, 0), LocalTime.of(14, 0));
        IO.println("Horario de prueba: " + agenda.getApertura() + " a "
                + agenda.getCierre() + " (" + agenda.getJornada() + " min)");

        agenda.reservar(new Cita("Bruno", martes, LocalTime.of(10, 0), 60));
        agenda.reservar(new Cita("Ana", martes, LocalTime.of(9, 0), 40));
        agenda.reservar(new Cita("David", martes, LocalTime.of(12, 30), 60));
        agenda.reservar(new Cita("Carmen", martes, LocalTime.of(11, 15), 45));
        agenda.reservar(new Cita("Ana", lunes, LocalTime.of(13, 0), 30));
        agenda.reservar(new Cita("Elena", miercoles, LocalTime.of(9, 0), 300));
        agenda.reservar(new Cita("Nora", sabado, LocalTime.of(9, 0), 30));
        agenda.reservar(new Cita("Pablo", domingo, LocalTime.of(11, 0), 45));

        IO.println();
        IO.println(agenda);
        IO.println("Total de citas: " + agenda.getNumCitas());
        IO.println("Primera cita en orden: " + agenda.getCitaEn(0));
        IO.println("Ana tiene cita el martes: " + agenda.tieneCita("ana", martes));
        IO.println("Cita de Ana el martes: " + agenda.getCitaDe("ANA", martes));
        IO.println("Tiempo libre el martes: " + agenda.getTiempoLibre(martes) + " min");
        IO.println("Tiempo libre el miércoles: "
                + agenda.getTiempoLibre(miercoles) + " min");
        IO.println("¿Está libre 9:40-10:00 el martes?: "
                + agenda.estaLibre(martes, LocalTime.of(9, 40), 20));
        IO.println("¿Hay hueco de 45 min el martes?: "
                + agenda.hayHueco(martes, 45));
        IO.println("Primer hueco de 30 min el martes: "
                + agenda.primerHueco(martes, 30));
        IO.println("Primer día con 45 min desde el martes: "
                + agenda.primerDiaConHueco(martes, 45));

        ArrayList<Cita> entreFechas = agenda.getCitasEntre(lunes, martes);
        IO.println("Citas entre lunes y martes (ambos incluidos): "
                + entreFechas.size());

        ArrayList<Cita> copiaMartes = agenda.getCitasDel(martes);
        copiaMartes.clear();
        IO.println("Tras vaciar la copia de getCitasDel, siguen en martes: "
                + agenda.getCitasDel(martes).size());

        agenda.retrasar("Nora", sabado, 30);
        Cita retrasada = agenda.getCitaDe("Nora", sabado);
        IO.println("Cita retrasada con conInicio: " + retrasada);

        agenda.cancelar("Bruno", martes);
        IO.println("Tras cancelar a Bruno, primer hueco de 45 min el martes: "
                + agenda.primerHueco(martes, 45));
    } catch (Exception e) {
        IO.println("Error inesperado: " + e.getMessage());
    }

    IO.println("\n--- Comprobación de errores ---");
    int numAntes = agenda.getNumCitas();

    try {
        new Agenda(LocalTime.of(14, 0), LocalTime.of(9, 0));
    } catch (Exception e) {
        IO.println("Horario inválido rechazado: " + e.getMessage());
    }

    try {
        new Cita("   ", lunes, LocalTime.of(9, 0), 30);
    } catch (Exception e) {
        IO.println("Cliente vacío rechazado: " + e.getMessage());
    }

    try {
        agenda.reservar(new Cita("ANA", martes, LocalTime.of(13, 30), 20));
    } catch (Exception e) {
        IO.println("Cita duplicada rechazada: " + e.getMessage());
    }

    try {
        agenda.reservar(new Cita("Luis", martes, LocalTime.of(9, 15), 30));
    } catch (Exception e) {
        IO.println("Solapamiento rechazado: " + e.getMessage());
    }

    try {
        agenda.retrasar("David", martes, 60);
    } catch (Exception e) {
        IO.println("Retraso fuera de horario rechazado: " + e.getMessage());
    }

    try {
        agenda.getCitasEntre(miercoles, lunes);
    } catch (Exception e) {
        IO.println("Periodo invertido rechazado: " + e.getMessage());
    }

    try {
        agenda.primerHueco(miercoles, 30);
    } catch (Exception e) {
        IO.println("Día sin hueco rechazado: " + e.getMessage());
    }

    IO.println("Citas antes de los fallos: " + numAntes
            + " | después de los fallos: " + agenda.getNumCitas());
}
