import org.radrso.plugins.exceptions.impl.RequestException;
import org.radrso.plugins.requests.Request;
import org.radrso.plugins.requests.RequestFactory;
import org.radrso.plugins.requests.entity.Method;
import org.radrso.plugins.requests.entity.Response;

/**
 * Created by raomengnan on 16-12-11.
 */
public class Test {
    public static void main(String[] args) throws RequestException {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=xxx&secret=xxx";
        RequestFactory requestFactory = new RequestFactory(url,
                Method.GET, null, null, false);

        Request request = requestFactory.createRequest("http");
        try {
            Response response = request.sendRequest();
            System.out.println(response.getContent());
            System.out.println(response.getContentType());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
