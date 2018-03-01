package com.mfg.Repository;

import com.mfg.Entity.JenkinsServer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by I309908 on 4/24/2017.
 */
public interface JenkinsServerRepository extends JpaRepository<JenkinsServer, Long> {
    JenkinsServer findByDashboard_idAndIsMain(Long dashboardId, boolean isMain);
}
