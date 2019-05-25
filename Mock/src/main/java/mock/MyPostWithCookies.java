package mock;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MyPostWithCookies {
    //读取配置文件
    private ResourceBundle bundle = ResourceBundle.getBundle("myconfig", Locale.CHINA);
    //创建访问路径
    private String url;
    //Post请求方法
    private HttpPost post;
    //默认客户端
    private DefaultHttpClient client = new DefaultHttpClient();
    //响应信息
    private HttpResponse response;
    //创建CookieStore对象，存储cookies
    private CookieStore cookies;

    //获取cookies
    @Test
    public void postCookies() throws IOException {
        //访问路径
        url = bundle.getString("test.host") + bundle.getString("test.postcookies");
        //实例化post请求
        post = new HttpPost(url);
        //设置请求参数
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        param.add(new BasicNameValuePair("name", "jinjin"));
        param.add(new BasicNameValuePair("sex", "man"));
        //将请求参数放置post请求中
        post.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
        //客户端执行post请求、
        response = client.execute(post);
        //得到响应体
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("postcookies访问结果：" + result);
        //得到 CookieStore
        cookies = client.getCookieStore();
        List<Cookie> cookieList = this.cookies.getCookies();
        for (Cookie cookie : cookieList
        ) {
            System.out.println("cookie信息为：" + cookie.getName() + "=" + cookie.getValue());
        }
    }

    //post请求带cookies访问
    @Test(dependsOnMethods = "postCookies")
    public void postWithCookies() throws IOException {
        //设置访问路径
        url = bundle.getString("test.host") + bundle.getString("test.post.with.cookies");
        //创建post请求对象
        post = new HttpPost(url);
        //设置请求头信息，json格式参数
        post.setHeader("content-type", "application/json");
        //设置参数
        JSONObject param = new JSONObject();
        param.put("ssn", "jinjin");
        param.put("password", "1234");
        //将参数放入实体中
        StringEntity entity = new StringEntity(param.toString(), "utf-8");
        //将实体放入post请求
        post.setEntity(entity);
        //设置cookies
        client.setCookieStore(cookies);
        //客户端访问post请求,获取响应体
        response = client.execute(post);
        //将响应体转化为String类型
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("postwithcookies响应结果为：" + result);
        //将响应结果转成JSONObject对象
        JSONObject object = new JSONObject(result);
        //获取响应的json数据字符串
        String code = (String) (object.get("code"));
        String message = (String) (object.get("message"));
        //断言响应结果
        Assert.assertEquals("1", code);
        Assert.assertEquals("login success", message);

    }
}
