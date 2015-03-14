package com.dotin.socket.client;

import com.dotin.socket.server.model.to.ServerTransactionTo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * Created by Dotin school 6 on 3/7/2015.
 */
public class ClientJson {
    public String writeTransactionJsonObject(ClientTransaction transaction)
    {
        JSONObject transactionObj = new JSONObject();
        transactionObj.put("type", transaction.getType());
        transactionObj.put("id",transaction.getId());
        transactionObj.put("amount",transaction.getAmount());
        //transactionObj.put("", new Integer(20));
        return transactionObj.toJSONString();

    }

    public String[] parseResponse(String jsonResponseString)
    {
        JSONParser parser = new JSONParser();
        String [] responses=new String[2];
        try {

            Object obj = parser.parse(jsonResponseString);
            JSONObject jsonObject = (JSONObject) obj;

            String type = (String) jsonObject.get("type");
            String response = (String) jsonObject.get("response");
            responses[0]=type;
            responses[1]=response;
            /*if (type.equals("info")) {
                responses[2] = (String) jsonObject.get("new amount");
            }*/

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return responses;
    }

    public String parseReceivedData(String jsonString) {
        JSONParser parser = new JSONParser();
        try {

            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            String id = (String) jsonObject.get("id");
            return id;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ClientTransaction parseRequset(String jsonString) {
        JSONParser parser = new JSONParser();
        try {

            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;

            String id = (String) jsonObject.get("id");
            String amount = (String) jsonObject.get("amount");
            String type = (String) jsonObject.get("type");

            Object transactionObject = Class.forName("com.dotin.socket.client.ClientTransaction").newInstance();
            Method setParameterMethod = transactionObject.getClass().getDeclaredMethod("setTransactionFields", new Class[]{String.class, String.class, String.class});
            setParameterMethod.invoke(transactionObject, new Object[]{id, amount, type});
            return (ClientTransaction) transactionObject;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
