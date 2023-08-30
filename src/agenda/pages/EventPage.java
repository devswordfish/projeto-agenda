package agenda.pages;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

import agenda.activities.Event;
import agenda.datetime.AgendaChronologicalOrderException;
import agenda.datetime.AgendaDateTime;
import agenda.io.AgendaInput;
import agenda.io.AgendaOutput;
import agenda.menu.Menu;
import agenda.menu.Option;

public class EventPage extends ActivityPage<Event> {
    private static final Menu<Event> menuChange = new Menu<>(0);

    public EventPage() {
        super("eventos.txt");
        this.setUpMenuChange();
    }

    private void add(Event event) {
        LocalDate date = event.getStartDate();
        
        // binary search para achar a posição na qual o evento deve ser inserido (evento é sempre inserido por último nos eventos referentes ao mesmo dia)
        int l = 0;
        int r = this.elements.size() - 1;

        while (l <= r) {
            int m = (l + r) / 2;

            LocalDate middleDate = this.elements.get(m).getStartDate();

            if (date.isBefore(middleDate)) r = m - 1;
            else                           l = m + 1;
        }

        this.elements.add(l, event);
    }

    private boolean checkEventCollision(LocalDateTime dateTime) {
        for (Event event : this.elements) {
            if (
                dateTime.isAfter(event.getStartDateTime()) &&
                dateTime.isBefore(event.getEndDateTime())
            ) {
                AgendaOutput.errorMessage("A data e hora informados entram em conflito com o evento \"" + event.getName() + "\"");
                return true;
            }
        }

        return false;
    }

    private boolean checkEventCollision(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        for (Event event : this.elements) {
            if (
                dateTime1.isBefore(event.getEndDateTime()) &&
                dateTime2.isAfter(event.getStartDateTime())
            ) {
                AgendaOutput.errorMessage("A data e hora informados entram em conflito com o evento \"" + event.getName() + "\"");
                return true;
            }
        }

        return false;
    }
    
    @Override
    public void create() {
        // pega o nome do evento
        String name = AgendaInput.inputString("Digite o nome do evento: ");

        // pega a descrição do evento
        String description = AgendaInput.inputString("Digite uma descrição do evento: ");

        AgendaOutput.jumpLine();

        LocalDate startDate = null;
        LocalTime startTime = null;
        LocalDateTime startDateTime = null;

        while (true) {
            // pega a data do início do evento
            startDate = AgendaInput.inputDate("Digite a data de início do evento: ");

            // verifica se a data informada já passou
            if (AgendaDateTime.hasDatePassedFromNow(startDate)) AgendaOutput.warningMessage("A data informada já passou");

            // pega o horário do início do evento
            startTime = AgendaInput.inputTime("Digite o horário de início do evento: ");
            
            // verifica se o horário informado já passou
            if (AgendaDateTime.hasTimePassedFromNow(startDate, startTime)) AgendaOutput.warningMessage("O horário informado já passou");

            startDateTime = LocalDateTime.of(startDate, startTime);

            if (!this.checkEventCollision(startDateTime)) break;
        }

        AgendaOutput.jumpLine();

        LocalDate endDate = null;
        LocalTime endTime = null;
        LocalDateTime endDateTime = null;

        while (true) {
            // pega a data do término do evento
            endDate = null;

            while (endDate == null) {
                endDate = AgendaInput.inputDate("Digite a data de término do evento: ");

                // trata o erro no qual o usuário pode digitar uma data de término antes do início do evento
                if (endDate.isBefore(startDate)) {
                    AgendaOutput.errorMessage("Data de término não pode ser antes da data de início");
                    endDate = null;
                }
            }

            // verifica se a data informada já passou
            if (AgendaDateTime.hasDatePassedFromNow(endDate)) AgendaOutput.warningMessage("A data informada já passou");

            // pega o horário do término do evento
            endTime = null;

            while (endTime == null) {
                endTime = AgendaInput.inputTime("Digite o horário de término do evento: ");
                
                // trata o erro no qual o usuário pode digitar um horário de término antes do horário do início evento do mesmo dia
                if (endDate.isEqual(startDate) && !endTime.isAfter(startTime)) {
                    AgendaOutput.errorMessage("Horário de término não pode ser o mesmo ou antes do horário de início");
                    endTime = null;
                }
            }

            // verifica se o horário informado já passou
            if (AgendaDateTime.hasTimePassedFromNow(endDate, endTime)) AgendaOutput.warningMessage("O horário informado já passou");

            endDateTime = LocalDateTime.of(endDate, endTime);

            if (!this.checkEventCollision(startDateTime, endDateTime)) break;
        }

        // cria o evento
        Event evento = new Event(name, description, startDateTime, endDateTime);

        this.add(evento);
        this.save();

        AgendaOutput.okMessage("Evento criado com sucesso!");
        evento.showAttributes();

        AgendaInput.holdScreen();
    }

    @Override
    public void cancel() {
        Menu<Event> menu = this.enumeratedElements(event -> {
            this.elements.remove(event);
            this.save();

            AgendaOutput.okMessage("Evento deletado com sucesso!");

            AgendaInput.holdScreen();
        });

        if (menu != null) {
            menu.show();
            
            int option = AgendaInput.inputOption(0, this.elements.size());
            
            menu.choose(option, option == 0 ? null : this.elements.get(option - 1));
        } else {
            AgendaOutput.okMessage("Sem eventos!");
            AgendaInput.holdScreen();
        }
    }

