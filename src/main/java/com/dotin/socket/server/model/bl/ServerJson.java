package com.dotin.socket.server.model.bl;

import com.dotin.socket.server.model.to.DepositTo;
import com.dotin.socket.server.model.to.ServerTransactionTo;
import org.apache.maven.plugins.annotations.Parameter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Dotin school 6 on 3/7/2015.
 */
public class ServerJson {
    private HashMap<String, DepositTo> depositsHashMap = null;

    @Parameter(property = "portNumber")
    private Long portNumber;

    public Long getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Long portNumber) {
        this.portNumber = portNumber;
    }

    public HashMap<String, DepositTo> getDepositsHashMap() {
        return depositsHashMap;
    }

    public void setDepositsHashMap(HashMap<String, DepositTo> depositsHashMap) {
        this.depositsHashMap = depositsHashMap;
    }

    public void readJsonFile() throws Exception {
        JSONParser parser = new JSONParser();
        depositsHashMap = new HashMap<String, DepositTo>();
        try {

            Object obj = parser.parse(new FileReader("./src/main/java/com/dotin/socket/server/resources/core.json"));
            JSONObject jsonObject = (JSONObject) obj;

            portNumber = (Long) jsonObject.get("port");

            JSONArray depositsArray = (JSONArray) jsonObject.get("deposits");
            Iterator depositsIterator = depositsArray.iterator();

            while (depositsIterator.hasNext()) {

                JSONParser depositsParser = new JSONParser();
                JSONObject depositJsonObject = (JSONObject) depositsParser.parse(depositsIterator.next().toString());
                String customerName = (String) depositJsonObject.get("customer");
                String customerId = (String) depositJsonObject.get("id");
                BigDecimal upperBound = new BigDecimal(((String) depositJsonObject.get("upperBound")).replaceAll(",", ""));
                BigDecimal initialBalance = new BigDecimal(((String) depositJsonObject.get("initialBalance")).replaceAll(",", ""));

                Object depositObject = Class.forName("com.dotin.socket.server.model.to.DepositTo").newInstance();
                Method setParameterMethod = depositObject.getClass().getDeclaredMethod("setDepositFields", new Class[]{String.class, BigDecimal.class, BigDecimal.class});
                setParameterMethod.invoke(depositObject, new Object[]{customerName, initialBalance, upperBound});

                depositsHashMap.put(customerId, (DepositTo) depositObject);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();

        }
    }

    public ServerTransactionTo parseReceivedData(String jsonString) {
        JSONParser parser = new JSONParser();
        try {

            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;

            String id = (String) jsonObject.get("id");
            BigDecimal amount = new BigDecimal(((String) jsonObject.get("amount")).replaceAll(",", ""));
            String type = (String) jsonObject.get("type");

            Object transactionObject = Class.forName("com.dotin.socket.server.model.to.ServerTransactionTo").newInstance();
            Method setParameterMethod = transactionObject.getClass().getDeclaredMethod("setDepositFields", new Class[]{String.class, BigDecimal.class, String.class});
            setParameterMethod.invoke(transactionObject, new Object[]{id, amount, type});
            return (ServerTransactionTo) transactionObject;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String writeTransactionInfoObject(String type, String response,String amount)
    {
        JSONObject transactionObj = new JSONObject();
        transactionObj.put("type", type);
        transactionObj.put("response",response);
        transactionObj.put("new amount",amount);
        //transactionObj.put("thraed",name);
        System.out.println(transactionObj.toJSONString());
        //System.out.println("dd");*/
        return transactionObj.toJSONString();
    }

    public String writeTransactionInfoObject(String type, String response)
    {
        JSONObject transactionObj = new JSONObject();
        transactionObj.put("type", type);
        transactionObj.put("response",response);
        //transactionObj.put("thraed",name);
        System.out.println(transactionObj.toJSONString());
        //System.out.println("dd");*/
        return transactionObj.toJSONString();
    }

    public String writeTransactionWarnObject(String type, String response)
    {
        JSONObject transactionObj = new JSONObject();
        transactionObj.put("type", type);
        transactionObj.put("response",response);
        return transactionObj.toJSONString();
    }
    public String[] parseResponse(String jsonResponseString)
    {
        JSONParser parser = new JSONParser();
        String [] responses=new String[3];
        try {

            Object obj = parser.parse(jsonResponseString);
            JSONObject jsonObject = (JSONObject) obj;

            String type = (String) jsonObject.get("type");
            String response = (String) jsonObject.get("response");
            responses[0]=type;
            responses[1]=response;
            if (type.equals("info")) {
                responses[2] = (String) jsonObject.get("new amount");
                //System.out.println(responses[2]);
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return responses;
    }
}
