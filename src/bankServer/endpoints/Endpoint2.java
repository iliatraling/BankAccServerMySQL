package bankServer.endpoints;

import ConsoleApp.BankAcc;
import bankServer.bankAcc.BankAccaunt;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Endpoint2 implements HttpHandler {
    private BankAccaunt bankAccaunt = new BankAccaunt();
    private Scanner scanner;
    private Connection connection;
    public Endpoint2(Scanner scanner, Connection connection) {
        this.scanner = scanner;
        this.connection = connection;
        testSQL();
    }
    public void testSQL(){
        System.out.println("in test");
        try {
            BankAcc ba = new BankAcc();
            ba.setID(200);
            ba.showAcc(connection);
        } catch (SQLException e) {
            System.out.println("exeption in test");
            e.printStackTrace();

        }


    }
    
    public void setHttpExchangeResponseHeaders(HttpExchange httpExchange) {
        // Set common response headers
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Credentials-Header", "*");
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestParamValue=null;
        String requestURI = httpExchange.getRequestURI().toString();
        System.out.println(requestURI);
        System.out.println(httpExchange.getRequestMethod());
        if("GET".equals(httpExchange.getRequestMethod())) {
            System.out.println("bankServer.endpoints.Endpoint2: GET handled");
            System.out.println("handle get requaest" + handleGetRequest(httpExchange));

           // requestParamValue = new ToJsonBankAcc(1,"bill", "jhon", new Date(2020,10,12), new Time(13,54,48) , 100).convert();
            //requestParamValue = new BankAccaunt().convertToJson();
            //requestParamValue = new bankServer.bankAcc.BankAccaunt(1,"bill", "jhon", new Date(2020,10,12), new Time(13,54,48) , 100).convertToJson();
            try {
                bankAccaunt.setID(Integer.parseInt(handleGetRequest(httpExchange)));
                testSQL();
                bankAccaunt.setID(200);
                bankAccaunt.showAcc(connection);
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
            }

            requestParamValue = bankAccaunt.convertToJson();
        }
        else if("POST".equals(httpExchange.getRequestMethod())) {
            System.out.println("bankServer.endpoints.Endpoint2: Post handled");
            requestParamValue = handlePostRequest(httpExchange);
            System.out.println("Endpoint2: Post handled" + requestParamValue);
        }
        else{
            System.out.println("bankServer.endpoints.Endpoint2: Nothing handled");
        }
        handleResponse(httpExchange,requestParamValue);
    }


    private String handleGetRequest(HttpExchange httpExchange) {
        System.out.println("hello" + httpExchange.
                getRequestURI()
                .toString());
        return httpExchange.
                getRequestURI()
                .toString()
                .split("\\?")[1]
                .split("=")[1];
    }

    private String handlePostRequest(HttpExchange httpExchange) throws IOException {
        BufferedReader httpInput = new BufferedReader(new InputStreamReader(
                httpExchange.getRequestBody(), "UTF-8"));
        StringBuilder in = new StringBuilder();
        String input;
        while ((input = httpInput.readLine()) != null) {
            in.append(input).append(" ");
        }
        System.out.println(input);
        httpInput.close();
        return in.toString().trim();
    }

    private void handleResponse(HttpExchange httpExchange, String requestParamValue)  throws  IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        String htmlResponse = requestParamValue;
        
        System.out.println(htmlResponse);
        setHttpExchangeResponseHeaders(httpExchange);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");//kok
        byte[] bytes = htmlResponse.getBytes("UTF-8");

        httpExchange.sendResponseHeaders(200, bytes.length);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
