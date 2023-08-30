package agenda.io;

public abstract class AgendaOutput {
    private static final int MAX_CHAR = 51;

    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    // divisória
    public static void section() {
        System.out.println("=".repeat(MAX_CHAR));
    }

    // divisória nomeada
    public static void section(String name) {
        int total = MAX_CHAR - name.length() - 2;
        int left = total / 2;
        int right = total - left;
        System.out.println("=".repeat(left) + " " + name + " " + "=".repeat(right));
    }

    // mostra uma mensagem de erro para o usuário
    public static void errorMessage(String message) {
        System.out.println(ANSI_RED + "    [?] " + message + ANSI_RESET);
    }
    
    // mostra uma mensagem de alerta para o usuário
    public static void warningMessage(String message) {
        System.out.println(ANSI_YELLOW + "    [!] " + message + ANSI_RESET);
    }
    
    // mostra uma mensagem que a operação realizada deu certo para o usuário
    public static void okMessage(String message) {
        System.out.println(ANSI_GREEN + "    " + message + ANSI_RESET);
    }

    // limpa a tela
    public static void clear() {
        try { new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static void jumpLine() {
        System.out.println();
    }
}
