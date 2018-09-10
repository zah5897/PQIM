package com.pg.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pg.db.enums.ChargStatus;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zah on 2018/6/22.
 */
@Entity
@Table(name = "charge_degree", uniqueConstraints = {@UniqueConstraint(columnNames ={"ammeterCode","unique_id"})})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class ChargeDegree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 32)
    private String ammeterCode;
    private float degree;
    @JsonIgnore
    private Date create_time;
    @Enumerated(EnumType.ORDINAL)
    private ChargStatus status = ChargStatus.CREATE;
    @Column(length = 32)
    @JsonIgnore
    private String jzqNO;
    @Column(length = 32)
    @JsonIgnore
    private String token;
    @JsonIgnore
    private int flag;
    @Column(length = 64)
    private  String unique_id;

    private int notiftTimes=0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmmeterCode() {
        return ammeterCode;
    }

    public void setAmmeterCode(String ammeterCode) {
        this.ammeterCode = ammeterCode;
    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public ChargStatus getStatus() {
        return status;
    }

    public void setStatus(ChargStatus status) {
        this.status = status;
    }

    public String getJzqNO() {
        return jzqNO;
    }

    public void setJzqNO(String jzqNO) {
        this.jzqNO = jzqNO;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }


    public int getNotiftTimes() {
        return notiftTimes;
    }

    public void setNotiftTimes(int notiftTimes) {
        this.notiftTimes = notiftTimes;
    }
}
