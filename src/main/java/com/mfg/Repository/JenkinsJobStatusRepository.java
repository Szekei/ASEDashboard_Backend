package com.mfg.Repository;

import com.mfg.Entity.JenkinsJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by I309908 on 5/4/2017.
 */
public interface JenkinsJobStatusRepository extends JpaRepository<JenkinsJobStatus, Long> {
//    List<JenkinsJobStatus> findByVersion_idAndProjectModule_id(Long vid, Long pmId);
    List<JenkinsJobStatus> findByVersion_idAndJenkinsJob_id(Long vid, Long jobId);

}
