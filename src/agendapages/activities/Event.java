package agendapages.activities;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import agendapages.datetime.AgendaChronologicalOrderException;

public class Event extends AgendaActivity {
    private String description;

    public Event(String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
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

    public boolean checkDateTime() {
        return (
            this.startDateTime == null || this.endDateTime == null || // não existe um intervalo de início e fim
            this.startDateTime.toLocalDate().isBefore(this.endDateTime.toLocalDate()) || // data de início < data de término
            ( // horário de início <= horário de término do mesmo dia
                this.startDateTime.toLocalDate().isEqual(this.endDateTime.toLocalDate()) &&
                !this.startDateTime.toLocalTime().isAfter(this.endDateTime.toLocalTime())
            )
        );
    }

    /* getters e setters */

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
        if (this.checkDateTime()) {
            this.startDateTime = startDateTime;
        } else {
            throw new AgendaChronologicalOrderException(startDateTime + " não pode ocorrer depois de " + this.endDateTime);
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
        if (this.checkDateTime()) {
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
