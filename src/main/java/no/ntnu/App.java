package no.ntnu;


import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 *
 */
public class App 
{
   // JSONObject answer;
    private int sessionID;
    private Get httpGet;
    private Post httpPost;
    private String host = "datakomm.work";
    private int port = 80;


    public static void main( String[] args )
    {
        App app = new App();
        app.startApp();
        app.doTask();
    }

    private void startApp(){
        httpGet = new Get(host, port);
        httpPost = new Post(host, port);
        //answer = new JSONObject();
    }

    public void doTask(){
        int numberOfTasks = 8;
        int i = 0;
        while(i<numberOfTasks){
            executeTask(i);
            i++;
        }
    }

    private void postAuthorization(){
        String mail = "trymj@stud.ntnu.no";
        String phoneNumber = "95945742";

        JSONObject jsonAuthorization = new JSONObject();
        jsonAuthorization.put("email", mail);
        jsonAuthorization.put("phone", phoneNumber);
        JSONObject response = httpPost.sendPost("dkrest/auth",jsonAuthorization);
        if(response != null && response.has("sessionId")){
            sessionID = response.getInt("sessionId");
        }
        System.out.println(sessionID);
    }

    private void executeTask(int task){
        switch(task) {
            case 0:
                postAuthorization();
                break;
            case 1:
                doTask1(task);
                break;
            case 2:
                doTask2(task);
                break;
            case 3:
                doTask3(task);
                break;
            case 4:
                doTask4(task);
                break;
            case 5:
                doTask5();
                break;
            case 6:
                doTask6();
                break;
          //  case 7:
            //    sendAnswer();
              //  break;

        }
    }

    private JSONObject getTask(int task){
        return httpGet.sendGet("dkrest/gettask/" + task + "?sessionId=" + sessionID);
    }

    private void doTask1(int task){
        JSONObject response = getTask(task);
        if (response != null && response.getInt("taskNr") == 1){
            JSONObject answer = new JSONObject();
            answer.put("msg", "Hello");
            answer.put("sessionId", sessionID);
            httpPost.sendPost("dkrest/solve", answer);
        }
    }

    private void doTask2(int task){
        JSONObject response = getTask(task);
        if (response != null && response.getInt("taskNr") == 2){
            JSONObject answer = new JSONObject();
            JSONArray array = response.getJSONArray("arguments");
            answer.put("msg", array.getString(0));
            answer.put("sessionId", sessionID);
            httpPost.sendPost("dkrest/solve", answer);
        }
    }

    private void doTask3(int task){
        JSONObject response = getTask(task);
        if (response != null && response.getInt("taskNr") == 3){
            JSONObject answer = new JSONObject();
            JSONArray array = response.getJSONArray("arguments");
            int X = 1;
            for(int i = 0; i<array.length(); i++){
                X *= array.getInt(i);
            }
            System.out.println(X);
            answer.put("result", X);
            answer.put("sessionId", sessionID);
            httpPost.sendPost("dkrest/solve", answer);
        }
    }

    private void doTask4(int task){
        JSONObject response = getTask(task);
        if (response != null && response.getInt("taskNr") == 4){
            JSONObject answer = new JSONObject();
            JSONArray array = response.getJSONArray("arguments");
            String pin = bruteForce(array.getString(0));
            answer.put("pin", pin);
            answer.put("sessionId", sessionID);
            httpPost.sendPost("dkrest/solve", answer);
        }
    }

    private void doTask5(){
        JSONObject response = httpGet.sendGet("dkrest/results/" + sessionID);
    }

    private void doTask6(){
        JSONObject answer = new JSONObject();
        httpGet.sendGet("dkrest/gettask/secret?sessionId=" + sessionID);
        int answer_root = (int) Math.sqrt(4064256);
        httpGet.sendGet("dkrest/gettask/"+ answer_root + "?sessionId=" + sessionID);
        answer.put("sessionId", sessionID);
        httpPost.sendPost("dkrest/solve", answer);
    }

   // private void sendAnswer(){
     //   answer.put("sessionId", sessionID);
       // httpPost.sendPost("dkrest/solve", answer);
    //}

    private String bruteForce(String password){
        boolean run = true;
        String pin = null;
        int num = 0;
        String decodedpin = null;
        while(run){
                if(num<10){
                    pin = "000" + num;
                    decodedpin = decodePin(pin);
                    }
                else if((num<100) && (num>9)){
                    pin = "00" + num;
                    decodedpin = decodePin(pin);
                }
                else if((num<1000) && (num>99)){
                    pin = "0" + num;
                    decodedpin = decodePin(pin);
                }
                else{
                    pin = "" + num;
                    decodedpin = decodePin(pin);
                }
                if(password.equals(decodedpin)){
                    run = false;
                }
                num++;
            }
        System.out.println(pin);
        return pin;
    }

    /**
     *
     * @param pin pin to convert to md5
     * @return
     */
    private String decodePin(String pin){
        String md5Pin = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(pin.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes){
                sb.append(String.format("%02x", b));
            }
            md5Pin = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5Pin;
    }
}
