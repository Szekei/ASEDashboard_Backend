package com.mfg.Repository;

import com.mfg.Entity.FunctionalQuality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by I309908 on 4/21/2017.
 */
public interface FunctionalQualityRepository extends JpaRepository<FunctionalQuality, Long> {
//    List<FunctionalQuality> findByTypeAndVersion_idAndProjectModule_id(String type, Long vid, Long pmId);

    List<FunctionalQuality> findByTypeAndVersion_idAndJenkinsJob_id(String type, Long vid, Long jobId);
}
