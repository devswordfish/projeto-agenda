package agenda.menu;

@FunctionalInterface
public interface Action<T> {
    void run(T element);
}
