package com.mfg.Service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mfg.Exception.ParseErrorException;
import com.mfg.Exception.RequestErrorException;
import com.mfg.config.Constants;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.ProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;

/**
 * Created by I309908 on 4/21/2017.
 */
@Component
public class WebUtils {
    static final protected String  keyStoreTypePKCS12 = "PKCS12";
    static final protected String protocol           = "TLS";
    static  protected SSLSocketFactory factory  = null;

    public String httpsGet(String url) throws Exception{
//        String urlBase = "https://support.wdf.sap.corp/sap/bc/devdb/internal_incid?sap-client=001";
//        System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_111\\jre\\lib\\security\\cacerts");
        createSSLConn(Constants.p12File_Path,Constants.p12Password);
        HttpsURLConnection.setDefaultSSLSocketFactory(factory);
        URL obj = new URL(url.toString());
        HttpsURLConnection conn = (HttpsURLConnection)obj.openConnection();


        //add request header
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        // Send post request
        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }


    public static void createSSLConn(String p12File, String p12Password) throws Exception{
        KeyStore keystore = null;
        SSLContext sslCtxt  = null;
        KeyManagerFactory kmf = null;

        keystore = KeyStore.getInstance(keyStoreTypePKCS12);
        FileInputStream inputStream = new FileInputStream(p12File);
        keystore.load(inputStream, p12Password.toCharArray());

        sslCtxt = SSLContext.getInstance(protocol);
        kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        kmf.init(keystore,p12Password.toCharArray());
        sslCtxt.init(kmf.getKeyManagers(),null,null);

        factory = sslCtxt.getSocketFactory();
    }

    //will be used in next function
    public WebTarget getTarget(String url, String userName, String password) {
        ClientConfig clientConfig = new ClientConfig();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(userName, password);
        clientConfig.register(feature);
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget webTarget = client.target(url);

        return webTarget;
    }

//     function to call api
    public synchronized String getContent(String url, String userName, String password) throws Exception{
        httpGetMethod(url, userName, password);

        WebTarget target = getTarget(url, userName, password);

        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        if (response.getStatus() == 200) {
            String responseAsString = response.readEntity(String.class);
            return responseAsString;
        }else{
            throw new RequestErrorException("Request error for url : " + url + "|" + "status code : " + response.getStatus());
        }
    }


    public String httpGetMethod(String url, String userName, String password) throws Exception{
        try{
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            String authStr = userName + ":" + password;
            String encoding = new BASE64Encoder().encode(authStr.getBytes());

            org.apache.http.client.HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization","Basic "+encoding);

            org.apache.http.HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String statusInfo = response.getStatusLine().toString();
            if ( statusCode != 200){
                String message = null;
                if (statusCode == 401 || statusCode == 403){
                    message = "Unauthorized, check your userName and password";
                }else if(statusCode == 404){
                    message = "Not found, check the url";
                }else if (statusCode >= 500){
                    message = "Server error,check the status of requested server and request url";
                }else {
                    message = response.getStatusLine().getReasonPhrase();
                }
                throw new RequestErrorException(String.format("Http request error. StatusInfo: %s, Cause: %s. Request Url: %s", statusInfo, message, url));
            }
            String dataStr = EntityUtils.toString(response.getEntity());
            if (dataStr == null || dataStr.length() == 0){
                throw new NoContentException(String.format("No content found from the api. Request Url: %s", url));
            }
//            System.out.println("response entity : " + dataStr);
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

            return dataStr;
        }catch (HttpHostConnectException e){
            String message = "Connection timed out, check the status of requested server and request url";
            throw new RequestErrorException(String.format("Http request error. Cause: %s. Request Url: %s", message, url), e);
        }
    }

    public HttpHeaders createHeaderForResttemplate(String userName, String password){
        String plainCreds = userName + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }

    public <T> T getDataFromApi(String url, String userName, String password, Class<T> t) throws Exception{
        try {
            String response = httpGetMethod(url, userName, password);
            Gson gson = new Gson();
            T extractedObj = gson.fromJson(response, t);
            if (extractedObj == null){
                throw new NoContentException(String.format("No content found from the api. Request Url: %s", url));
            }
            return extractedObj;
        } catch (IllegalStateException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        } catch (JsonSyntaxException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        } catch (ProtocolException e){
            throw new RequestErrorException(String.format("%s. Request Url: %s",e.getCause(), url), e);
        }
    }
}
