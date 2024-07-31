package client;

import common.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class ChatFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextArea chatArea;
    private JTextField messageField;
    private DefaultListModel<String> userListModel;
    private JFileChooser fileChooser;
    private ChatClient chatClient;

    public ChatFrame(String username) {
        setTitle("Chat Application - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(username);
            }
        });

        JButton fileButton = new JButton("Send File");
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile(username);
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(fileButton, BorderLayout.WEST);

        JPanel groupPanel = new JPanel(new BorderLayout());
        JTextField groupNameField = new JTextField();
        JButton createGroupButton = new JButton("Create Group");
        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = groupNameField.getText();
                try {
                    chatClient.createGroup(groupName);
                    groupNameField.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton joinGroupButton = new JButton("Join Group");
        joinGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = groupNameField.getText();
                try {
                    chatClient.joinGroup(groupName);
                    groupNameField.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        groupPanel.add(groupNameField, BorderLayout.CENTER);
        groupPanel.add(createGroupButton, BorderLayout.WEST);
        groupPanel.add(joinGroupButton, BorderLayout.EAST);

        JPanel userStatusPanel = new JPanel(new BorderLayout());
        userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        userStatusPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JComboBox<String> statusComboBox = new JComboBox<>(
                new String[] { "Online", "Away", "Do Not Disturb", "Offline" });
        statusComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String status = (String) statusComboBox.getSelectedItem();
                try {
                    chatClient.setStatus(status);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        userStatusPanel.add(statusComboBox, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(chatArea), userStatusPanel);
        splitPane.setResizeWeight(0.7);

        add(splitPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(groupPanel, BorderLayout.NORTH);

        fileChooser = new JFileChooser();

        try {
            chatClient = new ChatClient("localhost", username, this);
            chatClient.receiveMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String username) {
        String text = messageField.getText();
        if (text.isEmpty()) {
            return;
        }
        Message message = new Message(username, text, "text", null, null);
        try {
            chatClient.sendMessage(message);
            messageField.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendFile(String username) {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                byte[] fileData = Files.readAllBytes(file.toPath());
                Message message = new Message(username, fileData, file.getName(), null, null);
                chatClient.sendMessage(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void displayMessage(Message message) {
        if (message.getType().equals("text")) {
            chatArea.append(message.getSender() + ": " + message.getContent() + "\n");
        } else if (message.getType().equals("file")) {
            chatArea.append(message.getSender() + " sent a file: " + message.getFileName() + "\n");
            int response = JOptionPane.showConfirmDialog(this, "Do you want to download the file?", "File received",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                JFileChooser saveFileChooser = new JFileChooser();
                saveFileChooser.setSelectedFile(new File(message.getFileName()));
                int saveReturnValue = saveFileChooser.showSaveDialog(this);
                if (saveReturnValue == JFileChooser.APPROVE_OPTION) {
                    File saveFile = saveFileChooser.getSelectedFile();
                    try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                        fos.write(message.getFileData());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else if (message.getType().equals("status")) {
            updateUserStatus(message.getSender(), message.getContent());
        }
    }

    public void updateUserStatus(String username, String status) {
        SwingUtilities.invokeLater(() -> {
            boolean userExists = false;
            for (int i = 0; i < userListModel.size(); i++) {
                String user = userListModel.get(i);
                if (user.startsWith(username)) {
                    userListModel.set(i, username + " (" + status + ")");
                    userExists = true;
                    break;
                }
            }
            if (!userExists) {
                userListModel.addElement(username + " (" + status + ")");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String username = JOptionPane.showInputDialog(null, "Enter your username:");
            if (username != null && !username.trim().isEmpty()) {
                ChatFrame chatFrame = new ChatFrame(username);
                chatFrame.setVisible(true);
            }
        });
    }
}
