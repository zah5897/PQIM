package com.pg.db.controller;

import com.pg.db.entity.CacheDegree;
import com.pg.db.exception.ERROR;
import com.pg.db.service.DBService;
import com.pg.db.util.HttpResult;
import com.pg.db.util.TextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zah on 2018/6/19.
 */
@RestController
@RequestMapping("/db")
@Api(value = "电表")
public class DBController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBController.class);
    @Autowired
    DBService dbService;

    @RequestMapping("/query_degree")
    @ApiOperation(value = "查询电表剩余电量")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "dbNO", value = "表号", required = true)})
    public ModelMap queryegree(String dbNO) {
        if (TextUtils.isEmpty(dbNO)) {
            return HttpResult.getResultMap(ERROR.ERR_FAILED, "dbNo不能为空");
        }
        CacheDegree cacheDegree = dbService.query(dbNO);
        return HttpResult.getResultOKMap(cacheDegree);
    }


    @RequestMapping("/charge")
    @ApiOperation(value = "充值")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "ammeterCode", value = "表号", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "orderNo", value = "订单-充值唯一值", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "token", value = "充值token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Float", name = "degree", value = "充值度数", required = true)})

    public ModelMap charge(String ammeterCode,String token, float degree, String orderNo) {
        LOGGER.debug("充值：表号=" + ammeterCode + ",充值度数=" + degree);
        if (TextUtils.isEmpty(ammeterCode)) {
            return HttpResult.getResultMap(ERROR.ERR_FAILED, "dbNO不能为空");
        }

        if (TextUtils.isEmpty(token)) {
            return HttpResult.getResultMap(ERROR.ERR_FAILED, "token不能为空");
        }
        if (degree<10) {
            return HttpResult.getResultMap(ERROR.ERR_FAILED, "至少充值10度");
        }
        if (TextUtils.isEmpty(orderNo)) {
            return HttpResult.getResultMap(ERROR.ERR_FAILED, "orderNo不能为空");
        }
        return dbService.saveCharge(ammeterCode,token, degree, orderNo);
    }

    @RequestMapping("/charge_history")
    @ApiOperation(value = "查询充值记录")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "dbNO", value = "表号", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageIndex", value = "页码（1开始）", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数量", required = true)})

    public ModelMap chargeHistory(String dbNO, int pageIndex, int pageSize) {
        if (TextUtils.isEmpty(dbNO)) {
            return HttpResult.getResultMap(ERROR.ERR_FAILED, "dbNO不能为空");
        }
        return HttpResult.getResultOKMap(dbService.chargeHistory(dbNO, pageIndex, pageSize));
    }

    @RequestMapping("/query_charge")
    @ApiOperation(value = "查询充值状态")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "ammeterCode", value = "表号", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "orderNo", value = "订单编号", required = true)})

    public ModelMap queryCharge(String ammeterCode, String orderNo) {
        if (TextUtils.isEmpty(ammeterCode)) {
            return HttpResult.getResultMap(ERROR.ERR_FAILED, "表号不能为空");
        }
        if (TextUtils.isEmpty(orderNo)) {
            return HttpResult.getResultMap(ERROR.ERR_FAILED, "订单号不能为空");
        }
        return HttpResult.getResultOKMap(dbService.queryChargeByOrderNO(ammeterCode, orderNo));
    }
}
