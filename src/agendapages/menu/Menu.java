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

    public void addOption(Option<T> option) {
        this.options.add(option);
    }

    public void show() {
        int n = this.startEnumeration;
        for (int i = 0; i < this.options.size(); i++) {
            System.out.format("[%d] - %s\n", n++, this.options.get(i).getOption());  
        }
    }

    public int getTotalOptions() {
        return this.options.size();
    }

    public void chooseOptionAction(int option, T element) {
        this.options.get(option - this.startEnumeration).useAction(element);
    }
}
