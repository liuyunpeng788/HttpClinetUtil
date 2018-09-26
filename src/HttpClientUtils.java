
import com.fonova.stargazer.exception.StargazerException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ronnie on 2017/12/4 16:46.
 */
public class HttpClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger("HttpClientUtils");
    private final static int CONNECTION_TIMEOUT = 6000;
    private final static int SO_TIMEOUT = 6000;
    private final static int CONNECTION_REQ_TIMEOUT = 15000;
    /**
     * HttpClient Post 请求
     *
     * @param requestUrl 请求url
     * @param paramMap   请求参数
     * @return 返回信息
     */

    public static String requestPost(String requestUrl, Map<String, String> paramMap) {

        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            HttpPost httpPost = new HttpPost(requestUrl);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            if (paramMap != null && !paramMap.isEmpty())
                paramMap.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v)));
            if (!nameValuePairs.isEmpty()) {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            }

            //请求超时
            //setConfig,添加配置,如设置请求超时时间,连接超时时间
            RequestConfig reqConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_REQ_TIMEOUT).setSocketTimeout(SO_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT).build();
            httpPost.setConfig(reqConfig);

            LOGGER.info("Fas POST 请求地址:" + requestUrl + ",param = " + paramMap );
            CloseableHttpResponse response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {//如果状态码为200,就是正常返回
                String message = EntityUtils.toString(response.getEntity(),"utf-8");
                response.close();
                return message;
            }else{
                  LOGGER.info("返回码：" + response.getStatusLine().getStatusCode());
                  response.close();
                  return null;
            }
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * HttpClient Get 请求
     *
     * @param requestUrl 请求url
     * @param paramMap   请求参数
     * @return 返回信息
     */
    public static String requestGet(String requestUrl, Map<String, String> paramMap) throws Exception {
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(requestUrl);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            if (paramMap != null && !paramMap.isEmpty())
                paramMap.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v)));
            String str = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            httpGet.setURI(new URI(httpGet.getURI().toString() + "?" + str));


            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(20000).setConnectionRequestTimeout(1000)
                    .setSocketTimeout(15000).build();
            httpGet.setConfig(requestConfig);
            HttpResponse response = new DefaultHttpClient().execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info("Fas POST 请求地址:" + requestUrl + ",param = " + paramMap + ",responseCode => " + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                String message = EntityUtils.toString(response.getEntity());
//                LOGGER.info(message);
                return message;
            }
        } catch (IOException | URISyntaxException e) {
            throw new StargazerException("1001", "系统繁忙.请稍后再试.");
        }
        return null;
    }

}
