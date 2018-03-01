package com.mfg.Repository;

import com.mfg.Entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by I309908 on 4/18/2017.
 */
public interface VersionRepository extends JpaRepository<Version, Long> {
    String Q_GET_LATESTVERSION_BY_DASHBOARDID = "select * from VERSION  where dashboard_id = ?1 order by id DESC LIMIT 1";
    String Q_GET_EARLIESTVERSION_BY_DASHBOARDID = "select * from VERSION  where dashboard_id = ?1 order by id ASC LIMIT 1";
    Version findTop1ByOrderByIdDesc();

    @Query(value = Q_GET_LATESTVERSION_BY_DASHBOARDID, nativeQuery = true)
    Version findTop1ByDashboardIdByOrderByIdDesc(Long dashboardId);

    @Query(value = Q_GET_EARLIESTVERSION_BY_DASHBOARDID, nativeQuery = true)
    Version findTop1ByDashboardIdByOrderByIdAsc(Long dashboardId);

    Version findById(Long id);
}
