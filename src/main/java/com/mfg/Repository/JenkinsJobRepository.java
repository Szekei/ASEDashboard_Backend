package com.mfg.Repository;

import com.mfg.Entity.JenkinsJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by I309908 on 4/24/2017.
 */
public interface JenkinsJobRepository extends JpaRepository<JenkinsJob, Long> {

    JenkinsJob findByProjectModule_idAndIsVisible(Long pmId, boolean isVisible);
}
