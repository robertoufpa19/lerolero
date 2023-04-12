package robertorodrigues.curso.academicos.model;

import java.text.SimpleDateFormat;

public class HoraAtual {

    public static String horaAtual() {
        long hora = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String horaString = dateFormat.format(hora);

        return horaString;
    }
}
