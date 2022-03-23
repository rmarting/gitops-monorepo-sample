package io.jromanmartin.quarkus.sampleapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    public enum MessageType {
        GREETING,
        FAREWELL
    }

    private MessageType messageType;

    private String name;

    private String message;

    private String environment;

}
