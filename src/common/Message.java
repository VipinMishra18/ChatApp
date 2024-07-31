package common;

import java.io.Serializable;

public class Message implements Serializable {
    private String sender;
    private String content;
    private String type; // "text" or "file"
    private String recipient; // For private messaging
    private String group; // For group messaging
    private byte[] fileData;
    private String fileName;

    public Message(String sender, String content, String type, String recipient, String group) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.recipient = recipient;
        this.group = group;
    }

    public Message(String sender, byte[] fileData, String fileName, String recipient, String group) {
        this.sender = sender;
        this.fileData = fileData;
        this.fileName = fileName;
        this.type = "file";
        this.recipient = recipient;
        this.group = group;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getGroup() {
        return group;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public String getFileName() {
        return fileName;
    }
}
