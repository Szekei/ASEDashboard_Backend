package com.mfg.Repository;

import com.mfg.Entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by I309908 on 5/18/2017.
 */
public interface UserLogRepository extends JpaRepository<UserLog, Long>{

    List<UserLog> findByDashboardIdAndVersionId(Long dashboardId, Long versionId);
}
