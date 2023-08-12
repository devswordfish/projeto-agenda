package agenda.activities;

import java.io.Serializable;
import java.time.LocalDateTime;
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

    public abstract void visualize();    

    public String getName() {
        return this.name;
    }

    public void setName(String nome) {
        this.name = nome;
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }

    public String formatStartDateTime(String pattern) {
        return this.startDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public void setStartDateTime(LocalDateTime dataInicio) {
        this.startDateTime = dataInicio;
    }

    public LocalDateTime getEndDateTime() {
        return this.endDateTime;
    }

    public String formatEndDateTime(String pattern) {
        return this.endDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public void setEndDateTime(LocalDateTime dataTermino) {
        this.endDateTime = dataTermino;
    }    
}
