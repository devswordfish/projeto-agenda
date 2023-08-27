package agendapages.activities;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

public class Task extends AgendaActivity {
    private boolean completed;

    public Task(String name, LocalDate date) {
        super(name);
        this.setDate(date);
        this.completed = false;
    }

    public void complete() {
        this.completed = true;
    }

    public void uncomplete() {
        this.completed = false;
    }

    @Override
    public void showAttributes() {
        System.out.println("Tarefa:");
        System.out.println("    Nome..: " + this.name);
        System.out.println("    Dia...: " + this.formatStartDateTime("dd/MM/yyy"));
    }

    @Override
    public void show() {
        System.out.format("    Tarefa - %s [%s]\n", this.name, this.completed ? "x" : " ");
    }

    public LocalDate getDate() {
        return this.startDateTime.toLocalDate();
    }

    public void setDate(LocalDate date) {
        this.startDateTime = LocalDateTime.of(date, LocalTime.of(0, 0, 0, 0)); // começo do dia
        this.endDateTime = LocalDateTime.of(date, LocalTime.of(23, 59, 59, 999_999_999)); // fim do dia
    }

    public boolean isCompleted() {
        return completed;
    }
}
