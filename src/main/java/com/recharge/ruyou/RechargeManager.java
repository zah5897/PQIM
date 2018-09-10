package com.recharge.ruyou;

import com.recharge.ruyou.callback.Callback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;


public enum RechargeManager {
    instance;
    private ServerSocketHandler serverHandler;
    public static final int BIND_PORT = 9528;
    private List<Callback> callbacks;
    Timer timer = new Timer();

    public void start() {
        start(BIND_PORT);
    }

    public void start(int port) {

        if (port < 1 || port > 65535) {
            port = BIND_PORT;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("VM shutdown.");
                instance.stop();
            }
        });
        serverHandler = new ServerSocketHandler(port);
        serverHandler.start();

        long time = 1000 * 30; //10 s
        timer.scheduleAtFixedRate(new KeepAliveTask(), time, time);
    }

    public void stop() {
        System.out.println("server stop.");
        timer.cancel();
        serverHandler.stopBind();
    }

    public boolean charge(byte[] deviceNO, byte[] token) {
        byte[] protocl = ProtocolUtil.createChargeValueProtocl(deviceNO, token);
        return serverHandler.doCharge(protocl);
    }

    public void query(byte[] deviceNO, byte[] token, int x) {
        byte[] protocl = ProtocolUtil.createReadChargeValueProtocl(deviceNO, token, x);
        serverHandler.doCharge(protocl);
    }

    public void readDegree(String deviceNO) {
        byte[] protocl = ProtocolUtil.createReadValueProtocl(deviceNO);
        serverHandler.readCharge(protocl);
    }

    public boolean isStart() {
        return serverHandler.isBindOK();
    }

    public void registListener(Callback callback) {
        if (callbacks == null) {
            callbacks = new ArrayList<>();
        }

        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    public void uploadVal(byte[] jzq, byte[] dbNO, float sydlNum) {

        if (callbacks == null || callbacks.isEmpty()) {
            return;
        }
        List<Callback> temp = new ArrayList<>();
        temp.addAll(callbacks);
        for (Callback callback : temp) {
            callback.onPower(jzq, dbNO, sydlNum);
        }
    }

    public ServerSocketHandler getServerHandler() {
        return serverHandler;
    }
}
