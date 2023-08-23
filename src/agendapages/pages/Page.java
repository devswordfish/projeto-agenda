package agendapages.pages;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Page<T> {
    protected static Scanner scan = new Scanner(System.in);
    protected List<T> elements;
    private String file;

    public Page(String file) {
        this.file = file;
        this.load();
    }

    /* métodos auxiliares */

    protected void save() {
        try (FileOutputStream outputFile = new FileOutputStream(this.file)) {
            ObjectOutputStream outputStream = new ObjectOutputStream(outputFile);
            outputStream.writeObject(elements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected void load() {
        try (FileInputStream inputFile = new FileInputStream(this.file)) {
            ObjectInputStream inputStream = new ObjectInputStream(inputFile);
            this.elements = (ArrayList<T>) inputStream.readObject();
        } catch (Exception e) {
            this.elements = new ArrayList<>();
        }
    }

    /* métodos principais */

    public abstract void create();
    public abstract void cancel();
    public abstract void change();
    public abstract void view();
}
