package agenda.activities;

import java.time.LocalDateTime;

public class Evento extends AgendaActivity {
    private String description;

    public Evento(String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(name, startDateTime, endDateTime);
        this.description = description;
    }

    @Override
    public void showStats() {
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
            this.formatStartTime("HH:mm:ss"),
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
}
