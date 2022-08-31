package robertorodrigues.curso.academicos.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import robertorodrigues.curso.academicos.model.NotificacaoDados;

public interface NotificacaoService {

    @Headers({
            "Authorization:key=AAAA6XL-oEM:APA91bHsUQ33tpwKtxEjf18mReV16KNTcXi8gmwzT5Cm_agreAnT2yW5-571gA8OHgrSas6MhQggd_9rxuazD2PIjyj9xGX9a23YGudrsqRjj2GytQ2_-wDfsdTBwyJi95yMiBOp_kAD",
            "Content-Type:application/json"
    })
    @POST("send")
    Call<NotificacaoDados> salvarNotificacao(@Body NotificacaoDados notificacaoDados);

}
