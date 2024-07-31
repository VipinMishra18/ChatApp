package server;

import common.Message;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());

            // First message from client should be the username
            username = (String) input.readObject();
            ChatServer.addClient(username, this);

            while (true) {
                Message message = (Message) input.readObject();
                if (message.getType().equals("status")) {
                    ChatServer.updateStatus(message);
                } else if (message.getRecipient() != null) {
                    ChatServer.privateMessage(message);
                } else if (message.getGroup() != null) {
                    ChatServer.groupMessage(message);
                } else {
                    ChatServer.broadcastMessage(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                ChatServer.removeClient(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Message message) {
        try {
            output.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
