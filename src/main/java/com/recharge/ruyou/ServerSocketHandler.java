package com.recharge.ruyou;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerSocketHandler extends Thread {
    private int port = 9528;

    public ServerSocketHandler(int port) {
        this.port = port;
    }

    public ServerSocketHandler() {
    }

    private List<ProtocolHandler> socketHandles = Collections.synchronizedList(new ArrayList<>());

    private boolean bindOK = false;
    private ServerSocket serverSocket;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            bindOK = true;
            System.out.println("bind localhost port=" + port + " success.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("bind localhost port=" + port + " fail.");
            return;
        }
        while (bindOK) {
            Socket s;
            try {
                s = serverSocket.accept();
                System.out.println("new client connect.");
                ProtocolHandler shHandler = new ProtocolHandler(this, s);
                int index = socketHandles.indexOf(shHandler);
                if (index >= 0) {
                    socketHandles.get(index).stopConnect();
                    socketHandles.remove(index);
                    System.out.println("remove old client. index=" + index);
                }
                socketHandles.add(shHandler);
                System.out.println("new client add.");
                shHandler.start();
                System.out.println("client size=" + socketHandles.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isBindOK() {
        return bindOK;
    }

    public void stopBind() {
        if (!socketHandles.isEmpty()) {
            for (ProtocolHandler handler : socketHandles) {
                handler.stopConnect();
            }
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bindOK = false;
    }

    public void removeClient(ProtocolHandler protocolHandler) {
        if (socketHandles.contains(protocolHandler)) {
            socketHandles.remove(protocolHandler);
        }
    }

    public boolean doCharge(byte[] protocl) {
        boolean hasSendOK = true;

        if (!isBindOK()) {
            return false;
        }
        for (ProtocolHandler handler : socketHandles) {
            try {
                handler.writeData(protocl);
            } catch (IOException e) {
                e.printStackTrace();
                hasSendOK = false;
            }
        }
        return hasSendOK;
    }

    public void readCharge(byte[] protocl) {
        for (ProtocolHandler handler : socketHandles) {
            try {
                handler.writeData(protocl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void keepAlive() {
        if (bindOK) {
            for (ProtocolHandler handler : socketHandles) {
                handler.keepAlive();
            }
        }
    }
}
