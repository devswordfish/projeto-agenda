package agenda.activities;

import java.time.LocalDateTime;

public class Lembrete extends AgendaActivity {
    public Lembrete(String name, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(name, startDateTime, endDateTime);
    }

    @Override
    public void show() {
        System.out.println("Lembrete:");
        System.out.println("    Nome..: " + this.name);
        System.out.println("    Data..: " + this.formatStartDateTime("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public void showOneLine() {
        System.out.format(
            "    Lembrete - %s : %s\n",
            this.name,
            this.formatEndTime("HH:mm:ss")
        );
    }
}
