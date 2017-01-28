package nbouma.com.wstompclient.model;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Frame {

    private static final String ENCODING = "UTF-8";
    private String command;
    private String body;
    private Map<String, String> header;

    public Frame() {
        this.header = new HashMap<>();
    }

    public Frame(final String command) {
        this.command = command;
        this.header = new HashMap<>();
    }

    public Frame setConnection(final String destination) { // sets default connection frame
        this.command = Command.CONNECT;
        this.append("host", destination);
        this.append("accept-version", "1.1");
        this.append("heart-beat", "60000,0");
        return this;
    }

    public Frame setDisconnection() {
        this.command = Command.DISCONNECT;
        return this;
    }

    public Frame setSubscribe(final String destination, final String id) { // sets default subscribe frame
        this.command = Command.SUBSCRIBE;
        this.append("id", id);
        this.append("destination", destination);
        return this;
    }

    public Frame setUnsubscribe(final String destination, final String id) {
        this.command = Command.UNSUBSCRIBE;
        this.append("id", id);
        this.append("destination", destination);
        return this;
    }

    public Frame setSend(final String destination, final String body) {
        this.command = Command.SEND;
        this.body = body;
        this.append("destination", destination);
        return this;
    }


    public void setCommand(final String command) {
        this.command = command;
    }

    public void setBody(final String body) {
        this.body = body;
    }


    public String getCommand() {
        return this.command;
    }

    public String getBody() {
        return String.valueOf(this.body);
    }

    public Frame parseFrame(final String frameString) {
        String frameArray[] = frameString.split("\n\n");
        this.body = frameArray[1].replace("\n\n", "").substring(0, frameArray[1].length() - 1);
        return this;
    }


    public void append(final String key, final String value) {
        this.header.put(key, value);
    }

    public byte[] toBytes() {
        try {
            return this.toString().getBytes(ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        StringBuffer frameString = new StringBuffer(command);
        frameString.append("\n");

        for (String key : header.keySet()) {
            frameString.append(key);
            frameString.append(":");
            frameString.append(header.get(key));
            frameString.append("\n");
        }
        frameString.append("\n");

        if (this.body != null) {
            frameString.append(this.body);
        }
        frameString.append("\000");

        return frameString.toString();
    }


}
