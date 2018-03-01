package com.mfg.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I309908 on 7/17/2017.
 */
@Service
public class PingService {

    public boolean pingJenkinsServer(String jsonBodyStr, StringBuffer ping_url) throws Exception{
        JsonParser jp = new JsonParser();
        JsonObject paraBodyObj = (JsonObject) jp.parse(jsonBodyStr);
        String userName = paraBodyObj.get("userName").getAsString();
        String encodedPassword = paraBodyObj.get("password").getAsString();
        String password =  RSAUtils.decryptStringFromBase64(encodedPassword);
        String server_url = paraBodyObj.get("url").getAsString();
        server_url = server_url + "/";

        ping_url.append(server_url).append("j_acegi_security_check");

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(ping_url.toString());

        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36.8");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("j_username", userName));
        urlParameters.add(new BasicNameValuePair("j_password", password));
        urlParameters.add(new BasicNameValuePair("from", "/"));
        urlParameters.add(new BasicNameValuePair("Submit", "log in"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);


        if (response.getStatusLine().getStatusCode() == 302 && response.getHeaders("Location") != null){
            String respLocation = response.getHeaders("Location")[0].getValue();
            if (respLocation.equals(server_url)){
                return true;
            }else if (respLocation.indexOf("Loginerror") < 0){
                return true;
            }
        }
        return false;
    }

    public boolean pingSonarServer(String jsonBodyStr, StringBuffer ping_url) throws Exception{
        JsonParser jp = new JsonParser();
        JsonObject paraBodyObj = (JsonObject) jp.parse(jsonBodyStr);
        String userName = paraBodyObj.get("userName").getAsString();
        String encodedPassword = paraBodyObj.get("password").getAsString();
        String password =  RSAUtils.decryptStringFromBase64(encodedPassword);
        String server_url = paraBodyObj.get("url").getAsString();
        if (!server_url.endsWith("/")){
            server_url = server_url + "/";
        }

        //since sonar version 6.0
        String newVersionSuffix = "api/authentication/login";
        //for old version
        String oldVersionSuffix = "sessions/login";

        ping_url.append(server_url).append(newVersionSuffix);

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(ping_url.toString());

        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("login", userName));
        urlParameters.add(new BasicNameValuePair("password", password));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == 200){
            return true;
        }else if (response.getStatusLine().getStatusCode() == 404){
            post.setURI(new URI(server_url+oldVersionSuffix));
            HttpResponse secondResponse = client.execute(post);
            if (secondResponse.getStatusLine().getStatusCode() == 302){
                return true;
            }
        }
        return false;
    }

}
