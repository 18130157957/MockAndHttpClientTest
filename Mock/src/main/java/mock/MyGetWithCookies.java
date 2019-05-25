package mock;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MyGetWithCookies {
    //定义CookieStore，用于存储获取的cookies信息
    private CookieStore cookies;
    //创建ResourceBundle对象，读取配置信息
    private ResourceBundle bundle = ResourceBundle.getBundle("myconfig", Locale.CHINA);
    //访问的路径
    private String url;
    //get请求对象
    private HttpGet get;
    //默认访问服务器的客户端
    private DefaultHttpClient client = new DefaultHttpClient();
    //客户端响应信息
    private HttpResponse response;

    @Test
    //获取cookies方法
    public void getCookies() throws IOException {
        //访问路径
        url = bundle.getString("test.host") + bundle.getString("test.getcookies")+"?username=zhangsan&password=1234";
        get = new HttpGet(url);
        //客户端以get请求访问服务器
        response = client.execute(get);
        //将响应体转为String对象
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("获取cookies：" + result);
        //从客户端获取CookieStore对象
        cookies = client.getCookieStore();
        //从CookieStore对象得到cookies集合
        List<Cookie> cookiesList = cookies.getCookies();
        //遍历cookies集合得到cookies的key=value键值对
        for (Cookie cookie : cookiesList
        ) {
            System.out.println("cookies信息--" + cookie.getName() + "=" + cookie.getValue());
        }
    }

    @Test(dependsOnMethods = "getCookies")
    //携带cookies信息的get请求访问服务器
    public void getWithCookies() throws IOException {
        url = bundle.getString("test.host") + bundle.getString("test.get.with.cookies");
        get = new HttpGet(url);
        //给访问服务器的客户端设置cookies信息
        client.setCookieStore(cookies);
        response = client.execute(get);
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        if (response.getStatusLine().getStatusCode() == 200) {
            System.out.println("带cookies访问：" + result);
        }
    }
}
