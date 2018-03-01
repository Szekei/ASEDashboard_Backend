package com.mfg.Repository;

import com.mfg.Entity.ParsingRule;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by I309908 on 4/24/2017.
 */
public interface ParsingRuleRepository extends JpaRepository<ParsingRule, Long>{
    ParsingRule findByJenkinsJob_idAndType(Long id, String type);
}
