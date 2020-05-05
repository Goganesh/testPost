package request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
public class RequestTest {

    HttpURLConnection con;

    @Test
    public void shouldReturnResultForGetRequest1() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://github.com/"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        // print status code
        //System.out.println(response.statusCode());
        // print response body
        //System.out.println(response.body());


        assertTrue(200==response.statusCode());
    }

    @Test
    public void shouldReturnResultForPostRequest1() throws IOException, InterruptedException {

        //form parameters
        Map values = new HashMap<String, String>() {{
            put("login", "basiladze@mail.ru");
            put ("password", "****");
        }};

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(values);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://github.com/session"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        // print status code
        System.out.println(response.statusCode());
        // print response body
        //System.out.println(response.body());

        assertTrue(302 == response.statusCode());
    }

    @Test
    public void shouldReturnResultForGetRequest2() throws IOException, InterruptedException {
        var url = "https://github.com/";

        try {

            var myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();

            con.setRequestMethod("GET");
            //con.setRequestProperty("User-Agent", "Java client");
            StringBuilder content;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {

                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            System.out.println(content.toString());
            assertTrue(content.toString() != null);

        } finally {

            con.disconnect();
        }
    }

    @Test
    public void shouldReturnResultForPostRequest2() throws IOException, InterruptedException {

        var url = "https://github.com/session";
        var urlParameters = "login=basiladze@mail.ru&password=***";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        try {

            var myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            //con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (var wr = new DataOutputStream(con.getOutputStream())) {

                wr.write(postData);
            }

            StringBuilder content;

            try (var br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            System.out.println(content.toString());
            assertTrue(content.toString() != null);

        } finally {

            con.disconnect();
        }


    }

}
