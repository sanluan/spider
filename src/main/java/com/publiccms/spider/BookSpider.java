package com.publiccms.spider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractBy.Source;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl("https://www.publiccms.com/novel/2015/08-10/\\d+.html")
public class BookSpider {
    private static String url = "http://127.0.0.1:8080/api/contentCreate";
    // added in management
    private static String appToken = "ed486c61-0231-4a07-a96d-25033973937b";
    // http://127.0.0.1:8080/api/login?username=admin&password=admin
    private static String authUserId = "1";
    private static String authToken = "10caa63d-9d55-4c55-9f5e-a29f0dc78665";
    private static String categoryId = "15";
    private static String modelId = "chapter";
    private static String parentId = "7226851730082762752";

    @ExtractBy(value = "//h1/a/text()", type = ExtractBy.Type.XPath, notNull = true, source = Source.RawText)
    private String title;
    @ExtractBy(value = "//div[@id=content]/html()", type = ExtractBy.Type.XPath, notNull = true, source = Source.RawHtml)
    private String content;

    public static void main(String[] args) {
        OOSpider.create(Site.me().setSleepTime(3000), (b, t) -> {
            try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                    .setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build()).build()) {
                HttpPost request = new HttpPost(url);
                List<NameValuePair> nvps = new ArrayList<>();
                nvps.add(new BasicNameValuePair("categoryId", categoryId));
                nvps.add(new BasicNameValuePair("modelId", modelId));
                nvps.add(new BasicNameValuePair("parentId", parentId));
                nvps.add(new BasicNameValuePair("title", ((BookSpider) b).title));
                nvps.add(new BasicNameValuePair("text", ((BookSpider) b).content));
                // old version
                // nvps.add(new BasicNameValuePair("appToken", appToken));
                // nvps.add(new BasicNameValuePair("authUserId",
                // authUserId));
                // nvps.add(new BasicNameValuePair("authToken", authToken));
                request.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
                request.addHeader("appToken", appToken);
                request.addHeader("authUserId", authUserId);
                request.addHeader("authToken", authToken);
                try (CloseableHttpResponse response = httpclient.execute(request)) {
                    HttpEntity entity = response.getEntity();
                    if (null != entity) {
                        System.out.println(EntityUtils.toString(entity, StandardCharsets.UTF_8));
                        EntityUtils.consume(entity);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, BookSpider.class).addUrl("https://www.publiccms.com/novel/2015/08-10/39.html").run();

    }
}