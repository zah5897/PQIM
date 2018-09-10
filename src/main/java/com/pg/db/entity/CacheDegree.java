package com.pg.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zah on 2018/6/22.
 */
@Entity
@Table(name = "cache_degree", uniqueConstraints = {@UniqueConstraint(columnNames="ammeterCode")})

@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class CacheDegree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 32)
    private String ammeterCode;

    private float degree;
    @JsonIgnore
    private Date updateTime;
    @Column(length = 32)
    private String jzqNO;


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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getJzqNO() {
        return jzqNO;
    }

    public void setJzqNO(String jzqNO) {
        this.jzqNO = jzqNO;
    }
}
