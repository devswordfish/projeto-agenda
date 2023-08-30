package agenda;

import java.util.Map;

import agenda.activities.AgendaActivity;
import agenda.io.AgendaInput;
import agenda.io.AgendaOutput;
import agenda.menu.Menu;
import agenda.pages.EventPage;
import agenda.pages.Page;
import agenda.pages.ReminderPage;
import agenda.pages.TaskPage;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Agenda {
    // páginas da agenda
    private static enum PAGE { TASK, REMINDER, EVENT };
    
    // menus da agenda
    private static enum MENU { MAIN, CREATION, CHANGE, CANCEL, VIEW };

    private Map<MENU, Menu<Void>> menus;
    private Map<PAGE, Page<? extends AgendaActivity>> pages;

    private Menu<Void> currentMenu;
    private String menuName;
    private boolean continueInAgenda;

    public Agenda() {
        this.setUpMenus();
        this.setUpPages();
        
        this.setCurrentMenu(MENU.MAIN);
        this.continueInAgenda = true;
    }

    /* métodos de configurações */

    private void setUpMenus() {
        // menu principal
        Menu<Void> mMain = new Menu<>(0);
        mMain.createOption("Sair", __ -> this.finish());
        mMain.createOption("Criar", __ -> this.setCurrentMenu(MENU.CREATION));
        mMain.createOption("Alterar", __ -> this.setCurrentMenu(MENU.CHANGE));
        mMain.createOption("Cancelar", __ -> this.setCurrentMenu(MENU.CANCEL));
        mMain.createOption("Visualizar", __ -> this.setCurrentMenu(MENU.VIEW));
        mMain.createOption("Hoje", __ -> this.viewAllByDate(LocalDate.now()));

        // menu de criação
        Menu<Void> mCreate = new Menu<>(0);
        mCreate.createOption("Voltar", __ -> this.setCurrentMenu(MENU.MAIN));
        mCreate.createOption("Criar evento", __ -> this.getEventPage().create());
        mCreate.createOption("Criar lembrete", __ -> this.getReminderPage().create());
        mCreate.createOption("Criar tarefa", __ -> this.getTaskPage().create());

        // menu de alteração
        Menu<Void> mChange = new Menu<>(0);
        mChange.createOption("Voltar", __ -> this.setCurrentMenu(MENU.MAIN));
        mChange.createOption("Alterar evento", __ -> this.getEventPage().change());
        mChange.createOption("Alterar lembrete", __ -> this.getReminderPage().change());
        mChange.createOption("Alterar tarefa", __ -> this.getTaskPage().change());

        // menu de cancelamento
        Menu<Void> mCancelation = new Menu<>(0);
        mCancelation.createOption("Voltar", __ -> this.setCurrentMenu(MENU.MAIN));
        mCancelation.createOption("Cancelar evento", __ -> this.getEventPage().cancel());
        mCancelation.createOption("Cancelar lembrete", __ -> this.getReminderPage().cancel());
        mCancelation.createOption("Cancelar tarefa", __ -> this.getTaskPage().cancel());

        // menu de visualização
        Menu<Void> mVisualization = new Menu<>(0);
        mVisualization.createOption("Voltar", __ -> this.setCurrentMenu(MENU.MAIN));
        mVisualization.createOption("Ver eventos", __ -> this.getEventPage().view());
        mVisualization.createOption("Ver lembretes", __ -> this.getReminderPage().view());
        mVisualization.createOption("Ver tarefas", __ -> this.getTaskPage().view());
        mVisualization.createOption("Marcar tarefa como feita", __ -> this.getTaskPage().markTaskDone());
        mVisualization.createOption("Marcar tarefa como não feita", __ -> this.getTaskPage().markTaskNotDone());
        mVisualization.createOption("Filtrar por data", __ -> this.viewByDate());

        this.menus = new HashMap<>();

        // salva os menus
        this.menus.put(MENU.MAIN, mMain);
        this.menus.put(MENU.CREATION, mCreate);
        this.menus.put(MENU.CHANGE, mChange);
        this.menus.put(MENU.CANCEL, mCancelation);
        this.menus.put(MENU.VIEW, mVisualization);
    }

    private void setUpPages() {
        this.pages = new HashMap<>();

        this.pages.put(PAGE.TASK, new TaskPage());
        this.pages.put(PAGE.REMINDER, new ReminderPage());
        this.pages.put(PAGE.EVENT, new EventPage());
    }

    // começo do programa
    public void start() {
        // mostra as opções referente ao menu atual, então o usuário escolhe uma opção cuja ação é, logo, executada
        while (continueInAgenda) {
            AgendaOutput.clear();
            this.showTitle();

            AgendaOutput.section(this.menuName);

            this.currentMenu.show();
            
            System.out.println();
            int option = AgendaInput.inputOption(0, this.currentMenu.getTotalOptions());

            AgendaOutput.section();

            this.currentMenu.choose(option, null);
        }

        AgendaOutput.clear();
    }

    private void finish() {
        continueInAgenda = false;
    }

    // titulo do programa
    private void showTitle() {
        AgendaOutput.section();
        System.out.println("   +-----+");
        System.out.println("   |  :  | +-----  +----- |\\    | +--.   +-----+");
        System.out.println("   |  :  | |       |      | \\   | |   \\  |  .  |");
        System.out.println("   +-----+ |  ---+ +---   |  \\  | | :  | |_____|");
        System.out.println("   |     | |     | |      |   \\ | |   /  |     |");
        System.out.println("   |     | +-----+ +----- |    \\| +--°   |     |");
    }

    private void viewByDate() {
        LocalDate date = AgendaInput.inputDate("Digite a data para filtrar: ");

        this.viewAllByDate(date);
    }

    private void viewAllByDate(LocalDate date) {
        List<AgendaActivity> activities = new ArrayList<>();

        activities.addAll(this.getEventPage().getByDate(date));
        activities.addAll(this.getReminderPage().getByDate(date));
        activities.addAll(this.getTaskPage().getByDate(date));

        if (activities.size() == 0) {
            AgendaOutput.okMessage("Sem eventos/lembretes/tarefas!");
        } else {
            System.out.println("--- " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            activities.forEach(activity -> activity.show());
        }

        AgendaInput.holdScreen();
    }

    /* getters e setters */

    private String getMenuName(MENU menu) {
        switch (menu) {
            case MAIN: return "PRINCIPAL";
            case CREATION: return "CRIAR";
            case CHANGE: return "ALTERAR";
            case CANCEL: return "CANCELAR";
            case VIEW: return "VISUALIZAR";
        }
        return "";
    }


    private TaskPage getTaskPage() {
        return (TaskPage) this.pages.get(PAGE.TASK);
    }

    private ReminderPage getReminderPage() {
        return (ReminderPage) this.pages.get(PAGE.REMINDER);
    }

    private EventPage getEventPage() {
        return (EventPage) this.pages.get(PAGE.EVENT);
    }

    private void setCurrentMenu(MENU menuName) {
        this.menuName = this.getMenuName(menuName);
        this.currentMenu = this.menus.get(menuName);
    }
}
