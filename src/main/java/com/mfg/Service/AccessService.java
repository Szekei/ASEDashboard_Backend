package com.mfg.Service;

import com.mfg.Entity.Dashboard;
import com.mfg.Entity.DashboardViewer;
import com.mfg.Exception.NoDataFoundException;
import com.mfg.Repository.DashboardRepository;
import com.mfg.Repository.DashboardViewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by I309908 on 7/17/2017.
 */
@Service
public class AccessService {
    @Autowired
    private DashboardViewerRepository dashboardViewerRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    public boolean checkViewAccess(Long dashboardId, String userId) throws Exception{
        DashboardViewer viewer = dashboardViewerRepository.findByDashboardIdAndViewerId(dashboardId, userId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId);
        if (viewer != null || dashboard.getOwner().equalsIgnoreCase(userId)){
            return true;
        }
        return false;
    }

    public List<DashboardViewer> saveOrUpdateViewer(Long dashboardId, List<DashboardViewer> viewerList) throws Exception{
        dashboardViewerRepository.deleteByDashboardId(dashboardId);
        if (viewerList != null){
            return dashboardViewerRepository.save(viewerList);
        }else {
            throw new NoDataFoundException("No content in parameter.");
        }
    }

    public List<DashboardViewer> getViewerByDashboardId(Long dashboardId) throws Exception{
        return dashboardViewerRepository.findByDashboardId(dashboardId);
    }
}
