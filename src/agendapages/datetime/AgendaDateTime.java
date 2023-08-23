package agendapages.datetime;

import java.time.LocalDate;
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class AgendaDateTime {
    // checa se o dia já passou
    public static boolean hasDatePassedFromNow(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    // checa se o tempo passou no mesmo dia
    public static boolean hasTimePassedFromNow(LocalDate date, LocalTime time) {
        return date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now());
    }

    public static LocalDate parseDate(String dateString) throws AgendaDateTimeFormatException {
        // possibilita que a data seja informada, omitindo-se um dos seus componentes
        LocalDate now = LocalDate.now();

        String[] shorthands = {
            "",                                             // dia, mês e ano
            "/" + now.getYear(),                            // mês e ano
            "/" + now.getMonthValue() + "/" + now.getYear() // dia
        };

        LocalDate date = null;
        boolean ok = false;

        // faz a análise da data digitada pelo usuário
        for (String shorthand : shorthands) {
            try {
                date = LocalDate.parse(
                    dateString + shorthand,
                    DateTimeFormatter.ofPattern("d/M/y")
                );

                ok = true;
                break;
            } catch (DateTimeParseException e) {}
        }

        if (!ok) {
            throw new AgendaDateTimeFormatException("Não foi possível converter a string \"" + dateString + "\"");
        }

        return date;
    }

    public static LocalTime parseTime(String timeString) throws AgendaDateTimeFormatException {
        // possibilita que o tempo possa ser informado, omitindo-se um de seus componentes
        String shorthands[] = {
            "",      // horas, minutos e segundos
            ":00",   // horas e minutos
            ":00:00" // horas
        };

        LocalTime time = null;
        boolean ok = false;

        // faz a análise do tempo
        for (String shorthand : shorthands) {
            try {
                time = LocalTime.parse(
                    timeString + shorthand,
                    DateTimeFormatter.ofPattern("H:m:s")
                );

                ok = true;
                break;
            } catch (DateTimeParseException e) {}
        }

        if (!ok) {
            throw new AgendaDateTimeFormatException("Não foi possível converter a string \"" + timeString + "\"");
        }

        return time;
    }
}
