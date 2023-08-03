package agenda;

import java.util.Date;

public class Tarefa extends AtividadeAgenda {
    public Tarefa(long id, String nome, Date dataInicio, Date dataTermino) {
        super(id, nome, dataInicio, dataTermino);
    }
}
