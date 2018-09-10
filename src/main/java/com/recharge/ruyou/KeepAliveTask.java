package com.recharge.ruyou;

import java.util.List;
import java.util.TimerTask;

public class KeepAliveTask extends TimerTask {
    @Override
    public void run() {
        RechargeManager.instance.getServerHandler().keepAlive();
    }
}
