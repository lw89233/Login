package pl.edu.uws.lw89233;

import pl.edu.uws.lw89233.managers.DatabaseManager;
import pl.edu.uws.lw89233.managers.EnvManager;
import pl.edu.uws.lw89233.managers.MessageManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    private final int PORT = Integer.parseInt(EnvManager.getEnvVariable("LOGIN_MICROSERVICE_PORT"));
    private final String DB_HOST = EnvManager.getEnvVariable("DB_HOST");
    private final String DB_PORT = EnvManager.getEnvVariable("DB_PORT");
    private final String DB_NAME = EnvManager.getEnvVariable("DB_NAME");
    private final String DB_USER = EnvManager.getEnvVariable("DB_USER");
    private final String DB_PASSWORD = EnvManager.getEnvVariable("DB_PASSWORD");
    private final DatabaseManager dbManager = new DatabaseManager(DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);

    public void startService() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Login microservice is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting Login microservice: " + e.getMessage());
        }
    }

    private class ClientHandler extends Thread {

        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request = in.readLine();
                if (request != null && request.contains("login_request")) {
                    String response = handleLogin(request);
                    out.println(response);
                }
            } catch (IOException e) {
                System.err.println("Error handling client request: " + e.getMessage());
            }
        }

        private String handleLogin(String request) {
            MessageManager responseManager = new MessageManager(request);
            String login = responseManager.getAttribute("login");
            String password = responseManager.getAttribute("password");
            String sql = "SELECT COUNT(*) AS count FROM users WHERE login = ? AND password = ?";
            String response = "type:login_response#message_id:%s#".formatted(responseManager.getAttribute("message_id"));

            try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
                stmt.setString(1, login);
                stmt.setString(2, password);

                try (ResultSet resultSet = stmt.executeQuery()) {
                    resultSet.next();
                    int count = resultSet.getInt("count");
                    if (count > 0) {
                        response += "status:200#";
                    } else {
                        response += "status:400#";
                    }
                    return response;
                }
            } catch (SQLException e) {
                response += "status:400#";
                return response;
            }
        }
    }

    public static void main(String[] args) {
        new Login().startService();
    }
}