package com.pg.db.repository;

import com.pg.db.entity.CacheDegree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by zah on 2018/6/22.
 */
public interface DBRepository extends JpaRepository<CacheDegree, Integer> {
    @Query(value = "select * from cache_degree where ammeter_code=?1", nativeQuery = true)
    List<CacheDegree> getCacheDegree(String dbNO);

    @Query(value = "select count(*) from cache_degree where ammeter_code=?1", nativeQuery = true)
    int getCacheDegreeCount(String dbNO);


    @Modifying
    @Transactional
    @Query("update CacheDegree c set c.degree=?2,c.updateTime=?3  where c.ammeterCode=?1")
    int updateCacheDegree(String dbNO, float degree, Date updateTime);
}
