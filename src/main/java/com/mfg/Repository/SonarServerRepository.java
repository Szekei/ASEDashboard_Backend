package com.mfg.Repository;

import com.mfg.Entity.SonarServer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by I309908 on 4/19/2017.
 */
public interface SonarServerRepository extends JpaRepository<SonarServer, Long> {

}
