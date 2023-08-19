package agenda.activities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import agenda.AgendaChronologicalOrderException;

public abstract class AgendaActivity implements Serializable {
    protected String name;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;

    public AgendaActivity(String name, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.name = name;
        this.setStartDateTime(startDateTime);
        this.setEndDateTime(endDateTime);
    }

    public abstract void showAttributes();
    public abstract void show();

    public static boolean checkDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return (
            startDateTime == null || endDateTime == null || // não existe um intervalo de início e fim
            startDateTime.toLocalDate().isBefore(endDateTime.toLocalDate()) || // data de início < data de término
            ( // horário de início <= horário de término do mesmo dia
                startDateTime.toLocalDate().isEqual(endDateTime.toLocalDate()) &&
                !startDateTime.toLocalTime().isAfter(endDateTime.toLocalTime())
            )
        );
    }

    // verifica se a data de início < data de término
    public static boolean checkDate(LocalDate startDate, LocalDate endDate) {
        return (
            startDate == null || endDate == null || // não existe um intervalo de início e fim
            startDate.isBefore(endDate)
        );
    }
    
    // verifica se o horário de início <= horário de término do mesmo dia
    public static boolean checkTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return (
            startDateTime == null || endDateTime == null || // não existe um intervalo de início e fim
            startDateTime.toLocalDate().isEqual(endDateTime.toLocalDate()) &&
            !startDateTime.toLocalTime().isAfter(endDateTime.toLocalTime())
        );
    }

    /* formata date/time */

    public String formatStartDateTime(String pattern) {
        return this.startDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String formatEndDateTime(String pattern) {
        return this.endDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /* getters e setters */

    public String getName() {
        return this.name;
    }

    public void setName(String nome) {
        this.name = nome;
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