    @Override
    public void change() {
        // menu de escolha do evento
        Menu<Event> menuChoose = this.enumeratedElements(event -> {
            AgendaOutput.section();

            event.showAttributes();

            AgendaOutput.jumpLine();

            menuChange.show();

            int option = AgendaInput.inputOption(0, menuChange.getTotalOptions());

            if (option == 0) return;

            AgendaOutput.section();

            menuChange.choose(option, event);

            this.elements.remove(event);
            this.add(event);

            this.save();

            AgendaOutput.okMessage("Evento alterado com sucesso!");
            AgendaInput.holdScreen();
        });

        if (menuChoose != null) {
            menuChoose.show();

            int option = AgendaInput.inputOption(0, menuChoose.getTotalOptions());

            menuChoose.choose(option, option == 0 ? null : this.elements.get(option - 1));
        } else {
            AgendaOutput.okMessage("Sem eventos!");
            AgendaInput.holdScreen();
        }

    }

    @Override
    public void view() {
        if (this.elements.size() == 0) {
            AgendaOutput.okMessage("Sem eventos!");
        } else {
            LocalDate curDateTime = null;
            
            for (Event event : this.elements) {
                LocalDate activityDate = event.getStartDate();

                if (!activityDate.equals((curDateTime))) {
                    if (curDateTime != null) System.out.println();
                    
                    curDateTime = activityDate;
                    
                    System.out.println("--- " + curDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                
                event.show();
            }
        }

        AgendaInput.holdScreen();
    }

    @Override
    public List<Event> getByDate(LocalDate date) {
        return this.elements.stream().filter(event -> event.getStartDate().isEqual(date)).toList();
    }

    // cria o menu de alterações
    private void setUpMenuChange() {
        menuChange.addOption(new Option<>("Voltar", __ -> {}));
        menuChange.addOption(new Option<>("Mudar nome", event -> {
            String name = AgendaInput.inputString("Digite o novo nome: ");
            event.setName(name);
        }));
        menuChange.addOption(new Option<>("Mudar descrição", event -> {
            String description = AgendaInput.inputString("Digite a nova descrição: ");
            event.setDescription(description);
        }));
        menuChange.addOption(new Option<>("Mudar data de início", event -> {
            LocalDate startDate = null;

            while (startDate == null) {
                do {
                    startDate = AgendaInput.inputDate("Digite a nova data de início: ");
                } while (this.checkEventCollision(LocalDateTime.of(startDate, event.getStartTime()), event.getEndDateTime()));

                try {
                    event.setStartDate(startDate);
                } catch (AgendaChronologicalOrderException e) {
                    AgendaOutput.errorMessage("Data de início não pode ser depois da data de término");
                    startDate = null;
                }
            }

            if (AgendaDateTime.hasDatePassedFromNow(startDate)) AgendaOutput.warningMessage("A data informada já passou");
        }));
        menuChange.addOption(new Option<>("Mudar horário de início", event -> {
            LocalTime startTime = null;

            while (startTime == null) {
                do {
                    startTime = AgendaInput.inputTime("Digite o novo horário de início: ");
                } while (this.checkEventCollision(LocalDateTime.of(event.getStartDate(), startTime), event.getEndDateTime()));

                try {
                    event.setStartTime(startTime);
                } catch (AgendaChronologicalOrderException e) {
                    AgendaOutput.errorMessage("Horário de início não pode ser depois do horário de término");
                    startTime = null;
                }
            }

            if (AgendaDateTime.hasTimePassedFromNow(event.getStartDate(), startTime)) AgendaOutput.warningMessage("O horário informado já passou");
        }));
        menuChange.addOption(new Option<>("Mudar data de término", event -> {
            LocalDate endDate = null;

            while (endDate == null) {
                do {
                    endDate = AgendaInput.inputDate("Digite a nova data de término: ");
                } while (this.checkEventCollision(event.getStartDateTime(), LocalDateTime.of(endDate, event.getEndTime())));

                try {
                    event.setEndDate(endDate);
                } catch (AgendaChronologicalOrderException e) {
                    AgendaOutput.errorMessage("Data de término não pode ser antes da data de início");
                    endDate = null;
                }
            }

            if (AgendaDateTime.hasDatePassedFromNow(endDate)) AgendaOutput.warningMessage("A data informada já passou");
        }));
        menuChange.addOption(new Option<>("Mudar horário de término", event -> {
            LocalTime endTime = null;

            while (endTime == null) {
                do {
                    endTime = AgendaInput.inputTime("Digite o novo horário de término: ");
                } while (this.checkEventCollision(event.getStartDateTime(), LocalDateTime.of(event.getEndDate(), endTime)));

                try {
                    event.setEndTime(endTime);
                } catch (AgendaChronologicalOrderException e) {
                    AgendaOutput.errorMessage("Horário de término não pode ser antes do horário de início");
                    endTime = null;
                }
            }

            if (AgendaDateTime.hasTimePassedFromNow(event.getStartDate(), endTime)) AgendaOutput.warningMessage("O horário informado já passou");
        }));
    }
}
