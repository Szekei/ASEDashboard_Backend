package com.mfg.Repository;

import com.mfg.Entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by I309908 on 4/18/2017.
 */
@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {

    String Q_SET_ACTIVE_TO_FALSE = "update Dashboard d set d.isActive = 0 where d.id = ?1";
    String Q_SET_ACTIVE_TO_TRUE = "update Dashboard d set d.isActive = 1 where d.id = ?1";
    String Q_SET_ALL_TO_FALSE = "update Dashboard d set d.isActive = 0 where d.owner = ?1";
    String Q_GET_VISISBLE_BY_ID = "select d from Dashboard d where d.id = ?1 and d.isVisible = true";

    List<Dashboard> findByOwnerAndIsVisible(String owner, boolean isVisible);

    Dashboard findByOwnerAndIsVisibleAndIsActive(String owner, boolean isVisible, boolean isActive);

    List<Dashboard> findByIsVisibleAndIsActive(boolean isVisible, boolean isActive);

    Dashboard findByNameAndOwnerAndIsVisible(String name, String ownerId, boolean isVisible);

    Dashboard findByIdAndIsVisible(Long id, boolean isVisible);

    @Query(Q_GET_VISISBLE_BY_ID)
    Dashboard findById(Long id);

    @Transactional
    int deleteById(Long id);

    @Modifying
    @Query(Q_SET_ACTIVE_TO_FALSE)
    int setActiveToFalse(Long id);

    @Modifying
    @Query(Q_SET_ACTIVE_TO_TRUE)
    int setActiveToTrue(Long id);

    @Modifying
    @Query(Q_SET_ALL_TO_FALSE)
    int setAllToFalse(String ownerId);





}
