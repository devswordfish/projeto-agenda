package agenda.activities;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

public class Tarefa extends AgendaActivity {
    public Tarefa(String name, LocalDate date) {
        super(
            name,
            LocalDateTime.of(date, LocalTime.of(0, 0, 0, 0)), // começo do dia
            LocalDateTime.of(date, LocalTime.of(23, 59, 59, 999_999_999)) // fim do dia
        );
    }

    @Override
    public void showStats() {
        System.out.println("Tarefa:");
        System.out.println("    Nome..: " + this.name);
        System.out.println("    Dia...: " + this.formatStartDateTime("dd/MM/yyy"));
    }

    @Override
    public void show() {
        System.out.println("    Tarefa - " + this.name);
    }
}
