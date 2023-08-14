package agenda.activities;

import java.time.LocalDateTime;

public class Tarefa extends AgendaActivity {
    public Tarefa(String name, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(name, startDateTime, endDateTime);
    }

    @Override
    public void show() {
        System.out.println("Tarefa:");
        System.out.println("    Nome..: " + this.name);
        System.out.println("    Dia...: " + this.formatStartDateTime("dd/MM/yyy"));
    }

    @Override
    public void showOneLine() {
        System.out.println("    Tarefa - " + this.name);
    }
}
