package agendapages.pages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import agendapages.activities.Task;

import agendapages.datetime.AgendaDateTime;

import agendapages.io.AgendaInput;
import agendapages.io.AgendaOutput;

import agendapages.menu.Menu;
import agendapages.menu.Option;
import agendapages.menu.OptionAction;

public class TaskPage extends Page<Task> {
    private static final Menu<Task> menuChange = new Menu<>(0);
    
    public TaskPage() {
        super("tarefas.txt");
        this.setUpMenuChange();
    }

    private void add(Task task) {
        LocalDate date = task.getDate();
        
        // binary search para achar a posição na qual a tarefa deve ser inserida (tarefa é sempre inserida por último nas tarefas referentes ao mesmo dia)
        int l = 0;
        int r = this.elements.size() - 1;

        while (l <= r) {
            int m = (l + r) / 2;

            LocalDate middleDate = this.elements.get(m).getDate();

            if (date.isBefore(middleDate)) r = m - 1;
            else                           l = m + 1;
        }

        this.elements.add(l, task);
    }

    @Override
    public void create() {
        // pega o nome da tarefa
        String name = AgendaInput.inputString("Digite o nome da tarefa: ");

        // pega a data de quando essa tarefa deve acontecer
        LocalDate date = AgendaInput.inputDate("Digite a data da tarefa (formato - dia/mês/ano): ");

        // verifica se a data informada já passou
        if (AgendaDateTime.hasDatePassedFromNow(date)) AgendaOutput.warningMessage("A data informada já passou");

        // cria a tarefa
        Task tarefa = new Task(name, date);

        this.add(tarefa);
        this.save();

        AgendaOutput.okMessage("Tarefa criada com sucesso!");
        tarefa.showAttributes();

        AgendaInput.holdScreen();
    }

    @Override
    public void cancel() {
        Menu<Task> menu = this.enumerate(task -> {
            this.elements.remove(task);
            this.save();

            AgendaOutput.okMessage("Tarefa deletada com sucesso!");

            AgendaInput.holdScreen();
        });

        if (menu != null) {
            menu.show();
            
            int option = AgendaInput.inputOption(0, this.elements.size());
            
            menu.chooseOptionAction(option, option == 0 ? null : this.elements.get(option - 1));
        } else {
            AgendaOutput.okMessage("Sem tarefas!");
            AgendaInput.holdScreen();
        }
    }

    @Override
    public void change() {
        // menu de escolha da tarefa
        Menu<Task> menuChoose = this.enumerate(task -> {
            AgendaOutput.section();

            task.showAttributes();

            AgendaOutput.jumpLine();

            menuChange.show();

            int option = AgendaInput.inputOption(0, menuChange.getTotalOptions());

            if (option == 0) return;

            AgendaOutput.section();

            menuChange.chooseOptionAction(option, task);

            this.elements.remove(task);
            this.add(task);

            this.save();

            AgendaOutput.okMessage("Tarefa alterada com sucesso!");
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
            
            for (Task task : this.elements) {
                LocalDate activityDate = task.getDate();

                if (!activityDate.equals((curDateTime))) {
                    if (curDateTime != null) System.out.println();
                    
                    curDateTime = activityDate;
                    
                    System.out.println("--- " + curDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                
                task.show();
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
        menuChange.addOption(new Option<>("Mudar nome", task -> {
            String name = AgendaInput.inputString("Digite o novo nome: ");
            task.setName(name);
        }));
        menuChange.addOption(new Option<>("Mudar data", task -> {
            LocalDate startDate = null;

            while (startDate == null) {
                startDate = AgendaInput.inputDate("Digite a nova data (formato - dia/mês/ano): ");

                task.setDate(startDate);
            }

            if (AgendaDateTime.hasDatePassedFromNow(startDate)) AgendaOutput.warningMessage("A data informada já passou");
        }));
    }
}
