package pl.rstepniewski.sockets.domain.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rafal on 19.04.2023
 *
 * @author : rafal
 * @date : 19.04.2023
 * @project : SocketProgrammingClientServer
 */

public class Message {
    private String topic;
    private String content;
    private String recipient;
    private String sender;

    public Message(final @JsonProperty("topic") String topic, final @JsonProperty("content") String content, final @JsonProperty("recipient") String recipient, final @JsonProperty("sender") String sender) {
        this.topic = topic;
        this.content = content;
        this.recipient = recipient;
        this.sender = sender;
    }

    public Message() {
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public void setRecipient(final String recipient) {
        this.recipient = recipient;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }
}
