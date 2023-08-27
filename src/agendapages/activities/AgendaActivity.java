package agendapages.activities;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AgendaActivity implements Serializable {
    protected String name;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;

    public AgendaActivity(String name) {
        this.name = name;
    }

    public abstract void showAttributes();
    public abstract void show();

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
}
