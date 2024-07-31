package client;

import common.Message;

import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;
    private ChatFrame chatFrame;

    public ChatClient(String serverAddress, String username, ChatFrame chatFrame) throws IOException {
        this.username = username;
        this.chatFrame = chatFrame;
        socket = new Socket(serverAddress, 12345);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());

        // Send username to server
        output.writeObject(username);
    }

    public void sendMessage(Message message) throws IOException {
        output.writeObject(message);
    }

    public void receiveMessages() {
        new Thread(() -> {
            while (true) {
                try {
                    Message message = (Message) input.readObject();
                    chatFrame.displayMessage(message);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    public void createGroup(String groupName) throws IOException {
        output.writeObject(new Message(username, groupName, "create_group", null, groupName));
    }

    public void joinGroup(String groupName) throws IOException {
        output.writeObject(new Message(username, groupName, "join_group", null, groupName));
    }

    public void setStatus(String status) throws IOException {
        output.writeObject(new Message(username, status, "status", null, null));
    }
}
