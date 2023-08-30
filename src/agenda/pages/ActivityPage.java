package agenda.pages;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import agenda.activities.AgendaActivity;
import agenda.menu.Action;
import agenda.menu.Menu;
import agenda.menu.Option;

public abstract class ActivityPage<T extends AgendaActivity> extends Page<T> {
    public ActivityPage(String file) {
        super(file);
    }

    public List<T> filter(Predicate<T> predicate) {
        return this.elements.stream().filter(predicate).toList();
    }

    public Menu<T> enumeratedElements(Action<T> action) {
        Menu<T> menu = null;

        if (this.elements.size() != 0) {
            menu = new Menu<T>(0);
            menu.createOption("Voltar", __ -> {});

            for (int i = 0; i < this.elements.size(); i++) {
                menu.addOption(
                    new Option<>(
                        this.elements.get(i).getName(),
                        action
                    )
                );
            }
        }

        return menu;
    }

    public Menu<T> enumeratedElements(List<T> elements, Action<T> action) {
        Menu<T> menu = null;

        if (elements.size() != 0) {
            menu = new Menu<T>(0);
            menu.createOption("Voltar", __ -> {});

            for (int i = 0; i < elements.size(); i++) {
                menu.addOption(
                    new Option<>(
                        elements.get(i).getName(),
                        action
                    )
                );
            }
        }

        return menu;
    }

    public abstract void create();
    public abstract void cancel();
    public abstract void change();
    public abstract void view();
    public abstract List<T> getByDate(LocalDate date);
}
