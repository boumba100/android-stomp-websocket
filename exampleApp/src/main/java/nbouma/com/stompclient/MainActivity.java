package nbouma.com.stompclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import nbouma.com.wstompclient.implementation.StompClient;
import nbouma.com.wstompclient.model.Frame;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "stomp app";

    private StompClient stompClient;

    private Button connectButton;
    private Button disconnectButton;
    private Button subscribeButton;
    private Button unSubscribeButton;
    private Button sendButton;
    private EditText roomBox;
    private EditText messageBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.connectButton = (Button) findViewById(R.id.connect_button);
        this.disconnectButton = (Button) findViewById(R.id.disconnect_button);
        this.subscribeButton = (Button) findViewById(R.id.subscribe_button);
        this.unSubscribeButton = (Button) findViewById(R.id.unsubscribe_button);
        this.sendButton = (Button) findViewById(R.id.send_button);
        this.roomBox = (EditText) findViewById(R.id.room_editText);
        this.messageBox = (EditText) findViewById(R.id.message_editText);

        this.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectStomp();
            }
        });

        this.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectStomp();
            }
        });

        this.subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribeStomp();
            }
        });

        this.unSubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unSubscribeStomp();
            }
        });

        this.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("stomp app", "trying to send message");
                sendMessage();
            }
        });
    }

    private void connectStomp() {
        this.stompClient = new StompClient("ws://<server hostname>:8080/endpoint") { //example "ws://localhost:8080/message-server"
            @Override
            protected void onStompError(String errorMessage) {
                Log.d(TAG, "error : " + errorMessage);
            }

            @Override
            protected void onConnection(boolean connected) {
                Log.d(TAG, "connected : " + String.valueOf(connected));
            }

            @Override
            protected void onDisconnection(String reason) {
                Log.d(TAG, "disconnected : " + reason);
            }

            @Override
            protected void onStompMessage(final Frame frame) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), frame.getBody(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        enableConnectedControls();
    }

    private void disconnectStomp() {
        this.stompClient.disconnect();
        disableConnectedControls();
    }

    private void subscribeStomp() {
        this.stompClient.subscribe("/topic/Message/" + this.roomBox.getText().toString());
        enableSubscribedControls();
    }

    private void unSubscribeStomp() {
        this.stompClient.unSubscribe(this.roomBox.getText().toString());
        disableSubscribedControls();
    }

    private void sendMessage() {
        this.stompClient.sendMessage("/topic/Message/" + this.roomBox.getText().toString(), messageBox.getText().toString());

    }

    private void enableConnectedControls() {
        this.disconnectButton.setEnabled(true);
        this.subscribeButton.setEnabled(true);
        this.unSubscribeButton.setEnabled(true);
    }

    private void disableConnectedControls() {
        this.disconnectButton.setEnabled(false);
        this.subscribeButton.setEnabled(false);
        this.unSubscribeButton.setEnabled(false);
    }

    private void enableSubscribedControls() {
        this.sendButton.setEnabled(true);
    }

    private void disableSubscribedControls() {
        this.sendButton.setEnabled(false);

    }

}













