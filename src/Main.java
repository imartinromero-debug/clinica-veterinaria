import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

int pruebasCorrectas = 0;
int pruebasFallidas = 0;

void main() {
    try {
        probarAgendaAdaptada();
        probarMascotasYEnumerados();
        probarVeterinariosYEspecialidades();
        probarPolimorfismoYRetraso();
        probarNormasYCasosLimite();
        probarTerminarEIngresos();
    } catch (Exception e) {
        comprobar(false, "Excepción inesperada en las pruebas: "
                + e.getMessage());
    }

    IO.println("\n========================================");
    IO.println("Pruebas correctas: " + pruebasCorrectas);
    IO.println("Pruebas fallidas:  " + pruebasFallidas);
    IO.println("========================================");
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

void probarAgendaAdaptada() throws ArgumentoException {
    IO.println("=== 1. TAD AGENDA ADAPTADO Y CITA ABSTRACTA ===");

    Veterinario v1 = new Veterinario("V1", "Laura Martín", Especialidad.GENERAL);
    Mascota toby = new Mascota("M001", "Toby", "Ana García",
            Especie.PERRO, LocalDate.of(2020, 3, 15), 28000);
    Mascota misi = new Mascota("M002", "Misi", "Bruno López",
            Especie.GATO, LocalDate.of(2022, 6, 1), 4000);
    Mascota luna = new Mascota("M004", "Luna", "Diego Martín",
            Especie.PERRO, LocalDate.of(2025, 2, 20), 8000);

    Agenda agenda = new Agenda(LocalTime.of(9, 0), LocalTime.of(14, 0));
    comprobar(agenda.getApertura().equals(LocalTime.of(9, 0))
                    && agenda.getCierre().equals(LocalTime.of(14, 0))
                    && agenda.getJornada() == 300,
            "Agenda guarda apertura, cierre y calcula jornada de 300 min");

    LocalDate lunes = LocalDate.of(2026, 10, 19);
    LocalDate martes = LocalDate.of(2026, 10, 20);
    LocalDate sabado = LocalDate.of(2026, 10, 24);
    LocalDate domingo = LocalDate.of(2026, 10, 25);

    agenda.reservar(new Consulta(misi, v1, martes, LocalTime.of(10, 0), "Vacuna"));
    agenda.reservar(new Consulta(toby, v1, martes, LocalTime.of(9, 0), "Cojera"));
    agenda.reservar(new Consulta(luna, v1, lunes, LocalTime.of(13, 0), " Control "));
    agenda.reservar(new Consulta(toby, v1, sabado, LocalTime.of(9, 0), "Revisión"));
    agenda.reservar(new Consulta(misi, v1, domingo, LocalTime.of(11, 0), "Urgencia"));

    comprobar(agenda.getNumCitas() == 5,
            "Agenda admite sábado y domingo y cuenta 5 citas");
    comprobar(agenda.getCitaEn(0).getCliente().equals("M004")
                    && agenda.getCitaEn(1).getCliente().equals("M001"),
            "Las citas quedan ordenadas por fecha y hora de inicio");
    comprobar(agenda.tieneCita("m001", martes)
                    && agenda.getCitaDe("M001", martes).getFin().equals(LocalTime.of(9, 30)),
            "tieneCita ignora mayúsculas y getFin calcula 9:30");

    ArrayList<Cita> entre = agenda.getCitasEntre(lunes, martes);
    comprobar(entre.size() == 3,
            "getCitasEntre incluye ambas fechas extremas (3 citas)");
    entre.clear();
    comprobar(agenda.getCitasEntre(lunes, martes).size() == 3,
            "Modificar la lista de getCitasEntre no altera la agenda");

    comprobar(agenda.getTiempoLibre(martes) == 240
                    && agenda.primerHueco(martes, 30).equals(LocalTime.of(9, 30)),
            "getTiempoLibre devuelve 240 min y primerHueco 09:30");

    agenda.cancelar("M002", martes);
    comprobar(!agenda.tieneCita("M002", martes) && agenda.getNumCitas() == 4,
            "cancelar elimina únicamente la cita indicada");

    int antes = agenda.getNumCitas();
    try {
        new Agenda(LocalTime.of(14, 0), LocalTime.of(9, 0));
        comprobar(false, "Horario invertido debe lanzar ArgumentoException");
    } catch (ArgumentoException e) {
        comprobar(true, "Horario invertido lanza ArgumentoException");
    }

    try {
        agenda.reservar(new Consulta(toby, v1, martes, LocalTime.of(11, 0), "Otra"));
        comprobar(false, "Cliente duplicado en el mismo día debe fallar");
    } catch (ArgumentoException e) {
        comprobar(agenda.getNumCitas() == antes,
                "Cita duplicada lanza ArgumentoException sin cambiar la agenda");
    }
}

void probarMascotasYEnumerados() throws ArgumentoException {
    IO.println("\n=== 2. MASCOTAS Y ENUMERADOS ===");

    comprobar(Especie.PERRO.getDuracionConsulta() == 30
                    && Especie.PERRO.getEspecialidad() == Especialidad.GENERAL
                    && Especie.PERRO.toString().equals("Perro"),
            "Especie.PERRO: 30 min, GENERAL y nombre Perro");
    comprobar(Especie.GATO.getDuracionConsulta() == 30
                    && Especie.GATO.getEspecialidad() == Especialidad.GENERAL,
            "Especie.GATO: 30 min y GENERAL");
    comprobar(Especie.EXOTICO.getDuracionConsulta() == 45
                    && Especie.EXOTICO.getEspecialidad() == Especialidad.EXOTICOS
                    && Especie.EXOTICO.toString().equals("Exótico"),
            "Especie.EXOTICO: 45 min, EXOTICOS y nombre Exótico");

    Mascota toby = new Mascota("M001", "Toby", "Ana García",
            Especie.PERRO, LocalDate.of(2020, 3, 15), 28000);
    comprobar(toby.getEdad(LocalDate.of(2026, 3, 14)) == 5
                    && toby.getEdad(LocalDate.of(2026, 3, 15)) == 6,
            "getEdad calcula años cumplidos antes y en el cumpleaños");

    try {
        toby.getEdad(LocalDate.of(2019, 1, 1));
        comprobar(false, "Fecha anterior al nacimiento debe fallar");
    } catch (ArgumentoException e) {
        comprobar(true, "getEdad anterior al nacimiento lanza ArgumentoException");
    }

    try {
        new Mascota("M009", "Rex", "Luis", Especie.PERRO,
                LocalDate.of(2023, 5, 1), 0);
        comprobar(false, "Peso no positivo debe lanzar ArgumentoException");
    } catch (ArgumentoException e) {
        comprobar(true, "Mascota con peso 0 lanza ArgumentoException");
    }
}

void probarVeterinariosYEspecialidades() throws ArgumentoException {
    IO.println("\n=== 3. VETERINARIOS Y REGLA DE ESPECIALIDADES ===");

    Veterinario v1 = new Veterinario("V1", "Laura Martín", Especialidad.GENERAL);
    Veterinario v2 = new Veterinario("V2", "Pablo Ortega", Especialidad.CIRUGIA);
    Veterinario v3 = new Veterinario("V3", "Elena Sanz", Especialidad.EXOTICOS);

    comprobar(v1.getAgenda().getApertura().equals(LocalTime.of(9, 0))
                    && v1.getAgenda().getCierre().equals(LocalTime.of(20, 0)),
            "Veterinario crea su agenda con horario 9:00 a 20:00");
    comprobar(v1.puedeAtender(Especialidad.GENERAL)
                    && !v1.puedeAtender(Especialidad.CIRUGIA)
                    && !v1.puedeAtender(Especialidad.EXOTICOS),
            "Generalista solo atiende GENERAL");
    comprobar(v2.puedeAtender(Especialidad.GENERAL)
                    && v2.puedeAtender(Especialidad.CIRUGIA)
                    && !v2.puedeAtender(Especialidad.EXOTICOS),
            "Cirujano atiende GENERAL y CIRUGIA");
    comprobar(v3.puedeAtender(Especialidad.GENERAL)
                    && v3.puedeAtender(Especialidad.EXOTICOS)
                    && !v3.puedeAtender(Especialidad.CIRUGIA),
            "Especialista en exóticos atiende GENERAL y EXOTICOS");
}

void probarPolimorfismoYRetraso() throws ArgumentoException {
    IO.println("\n=== 4. POLIMORFISMO Y CONSERVACIÓN DEL SUBTIPO ===");

    Veterinario v1 = new Veterinario("V1", "Laura Martín", Especialidad.GENERAL);
    Veterinario v2 = new Veterinario("V2", "Pablo Ortega", Especialidad.CIRUGIA);
    Veterinario v3 = new Veterinario("V3", "Elena Sanz", Especialidad.EXOTICOS);

    Mascota toby = new Mascota("M001", "Toby", "Ana García",
            Especie.PERRO, LocalDate.of(2020, 3, 15), 28000);
    Mascota misi = new Mascota("M002", "Misi", "Bruno López",
            Especie.GATO, LocalDate.of(2022, 6, 1), 4000);
    Mascota kiwi = new Mascota("M003", "Kiwi", "Carla Ruiz",
            Especie.EXOTICO, LocalDate.of(2024, 1, 10), 1800);

    LocalDate lunes = LocalDate.of(2026, 10, 19);
    LocalDate martes = LocalDate.of(2026, 10, 20);

    CitaVeterinaria cPerro = new Consulta(
            toby, v1, lunes, LocalTime.of(9, 0), "cojera");
    CitaVeterinaria cGato = new Consulta(
            misi, v1, lunes, LocalTime.of(10, 0), "vacuna");
    CitaVeterinaria cExotico = new Consulta(
            kiwi, v3, lunes, LocalTime.of(11, 0), "revisión anual");
    CitaVeterinaria cirugia = new Cirugia(
            toby, v2, martes, LocalTime.of(9, 0), 120,
            "extirpación de un quiste");

    comprobar(cPerro.precio() == 3500 && cPerro.getDuracion() == 30
                    && cPerro.especialidadRequerida() == Especialidad.GENERAL
                    && cPerro.getTipo().equals("Consulta"),
            "Consulta de perro: 3500 céntimos, 30 min, GENERAL, Consulta");
    comprobar(cGato.precio() == 3500 && cGato.getDuracion() == 30
                    && cGato.especialidadRequerida() == Especialidad.GENERAL,
            "Consulta de gato: 3500 céntimos, 30 min, GENERAL");
    comprobar(cExotico.precio() == 3500 && cExotico.getDuracion() == 45
                    && cExotico.especialidadRequerida() == Especialidad.EXOTICOS,
            "Consulta de exótico: 3500 céntimos, 45 min, EXOTICOS");
    comprobar(cirugia.precio() == 22200 && cirugia.getDuracion() == 120
                    && cirugia.especialidadRequerida() == Especialidad.CIRUGIA
                    && cirugia.getTipo().equals("Cirugía"),
            "Cirugía de Toby (120 min, 28 kg): 22200 céntimos (222,00 €)");

    v1.getAgenda().reservar(cPerro);
    v1.getAgenda().retrasar("M001", lunes, 30);
    Cita consultaRetrasada = v1.getAgenda().getCitaDe("M001", lunes);
    comprobar(consultaRetrasada instanceof Consulta
                    && consultaRetrasada.getInicio().equals(LocalTime.of(9, 30)),
            "retrasar una Consulta conserva la instancia Consulta");

    v2.getAgenda().reservar(cirugia);
    v2.getAgenda().retrasar("M001", martes, 60);
    Cita cirugiaRetrasada = v2.getAgenda().getCitaDe("M001", martes);
    comprobar(cirugiaRetrasada instanceof Cirugia
                    && cirugiaRetrasada.getInicio().equals(LocalTime.of(10, 0)),
            "retrasar una Cirugia conserva la instancia Cirugia");
}

void probarNormasYCasosLimite() throws ArgumentoException, ClinicaException {
    IO.println("\n=== 5. NORMAS DE PROGRAMACIÓN Y CASOS LÍMITE ===");

    Veterinario v1 = new Veterinario("V1", "Laura Martín", Especialidad.GENERAL);
    Veterinario v2 = new Veterinario("V2", "Pablo Ortega", Especialidad.CIRUGIA);
    Mascota toby = new Mascota("M001", "Toby", "Ana García",
            Especie.PERRO, LocalDate.of(2020, 3, 15), 28000);
    Mascota kiwi = new Mascota("M003", "Kiwi", "Carla Ruiz",
            Especie.EXOTICO, LocalDate.of(2024, 1, 10), 1800);

    LocalDateTime ahora = LocalDateTime.of(2026, 10, 16, 10, 0);
    LocalDate sabado = LocalDate.of(2026, 10, 17);
    LocalDate domingo = LocalDate.of(2026, 10, 18);

    CitaVeterinaria limiteCierre = new Consulta(
            toby, v1, sabado, LocalTime.of(19, 30), "Última hora");
    limiteCierre.comprobarNormas(ahora);
    comprobar(limiteCierre.getFin().equals(LocalTime.of(20, 0)),
            "Caso límite válido: consulta en sábado que termina a las 20:00");

    CitaVeterinaria limiteManana = new Cirugia(
            toby, v2, sabado, LocalTime.of(12, 0), 120, "Esterilización");
    limiteManana.comprobarNormas(ahora);
    comprobar(limiteManana.getFin().equals(LocalTime.of(14, 0)),
            "Caso límite válido: cirugía del día siguiente que termina a las 14:00");

    probarNormaInvalida(new Consulta(toby, v1, domingo,
                    LocalTime.of(10, 0), "Control"), ahora,
            "Norma domingo: rechaza cita en domingo con ClinicaException");
    probarNormaInvalida(new Consulta(toby, v1, sabado,
                    LocalTime.of(19, 45), "Tarde"), ahora,
            "Norma horario: rechaza cita que acaba después de las 20:00");
    probarNormaInvalida(new Consulta(toby, v1, LocalDate.of(2026, 10, 16),
                    LocalTime.of(9, 30), "Pasado"), ahora,
            "Norma pasado: rechaza cita anterior a ahora");
    probarNormaInvalida(new Consulta(kiwi, v1, sabado,
                    LocalTime.of(11, 0), "Plumaje"), ahora,
            "Norma especialidad: generalista no puede atender exóticos");
    probarNormaInvalida(new Cirugia(toby, v2, LocalDate.of(2026, 10, 16),
                    LocalTime.of(11, 0), 60, "Urgencia"), ahora,
            "Norma cirugía: rechaza cirugía programada para el mismo día");
    probarNormaInvalida(new Cirugia(toby, v2, sabado,
                    LocalTime.of(12, 30), 120, "Tarde"), ahora,
            "Norma cirugía: rechaza cirugía que termina después de las 14:00");
    probarNormaInvalida(new Cirugia(toby, v2, sabado,
                    LocalTime.of(10, 0), 45, "Tramo inválido"), ahora,
            "Norma cirugía: rechaza duración que no es múltiplo de 30");
}

void probarNormaInvalida(CitaVeterinaria cita, LocalDateTime ahora,
                         String descripcion) throws ArgumentoException {
    try {
        cita.comprobarNormas(ahora);
        comprobar(false, descripcion);
    } catch (ClinicaException e) {
        comprobar(true, descripcion);
    }
}

void probarTerminarEIngresos() throws ArgumentoException {
    IO.println("\n=== 6. DESENLACES E INGRESOS ===");

    Veterinario v1 = new Veterinario("V1", "Laura Martín", Especialidad.GENERAL);
    Mascota toby = new Mascota("M001", "Toby", "Ana García",
            Especie.PERRO, LocalDate.of(2020, 3, 15), 28000);
    LocalDate lunes = LocalDate.of(2026, 10, 19);

    Consulta cAtendida = new Consulta(
            toby, v1, lunes, LocalTime.of(9, 0), "cojera");
    comprobar(cAtendida.getResultado() == null && cAtendida.ingreso() == 0,
            "Cita pendiente tiene resultado null e ingreso 0");

    cAtendida.terminar(Resultado.ATENDIDA, "Esguince leve, reposo");
    comprobar(cAtendida.getResultado() == Resultado.ATENDIDA
                    && cAtendida.ingreso() == 3500
                    && cAtendida.getNotas().equals("Esguince leve, reposo"),
            "Cita ATENDIDA cobra su precio (3500 céntimos) y guarda notas");

    Consulta cNoPresentado = new Consulta(
            toby, v1, lunes, LocalTime.of(10, 0), "vacuna");
    cNoPresentado.terminar(Resultado.NO_PRESENTADO, "");
    comprobar(cNoPresentado.ingreso() == 0,
            "Cita NO_PRESENTADO admite notas vacías e ingresa 0");

    Consulta cCancelada = new Consulta(
            toby, v1, lunes, LocalTime.of(11, 0), "control");
    cCancelada.terminar(Resultado.CANCELADA, "");
    comprobar(cCancelada.ingreso() == 0,
            "Cita CANCELADA ingresa 0");

    try {
        cAtendida.terminar(Resultado.CANCELADA, "");
        comprobar(false, "Terminar una cita ya terminada debe fallar");
    } catch (ArgumentoException e) {
        comprobar(true, "Terminar dos veces lanza ArgumentoException");
    }
}
