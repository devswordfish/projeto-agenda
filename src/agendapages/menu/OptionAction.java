package agendapages.menu;

@FunctionalInterface
public interface OptionAction<T> {
    void run(T element);
}
