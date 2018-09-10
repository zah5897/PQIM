package com.pg.db.service;

import com.pg.db.entity.CacheDegree;
import com.pg.db.entity.ChargeDegree;
import com.pg.db.enums.ChargStatus;
import com.pg.db.exception.ERROR;
import com.pg.db.exception.GlobalExceptionHandler;
import com.pg.db.repository.ChargeRepository;
import com.pg.db.repository.DBRepository;
import com.pg.db.util.HttpClientUtils;
import com.pg.db.util.HttpResult;
import com.pg.db.util.SignUtil;
import com.pg.db.util.SpringContextUtil;
import com.recharge.ruyou.Log;
import com.recharge.ruyou.ProtocolUtil;
import com.recharge.ruyou.RechargeManager;
import com.recharge.ruyou.callback.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zah on 2018/6/22.
 */
@Service
public class DBService implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBService.class);
    @Autowired
    private DBRepository dbRepository;
    @Autowired
    private ChargeRepository chargeRepository;


    private static final int MINUTES = 60; //1小时内没更新，表示电表掉线

    @Value("${db_server.bind_port}")
    private int bind_port;

    @Value("${updateAmmeterData_URL}")
    private String updateAmmeterData_URL;

    @Value("${ammeterNotify_URL}")
    private String ammeterNotify_URL;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public CacheDegree query(String dbNo) {
        List<CacheDegree> tmps = dbRepository.getCacheDegree(dbNo);
        if (!tmps.isEmpty()) {
            return tmps.get(0);
        }
        return null;
    }

    public void updateCacheDegree(String jzq, String dbNo, float degree) {
        //提交电量到后台
        updateDegree(dbNo, degree);

        int count = dbRepository.getCacheDegreeCount(dbNo);
        if (count > 0) {
            List<ChargeDegree> temps = chargeRepository.getChargeDegree(dbNo);
            if (!temps.isEmpty()) {
                ChargeDegree chargeDegree = temps.get(0);
                CacheDegree cacheDegree = query(dbNo);
                if (chargeDegree.getStatus() != ChargStatus.SUCCESS) {
                    //说明两次剩余电量有变动，更新上次充值状态
                    chargeRepository.updateChargeStatus(dbNo, 2, ChargStatus.SUCCESS, chargeDegree.getToken(), chargeDegree.getUnique_id());
                    dbRepository.updateCacheDegree(dbNo, degree, new Date());
                    notifyChargeSuccess(chargeDegree, !confirm(degree - cacheDegree.getDegree(), chargeDegree));
                } else {
                    dbRepository.updateCacheDegree(dbNo, degree, new Date());
                    doRechage(chargeDegree.getAmmeterCode(), chargeDegree.getToken());
                }
            }
            return;
        }
        CacheDegree cacheDegree = new CacheDegree();
        cacheDegree.setAmmeterCode(dbNo);
        cacheDegree.setDegree(degree);
        cacheDegree.setUpdateTime(new Date());
        cacheDegree.setJzqNO(jzq);
        dbRepository.save(cacheDegree);
    }

    public ModelMap saveCharge(String dbNO, String token, float degree, String unique_id) {
        List<ChargeDegree> chargeDegrees = chargeRepository.getChargeByUniqueId(dbNO, unique_id);
        if (!chargeDegrees.isEmpty()) {
            ChargeDegree temp = chargeDegrees.get(0);
            if (temp.getStatus() == ChargStatus.SUCCESS) {
                notifyChargeSuccess(temp, false);
            }
            return HttpResult.getResultOKMap("当前还有未处理订单");
        }
        CacheDegree cacheDegree = query(dbNO);
        if (cacheDegree == null) {
            return HttpResult.getResultMap(ERROR.ERR_DB_NOT_EXIST);
        }


        boolean isSendOK = false;
        //1小时内无更新剩余电量,无法充值
        if (System.currentTimeMillis() - cacheDegree.getUpdateTime().getTime() < MINUTES * 60 * 1000) {
            isSendOK = doRechage(dbNO, token);
        }

        ChargeDegree chargeDegree = new ChargeDegree();
        if (isSendOK) {
            chargeDegree.setStatus(ChargStatus.CREATE);
        } else {
            chargeDegree.setStatus(ChargStatus.FAIL);
        }
        chargeDegree.setAmmeterCode(dbNO);
        chargeDegree.setUnique_id(unique_id);
        chargeDegree.setCreate_time(new Date());
        chargeDegree.setDegree(degree);
        chargeDegree.setFlag(0);
        chargeDegree.setJzqNO(cacheDegree.getJzqNO());
        chargeDegree.setToken(token);
        chargeRepository.save(chargeDegree);
        return HttpResult.getResultOKMap(chargeDegree);
    }


    public boolean doRechage(String dbNO, String token) {
        byte[] dbByte = ProtocolUtil.dbNOStrtoByte("000000" + dbNO);
        byte[] tokenByte = ProtocolUtil.toTokenBytes(token);
        boolean isSendOK = RechargeManager.instance.charge(dbByte, tokenByte);
        return isSendOK;
    }


    public List<ChargeDegree> chargeHistory(String dbNO, int pageIndex, int pageSize) {
        return chargeRepository.getChargeDegreeByPage(dbNO, (pageIndex - 1) * pageSize, pageSize);
    }

    public ChargeDegree queryChargeByOrderNO(String dbNO, String orderNO) {
        List<ChargeDegree> chargeDegrees = chargeRepository.getChargeByUniqueId(dbNO, orderNO);
        if (!chargeDegrees.isEmpty()) {
            return chargeDegrees.get(0);
        }
        return null;
    }

    private void updateDegree(String dbNO, float degree) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> param = new HashMap<>();
                    param.put("ammeterCode", dbNO);
                    param.put("ammeterValue", String.valueOf(degree));
                    param.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    String sign = SignUtil.sign(param, "oi69w5qXBH3RigrX4ok0YkUxZewoJCY2");
                    param.put("sign", sign);
                    Map<String, Object> result = HttpClientUtils.post(updateAmmeterData_URL, param);
                    if ("0".equals(result.get("resultCode"))) {
                        LOGGER.debug("上传电量成功");
                    } else {
                        LOGGER.error("上传电量失败：code=" + result.get("resultCode"));
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        });
    }

    public void notifyChargeSuccess(ChargeDegree chargeDegree, boolean degreeErr) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> param = new HashMap<>();
                    param.put("orderNo", chargeDegree.getUnique_id());
                    if (degreeErr) {
                        param.put("result", "2");
                    } else {
                        param.put("result", "1");
                    }
                    String sign = SignUtil.sign(param, "oi69w5qXBH3RigrX4ok0YkUxZewoJCY2");
                    param.put("sign", sign);
                    Map<String, Object> result = HttpClientUtils.post(ammeterNotify_URL, param);
                    if ("0".equals(result.get("resultCode"))) {
                        LOGGER.debug("通知电量充值成功返回成功");
                        chargeRepository.updateNotifyChargeTimes(chargeDegree.getUnique_id(), chargeDegree.getNotiftTimes() + 1);
                    } else {
                        LOGGER.error("通知电量充值成功返回失败：code=" + result.get("resultCode"));
                    }
                } catch (Exception e) {
                    LOGGER.error("通知电量充值成功异常" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread() {
            @Override
            public void run() {
                RechargeManager.instance.start(bind_port);
                RechargeManager.instance.registListener(new Callback() {
                    @Override
                    public void onPower(byte[] jzq, byte[] db, float degree) {
                        String temp = ProtocolUtil.getJZQFromByte(jzq);
                        String dbStr = ProtocolUtil.toHexFmr(db);

                        if (temp.startsWith("0")) {
                            temp = temp.substring(1);
                        }
                        if (dbStr.startsWith("0")) {
                            dbStr = dbStr.substring(1);
                        }
                        String log = "收到电量上报：集中器编号=" + temp + ",电表编号=" + dbStr + ",剩余电量=" + degree;
                        LOGGER.debug(log);
                        Log.d(log);
                        updateCacheDegree(temp, dbStr, degree);
                    }

                });
            }
        }.start();
    }

    private boolean confirm(float degree, ChargeDegree chargeDegree) {
        if (chargeDegree.getDegree() - degree <= 10) {
            return true;
        }
        return false;
    }

}
