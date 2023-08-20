package agenda.activities;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import agenda.AgendaChronologicalOrderException;

public class Evento extends AgendaActivity {
    private String description;

    public Evento(String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(name);
        this.setStartDateTime(startDateTime);
        this.setEndDateTime(endDateTime);
        this.description = description;
    }

    @Override
    public void showAttributes() {
        System.out.println("Evento:");
        System.out.println("    Nome.......: " + this.name);
        System.out.println("    Descrição..: " + this.description);
        System.out.println("    Início.....: " + this.formatStartDateTime("dd/MM/yyyy HH:mm:ss"));
        System.out.println("    Término....: " + this.formatEndDateTime("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public void show() {
        System.out.format(
            "    Evento - %s (%s -> %s)\n",
            this.name,
            this.formatStartDateTime("HH:mm:ss"),
            this.formatEndDateTime("dd/MM/yyyy HH:mm:ss")
        );
        System.out.println("        - Descrição: " + this.description);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) throws AgendaChronologicalOrderException {
        if (AgendaActivity.checkDateTime(startDateTime, this.endDateTime)) {
            this.startDateTime = startDateTime;
        } else {
            throw new AgendaChronologicalOrderException(startDateTime + " não pode ocorrer antes de " + this.endDateTime);
        }
    }

    public LocalDate getStartDate() {
        return this.startDateTime.toLocalDate();
    }

    public void setStartDate(LocalDate startDate) {
        this.setStartDateTime(LocalDateTime.of(startDate, this.getStartTime()));
    }

    public LocalTime getStartTime() {
        return this.startDateTime.toLocalTime();
    }

    public void setStartTime(LocalTime startTime) {
        this.setStartDateTime(LocalDateTime.of(this.getStartDate(), startTime));
    }

    public LocalDateTime getEndDateTime() {
        return this.endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) throws AgendaChronologicalOrderException {
        if (AgendaActivity.checkDateTime(this.startDateTime, endDateTime)) {
            this.endDateTime = endDateTime;
        } else {
            throw new AgendaChronologicalOrderException(endDateTime + " não pode ocorrer antes de " + this.startDateTime);
        }
    }

    public LocalDate getEndDate() {
        return this.endDateTime.toLocalDate();
    }

    public void setEndDate(LocalDate endDate) {
        this.setEndDateTime(LocalDateTime.of(endDate, this.getEndTime()));
    }

    public LocalTime getEndTime() {
        return this.endDateTime.toLocalTime();
    }

    public void setEndTime(LocalTime endTime) {
        this.setEndDateTime(LocalDateTime.of(this.getEndDate(), endTime));
    }
}
