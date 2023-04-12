package robertorodrigues.curso.academicos.model;

import java.text.SimpleDateFormat;

public class DataAtual {
    public static String dataAtual() {
         long data = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = dateFormat.format(data);

        return dataString;
    }

    public static String mesAnoDataEscolhida(String data) {

        String retornoData[] = data.split("/");
        String dia = retornoData[0];//dia
        String mes = retornoData[1];//mes
        String ano = retornoData[2];//ano

        String mesAno = mes + ano;
        return mesAno;

    }
}
