package com.loan555.kisdapplication2.JavaCode;

public class SocketHandler {
    private static io.socket.client.Socket socket;

    public static synchronized io.socket.client.Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(io.socket.client.Socket socket){
        SocketHandler.socket = socket;
    }
}
