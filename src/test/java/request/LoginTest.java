package request;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import org.json.simple.JSONObject;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import static org.testng.AssertJUnit.assertTrue;

public class LoginTest {

    HttpURLConnection con;


    @DataProvider()
    public Object[][] loginPasswordData(){
        return new Object[][]{
                {"test_user", "q1w2e3r4", true},
                {"test", "q1w2e3r4", true},
                {"test_user", "q1", false},
                {"test", "q1", false},
                {"", "", false},
        };
    }

    @Test(dataProvider = "loginPasswordData")
    public void shouldReturnResultForPostRequest2(String login, String password, boolean result) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        System.out.println("login - " + login + ", password - " + password + ", result - " + result);
        //создаем диспетчер доверия, кот. не проверяет сертификаты
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        HostnameVerifier allHosts = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // устанавливаем менеджер доверия
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(allHosts);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "";
        String urlParameters = "username=test_user&password=q1w2e3r4";


        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        try {

            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {

                wr.write(postData);
            }

            StringBuilder content;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(content.toString());

            if(result){
                // ожидаемый результат в случае верного ввода пароль/логина
                Assert.assertEquals(jsonObject.get("loginState").toString(), "OK");
                Assert.assertEquals(jsonObject.get("httpStatusCode").toString(), "200");
            } else {
                // ожидаемый результат в случае неверного ввода пароль/логина
                //ТРЕБУЕТСЯ ДО УТОЧНИТЬ логику
                Assert.assertEquals(jsonObject.get("loginState").toString(), "ERROR");
                Assert.assertEquals(jsonObject.get("httpStatusCode").toString(), "302");
            }



        } catch (ParseException e) {
            e.printStackTrace();
        } finally {

            con.disconnect();
        }


    }


}