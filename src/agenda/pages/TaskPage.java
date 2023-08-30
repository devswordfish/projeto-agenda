package agenda.pages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;

import agenda.activities.Task;
import agenda.datetime.AgendaDateTime;
import agenda.io.AgendaInput;
import agenda.io.AgendaOutput;
import agenda.menu.Menu;
import agenda.menu.Option;

public class TaskPage extends ActivityPage<Task> {
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
        LocalDate date = AgendaInput.inputDate("Digite a data da tarefa: ");

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
        Menu<Task> menu = this.enumeratedElements(task -> {
            this.elements.remove(task);
            this.save();

            AgendaOutput.okMessage("Tarefa deletada com sucesso!");

            AgendaInput.holdScreen();
        });

        if (menu != null) {
            menu.show();
            
            int option = AgendaInput.inputOption(0, this.elements.size());
            
            menu.choose(option, option == 0 ? null : this.elements.get(option - 1));
        } else {
            AgendaOutput.okMessage("Sem tarefas!");
            AgendaInput.holdScreen();
        }
    }

    @Override
    public void change() {
        // menu de escolha da tarefa
        Menu<Task> menuChoose = this.enumeratedElements(task -> {
            AgendaOutput.section();

            task.showAttributes();

            AgendaOutput.jumpLine();

            menuChange.show();

            int option = AgendaInput.inputOption(0, menuChange.getTotalOptions());

            if (option == 0) return;

            AgendaOutput.section();

            menuChange.choose(option, task);

            this.elements.remove(task);
            this.add(task);

            this.save();

            AgendaOutput.okMessage("Tarefa alterada com sucesso!");
            AgendaInput.holdScreen();
        });

        if (menuChoose != null) {
            menuChoose.show();

            int option = AgendaInput.inputOption(0, menuChoose.getTotalOptions());

            menuChoose.choose(option, option == 0 ? null : this.elements.get(option - 1));
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

    public void markTaskDone() {
        List<Task> filteredList = this.filter(task -> !task.isCompleted());
        
        if (filteredList.size() != 0) {
            Menu<Task> menu = null;
            int option = -1;

            while (filteredList.size() != 0 && option != 0) {
                menu = this.enumeratedElements(
                    filteredList,
                    task -> {
                        task.complete();
                        this.save();
                    }
                );

                menu.show();

                option = AgendaInput.inputOption(0, menu.getTotalOptions());

                menu.choose(option, option == 0 ? null : filteredList.get(option - 1));

                filteredList = this.filter(task -> !task.isCompleted());
            }
        } else {   
            AgendaOutput.okMessage("Sem tarefas!");
            AgendaInput.holdScreen();
        }
    }

    public void markTaskNotDone() {
        List<Task> filteredList = this.filter(task -> task.isCompleted());
        
        if (filteredList.size() != 0) {
            Menu<Task> menu = null;
            int option = -1;

            while (filteredList.size() != 0 && option != 0) {
                menu = this.enumeratedElements(
                    filteredList,
                    task -> {
                        task.uncomplete();
                        this.save();
                    }
                );

                menu.show();

                option = AgendaInput.inputOption(0, menu.getTotalOptions());

                menu.choose(option, option == 0 ? null : filteredList.get(option - 1));

                filteredList = this.filter(task -> task.isCompleted());
            }
        } else {   
            AgendaOutput.okMessage("Sem tarefas!");
            AgendaInput.holdScreen();
        }
    }

    @Override
    public List<Task> getByDate(LocalDate date) {
        return this.filter(task -> task.getDate().isEqual(date));
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
                startDate = AgendaInput.inputDate("Digite a nova data: ");

                task.setDate(startDate);
            }

            if (AgendaDateTime.hasDatePassedFromNow(startDate)) AgendaOutput.warningMessage("A data informada já passou");
        }));
    }
}
