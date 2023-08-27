package agendapages.menu;

import java.util.List;
import java.util.ArrayList;

public class Menu<T> {
    private List<Option<T>> options;
    private int startEnumeration;

    public Menu(int startEnumeration) {
        this.options = new ArrayList<>();
        this.startEnumeration = startEnumeration;
    }

    public void createOption(String option, Action<T> action) {
        this.addOption(new Option<>(option, action));
    }

    public void addOption(Option<T> option) {
        this.options.add(option);
    }

    public void show() {
        for (int i = 0; i < this.options.size(); i++) {
            System.out.format("[%d] - %s\n", this.startEnumeration + i, this.options.get(i).getOption());  
        }
    }

    public int getTotalOptions() {
        return this.options.size();
    }

    public void choose(int option, T element) {
        this.options.get(option - this.startEnumeration).useAction(element);
    }
}
