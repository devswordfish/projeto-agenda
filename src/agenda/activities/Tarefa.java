package agenda.activities;

import java.time.LocalDateTime;

public class Tarefa extends AgendaActivity {
    public Tarefa(String name, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(name, startDateTime, endDateTime);
    }

    @Override
    public void visualize() {
        System.out.println("Tarefa:");
        System.out.println("    Nome..: " + this.name);
        System.out.println("    Dia...: " + this.formatStartDateTime("dd/MM/yyy"));
    }
}
