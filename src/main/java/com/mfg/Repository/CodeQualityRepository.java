package com.mfg.Repository;

import com.mfg.Entity.CodeQuality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by I309908 on 4/20/2017.
 */
public interface CodeQualityRepository extends JpaRepository<CodeQuality, Long> {

    List<CodeQuality> findByTypeAndVersion_idAndProjectModule_id(String type, Long vid, Long pmId);

}
