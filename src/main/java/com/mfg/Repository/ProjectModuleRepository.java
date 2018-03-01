package com.mfg.Repository;

import com.mfg.Entity.Dashboard;
import com.mfg.Entity.ProjectModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by I309908 on 4/18/2017.
 */
@Repository
public interface ProjectModuleRepository extends JpaRepository<ProjectModule, Long> {

    String Q_GET_PROJECT_BY_ID = "select pm from ProjectModule pm where pm.dashboard = ?1 and pm.parentId is null and pm.isVisible = 1";

    String Q_GET_MODULE_BY_ID = "select pm from ProjectModule pm where pm.dashboard = ?1 and pm.parentId is not null and pm.isVisible = 1"
                                + "and pm.isSonarTask=false";

    String Q_GET_SONARTASK_BY_ID = "select pm from ProjectModule pm where pm.dashboard = ?1 and pm.parentId is not null and pm.isVisible = 1 and pm.isSonarTask=true";

    @Query(Q_GET_PROJECT_BY_ID)
    List<ProjectModule> findProjectByDashId(Dashboard dashboard);

    @Query(Q_GET_MODULE_BY_ID)
    List<ProjectModule> findModuleByDashId(Dashboard dashboard);

    @Query(Q_GET_SONARTASK_BY_ID)
    List<ProjectModule> findSonarTaskByDashId(Dashboard dashboard);



    List<ProjectModule> findByParentIdAndIsSonarTaskAndIsVisible(Long id, boolean isSonarTask, boolean isVisible);

}
