package com.pg.db.repository;

import com.pg.db.entity.CacheDegree;
import com.pg.db.entity.ChargeDegree;
import com.pg.db.enums.ChargStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by zah on 2018/6/22.
 */
public interface ChargeRepository extends JpaRepository<ChargeDegree, Integer> {
    @Query(value = "select * from charge_degree where ammeter_code=?1 order by id desc limit 1", nativeQuery = true)
    List<ChargeDegree> getChargeDegree(String dbNO);

    @Query(value = "select * from charge_degree where ammeter_code=?1 order by id desc limit ?2,?3", nativeQuery = true)
    List<ChargeDegree> getChargeDegreeByPage(String dbNO,int index,int size);


    @Query(value = "select * from charge_degree where ammeter_code=?1 and unique_id=?2 order by id desc limit 1", nativeQuery = true)
    List<ChargeDegree> getChargeByUniqueId(String dbNO,String unique_id);

    @Query(value = "select count(*) from charge_degree where ammeter_code=?1 and unique_id=?2", nativeQuery = true)
    int getChargeCount(String dbNO,String unique_id);



    @Modifying
    @Transactional
    @Query("update ChargeDegree c set c.flag=?2,c.status=?3  where c.ammeterCode=?1 and c.token=?4 and c.unique_id=?5")
    void updateChargeStatus(String dbNo, int i, ChargStatus status,String token,String unique_id);


    @Modifying
    @Transactional
    @Query("update ChargeDegree c set c.notiftTimes=?2 where  c.unique_id=?1")
    void updateNotifyChargeTimes(String unique_id,int times);
}
