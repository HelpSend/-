package com.heu.cs.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heu.cs.conndb.ConnMongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Created by memgq on 2017/6/3.
 */
public class SMSApiDaoImpl implements SMSApiDao {
    //查账户信息的http地址
    private  final String URI_GET_USER_INFO = "https://sms.yunpian.com/v2/user/get.json";

    //智能匹配模板发送接口的http地址
    private  final String URI_SEND_SMS = "https://sms.yunpian.com/v2/sms/single_send.json";

    //发送语音验证码接口的http地址
    private  final String URI_SEND_VOICE = "https://voice.yunpian.com/v2/voice/send.json";

    //绑定主叫、被叫关系的接口http地址
    private  final String URI_SEND_BIND = "https://call.yunpian.com/v2/call/bind.json";

    //解绑主叫、被叫关系的接口http地址
    private  final String URI_SEND_UNBIND = "https://call.yunpian.com/v2/call/unbind.json";

    //编码格式。发送编码格式统一用UTF-8
    private  final String ENCODING = "UTF-8";

    private final String URI_GET_REPLY="https://sms.yunpian.com/v2/sms/get_reply.json";
    //apikey
    private final String apikey=getApikey();



    private String getApikey(){
        ConnMongoDB connMongoDB=new ConnMongoDB();
        MongoCollection collection=connMongoDB.getCollection("bbddb","secret");
        Document filter=new Document("info","smsapikey");
        MongoCursor<Document> cursor=collection.find(filter).iterator();
        Document d=cursor.next();
        cursor.close();
        connMongoDB.getMongoClient().close();
        return d.getString("apikey");
    }



    @Override
    public String getReplyInfo(){

        MultivaluedMap formData=new MultivaluedHashMap();
        String ak=getApikey();
        formData.add("apikey", ak);
        formData.add("start_time", "2017-06-10 00:00:00");
        formData.add("end_time", "2017-06-15 11:20:00");
        formData.add("page_num", "1");
        formData.add("page_size", "80");
        MultivaluedMap head=new MultivaluedHashMap();
        head.add("Accept","application/json");
        head.add("Content-Type","application/x-www-form-urlencoded");

        JerseyClientBuilder jerseyClientBuilder=new JerseyClientBuilder();
        Client client=jerseyClientBuilder.build();
        WebTarget target=client.target(URI_GET_REPLY);
        target.request().headers(head);
        Response response = target.request().buildPost(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED)).invoke();
        System.out.println(response.readEntity(String.class));

        return "";
    }


    /**
     * 取账户信息
     *
     * @return json格式字符串
     * @throws java.io.IOException
     */

    @Override
    public  String getUserInfo() throws IOException, URISyntaxException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        return post(URI_GET_USER_INFO, params);
    }

    /**
     * 智能匹配模板接口发短信
     *
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */

    @Override
    public  String sendSms(String text, String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }



    /**
     * 通过接口发送语音验证码
     * @param apikey apikey
     * @param mobile 接收的手机号
     * @param code   验证码
     * @return
     */

    @Override
    public  String sendVoice(String apikey, String mobile, String code) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("mobile", mobile);
        params.put("code", code);
        return post(URI_SEND_VOICE, params);
    }

    /**
     * 通过接口绑定主被叫号码
     * @param apikey apikey
     * @param from 主叫
     * @param to   被叫
     * @param duration 有效时长，单位：秒
     * @return
     */

    @Override
    public  String bindCall(String apikey, String from, String to, Integer duration) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("from", from);
        params.put("to", to);
        params.put("duration", String.valueOf(duration));
        return post(URI_SEND_BIND, params);
    }

    /**
     * 通过接口解绑绑定主被叫号码
     * @param apikey apikey
     * @param from 主叫
     * @param to   被叫
     * @return
     */
    @Override
    public  String unbindCall(String apikey, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("from", from);
        params.put("to", to);
        return post(URI_SEND_UNBIND, params);
    }

    /**
     * 基于HttpClient 4.3的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */

    @Override
    public  String post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, ENCODING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }




}
