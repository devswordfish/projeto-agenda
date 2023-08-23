package agendapages.activities;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.LocalDate;
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
}
