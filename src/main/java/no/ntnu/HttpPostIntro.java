package no.ntnu;

import org.json.JSONObject;
import java.util.concurrent.ThreadLocalRandom;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class HttpPostIntro {

    public static void main(String[] args) {
        HttpPostIntro postExample = new HttpPostIntro("datakomm.work", 80);
        postExample.post3RandomNumbers();
    }

    private String BASE_URL;

    public HttpPostIntro(String host, int port){
        BASE_URL = "http://" + host + ":" + port + "/";
    }

    private void post3RandomNumbers(){
        int a = ThreadLocalRandom.current().nextInt(1, 100 + 1);
        int b = ThreadLocalRandom.current().nextInt(1, 100 + 1);
        int c = ThreadLocalRandom.current().nextInt(1, 100 + 1);

        JSONObject json = new JSONObject();
        json.put("a", a);
        json.put("b", b);

        System.out.println(json);
        sendPost("dkrest/test/post", json);
    }

    /**
     * Send HTTP POST
     *
     * @param path     Relative path in the API.
     * @param jsonData The data in JSON format that will be posted to the server
     */
    private void sendPost(String path, JSONObject jsonData) {
        try {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP POST to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Server reached");

                // Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                String responseBody = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                System.out.println(responseBody);
            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
            }
        } catch (ProtocolException e) {
            System.out.println("Protocol nto supported by the server");
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Read the whole content from an InputStream, return it as a string
     * @param is Inputstream to read the body from
     * @return The whole body as a string
     */
    private String convertStreamToString(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append('\n');
            }
        } catch (IOException ex) {
            System.out.println("Could not read the data from HTTP response: " + ex.getMessage());
        }
        return response.toString();
    }

}
