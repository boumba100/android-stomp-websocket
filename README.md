# android-stomp-websocket

This library was created to allow android devices to communicate whith STOMP servers via websocket.

#Getting started

To get started, download the project from this repository. This project contains the STOMP client library(wstompclient) and an example app using the library. Since the project is created with Android Studio, you can simply open it with the IDE and run it on an emulator or a physical device.

If you want to include the wstompclient library into your own project do as the following: 

Android Studio

Click``` File → New → Import Module``` and select the wstompclient folder.

The step above will add ```':wstompclient'```  to the settings.gradle file as shown below.

```gradle
include ':app', ':wstompclient'
```
To compile the library in your application simply add this line to the project's build.gradle file.

```gradle
dependencies {
	...
compile project(':stompclient')
	...
}
```

# Example code for android client

Stomp server connection

```java
Private StompClient stompClient;

//connect to server
stompClient = new StompClient("ws://<server hostname>:port/endpoint") { //example "ws://localhost:8080/message-server"
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
    }
    
    
    
    //  disconnect from stomp server and websocket
stompClient.unSubscribe("destincation");

```

For different protocol use this constructor


```java

new StompClient("ws://<server hostname>:port/endpoint", new Draf_...()) {
...
}


```

Subscription

```java

stompClient.

(destination); // subscribe to topic

stompClient.unSubscribe(destination); // unsubscribes to topic

```

Send message

```java
stompClient.sendMessage(destination, string);

```

Custom frames 

This library already includes simple STOMP protocol frame templates for these commands :
  CONNECT
  DISCONNECT
  SEND
  SUBSCRIBE
  UNSUBSCRIBE
  
If you want to add extra headers to the frames before sending it, you can easily do as below :

```java

Frame connectionFrame = new Frame().setConnection(destination).append("login", "username").append("password", "password123");
stompClient.sendFrame(connectionFrame);

```

If you simply want to create a fully custom frame you can do as bellow

```java
new Frame().setCommand(command).append(name, value);

```

















 
