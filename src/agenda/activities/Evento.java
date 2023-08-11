package agenda.activities;

import java.time.LocalDateTime;

public class Evento extends AgendaActivity {
    private String description;

    public Evento(String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(name, startDateTime, endDateTime);
        this.description = description;
    }

    @Override
    public void visualize() {
        System.out.println("Evento");
        System.out.println("    Nome: " + this.name);
        System.out.println("    Descrição: " + this.description);
        System.out.println("    Início: " + this.formatStartDateTime("dd/MM/yyyy HH:mm:ss"));
        System.out.println("    Fim: " + this.formatEndDateTime("dd/MM/yyyy HH:mm:ss"));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
