package agenda.activities;

import java.time.LocalDateTime;

public class Lembrete extends AgendaActivity {
    public Lembrete(String name, LocalDateTime dateTime) {
        super(name, dateTime, dateTime);
    }

    @Override
    public void showStats() {
        System.out.println("Lembrete:");
        System.out.println("    Nome..: " + this.name);
        System.out.println("    Data..: " + this.formatStartDateTime("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public void show() {
        System.out.format(
            "   Lembrete - %s (%s)\n",
            this.name,
            this.formatEndTime("HH:mm:ss")
        );
    }
}
