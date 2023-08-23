package agendapages.pages;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import agendapages.activities.Reminder;

import agendapages.datetime.AgendaDateTime;

import agendapages.io.AgendaInput;
import agendapages.io.AgendaOutput;

import agendapages.menu.Menu;
import agendapages.menu.Option;
import agendapages.menu.OptionAction;

public class ReminderPage extends Page<Reminder> {
    private static final Menu<Reminder> menuChange = new Menu<>(0);
    
    public ReminderPage() {
        super("lembretes.txt");
        this.setUpMenuChange();
    }

    private void add(Reminder reminder) {
        LocalDate date = reminder.getDate();
        
        // binary search para achar a posição na qual o lembrete deve ser inserido (lembrete é sempre inserido por último nos lembretes referentes ao mesmo dia)
        int l = 0;
        int r = this.elements.size() - 1;

        while (l <= r) {
            int m = (l + r) / 2;

            LocalDate middleDate = this.elements.get(m).getDate();

            if (date.isBefore(middleDate)) r = m - 1;
            else                           l = m + 1;
        }

        this.elements.add(l, reminder);
    }

    @Override
    public void create() {
        // pega o nome do lembrete
        String name = AgendaInput.inputString("Digite o nome do lembrete: ");

        // pega a data de quando o lembrete deve ser ativado
        LocalDate date = AgendaInput.inputDate("Digite a data do lembrete (formato - dia/mês/ano): ");

        // verifica se a data informada já passou
        if (AgendaDateTime.hasDatePassedFromNow(date)) AgendaOutput.warningMessage("A data informada já passou");

        // pega o horário de quando o lembrete deve ser ativado
        LocalTime time = AgendaInput.inputTime("Digite o horário do lembrete (formato - horas:minutos:segundos): ");

        // verifica se o horário informado já passou
        if (AgendaDateTime.hasTimePassedFromNow(date, time)) AgendaOutput.warningMessage("O horário informado já passou");

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // cria o lembrete
        Reminder lembrete = new Reminder(name, dateTime);

        this.add(lembrete);
        this.save();

        AgendaOutput.okMessage("Lembrete criado com sucesso!");
        lembrete.showAttributes();

        AgendaInput.holdScreen();
    }

    @Override
    public void cancel() {
        Menu<Reminder> menu = this.enumerate(reminder -> {
            this.elements.remove(reminder);
            this.save();

            AgendaOutput.okMessage("Lembrete deletado com sucesso!");

            AgendaInput.holdScreen();
        });

        if (menu != null) {
            menu.show();
            
            int option = AgendaInput.inputOption(0, this.elements.size());
            
            menu.chooseOptionAction(option, option == 0 ? null : this.elements.get(option - 1));
        } else {
            AgendaOutput.okMessage("Sem lembretes!");
            AgendaInput.holdScreen();
        }
    }

    @Override
    public void change() {
        // menu de escolha do lembrete
        Menu<Reminder> menuChoose = this.enumerate(reminder -> {
            AgendaOutput.section();

            reminder.showAttributes();

            AgendaOutput.jumpLine();

            menuChange.show();

            int option = AgendaInput.inputOption(0, menuChange.getTotalOptions());

            if (option == 0) return;

            AgendaOutput.section();

            menuChange.chooseOptionAction(option, reminder);

            this.elements.remove(reminder);
            this.add(reminder);

            this.save();

            AgendaOutput.okMessage("Lembrete alterado com sucesso!");
            AgendaInput.holdScreen();
        });

        if (menuChoose != null) {
            menuChoose.show();

            int option = AgendaInput.inputOption(0, menuChoose.getTotalOptions());

            menuChoose.chooseOptionAction(option, option == 0 ? null : this.elements.get(option - 1));
        } else {
            AgendaOutput.okMessage("Sem tarefas!");
            AgendaInput.holdScreen();
        }

    }

    @Override
    public void view() {
        if (this.elements.size() == 0) {
            AgendaOutput.okMessage("Sem tarefas!");
        } else {
            LocalDate curDateTime = null;
            
            for (Reminder reminder : this.elements) {
                LocalDate activityDate = reminder.getDate();

                if (!activityDate.equals((curDateTime))) {
                    if (curDateTime != null) System.out.println();
                    
                    curDateTime = activityDate;
                    
                    System.out.println("--- " + curDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                
                reminder.show();
            }
        }

        AgendaInput.holdScreen();
    }

    // cria um menu dinâmico
    private <T> Menu<T> enumerate(OptionAction<T> optionAction) {
        Menu<T> menu = null;
    
        if (this.elements.size() != 0) {
            menu = new Menu<T>(0);
            menu.addOption(new Option<T>("Voltar", __ -> {}));

            for (int i = 0; i < this.elements.size(); i++) {
                menu.addOption(
                    new Option<T>(
                        this.elements.get(i).getName(),
                        optionAction
                    )
                );
            }
        }

        return menu;
    }

    // cria o menu de alterações
    private void setUpMenuChange() {
        menuChange.addOption(new Option<>("Voltar", __ -> {}));
        menuChange.addOption(new Option<>("Mudar nome", reminder -> {
            String name = AgendaInput.inputString("Digite o novo nome: ");
            reminder.setName(name);
        }));
        menuChange.addOption(new Option<>("Mudar data", reminder -> {
            LocalDate date = null;

            while (date == null) {
                date = AgendaInput.inputDate("Digite a nova data (formato - dia/mês/ano): ");

                reminder.setDate(date);
            }

            if (AgendaDateTime.hasDatePassedFromNow(date)) AgendaOutput.warningMessage("A data informada já passou");
        }));
        menuChange.addOption(new Option<>("Mudar horário", reminder -> {
            LocalTime time = null;

            while (time == null) {
                time = AgendaInput.inputTime("Digite o novo horário (formato - horas:minutos:segundos): ");

                reminder.setTime(time);
            }

            if (AgendaDateTime.hasTimePassedFromNow(reminder.getDate(), time)) AgendaOutput.warningMessage("O horário informado já passou");
        }));    
    }
}
