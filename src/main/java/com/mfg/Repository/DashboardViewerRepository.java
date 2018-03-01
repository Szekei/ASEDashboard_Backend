package com.mfg.Repository;

import com.mfg.Entity.DashboardViewer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by I309908 on 5/8/2017.
 */
public interface DashboardViewerRepository extends JpaRepository<DashboardViewer, Long> {

    List<DashboardViewer> findByDashboardId(Long dashboardId);

    int deleteByDashboardId(Long dashboardId);

    DashboardViewer findByDashboardIdAndViewerId(Long dashboardId, String viewerId);

    List<DashboardViewer> findByViewerId(String viewerId);
}
