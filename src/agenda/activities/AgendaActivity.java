package agenda.activities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class AgendaActivity implements Serializable {
    protected String name;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;

    public AgendaActivity(String name, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.name = name;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public abstract void showStats();
    public abstract void show();

    /* formata date/time */

    public String formatStartDateTime(String pattern) {
        return this.startDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String formatStartDate(String pattern) {
        return this.getStartDate().format(DateTimeFormatter.ofPattern(pattern));
    }

    public String formatStartTime(String pattern) {
        return this.getStartTime().format(DateTimeFormatter.ofPattern(pattern));
    }
    
    public String formatEndDateTime(String pattern) {
        return this.endDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String formatEndDate(String pattern) {
        return this.getEndDate().format(DateTimeFormatter.ofPattern(pattern));
    }

    public String formatEndTime(String pattern) {
        return this.getEndTime().format(DateTimeFormatter.ofPattern(pattern));
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

    public LocalDate getStartDate() {
        return LocalDate.of(
            this.startDateTime.getYear(),
            this.startDateTime.getMonthValue(),
            this.startDateTime.getDayOfMonth()
        );
    }

    public LocalTime getStartTime() {
        return LocalTime.of(
            this.startDateTime.getHour(),
            this.startDateTime.getMinute(),
            this.startDateTime.getSecond()
        );
    }

    public void setStartDateTime(LocalDateTime dataInicio) {
        this.startDateTime = dataInicio;
    }

    public LocalDateTime getEndDateTime() {
        return this.endDateTime;
    }

    public LocalDate getEndDate() {
        return LocalDate.of(
            this.endDateTime.getYear(),
            this.endDateTime.getMonthValue(),
            this.endDateTime.getDayOfMonth()
        );
    }

    public LocalTime getEndTime() {
        return LocalTime.of(
            this.endDateTime.getHour(),
            this.endDateTime.getMinute(),
            this.endDateTime.getSecond()
        );
    }

    public void setEndDateTime(LocalDateTime dataTermino) {
        this.endDateTime = dataTermino;
    }    
}
