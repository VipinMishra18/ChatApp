package server;

import common.Message;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static Map<String, ClientHandler> clients = new HashMap<>();
    private static Map<String, Set<ClientHandler>> groups = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started...");

        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            clientHandlers.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    public static void broadcastMessage(Message message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    public static void privateMessage(Message message) {
        ClientHandler recipientHandler = clients.get(message.getRecipient());
        if (recipientHandler != null) {
            recipientHandler.sendMessage(message);
        }
    }

    public static void groupMessage(Message message) {
        Set<ClientHandler> groupMembers = groups.get(message.getGroup());
        if (groupMembers != null) {
            for (ClientHandler member : groupMembers) {
                member.sendMessage(message);
            }
        }
    }

    public static void updateStatus(Message message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    public static void addClient(String username, ClientHandler clientHandler) {
        clients.put(username, clientHandler);
    }

    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        clients.values().remove(clientHandler);
    }

    public static void createGroup(String groupName, ClientHandler creator) {
        groups.put(groupName, new HashSet<>(Collections.singleton(creator)));
    }

    public static void joinGroup(String groupName, ClientHandler member) {
        groups.get(groupName).add(member);
    }
}
