package agendapages.activities;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reminder extends AgendaActivity {
    public Reminder(String name, LocalDateTime dateTime) {
        super(name);
        this.setDateTime(dateTime);
    }

    @Override
    public void showAttributes() {
        System.out.println("Lembrete:");
        System.out.println("    Nome..: " + this.name);
        System.out.println("    Data..: " + this.formatStartDateTime("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public void show() {
        System.out.format(
            "    Lembrete - %s (%s)\n",
            this.name,
            this.formatEndDateTime("HH:mm:ss")
        );
    }

    public LocalDateTime getDateTime() {
        return this.startDateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.startDateTime = dateTime;
        this.endDateTime = dateTime;
    }

    public LocalDate getDate() {
        return this.startDateTime.toLocalDate();
    }

    public void setDate(LocalDate date) {
        this.setDateTime(LocalDateTime.of(date, this.getTime()));
    }

    public LocalTime getTime() {
        return this.startDateTime.toLocalTime();
    }

    public void setTime(LocalTime time) {
        this.setDateTime(LocalDateTime.of(this.getDate(), time));
    }
}
