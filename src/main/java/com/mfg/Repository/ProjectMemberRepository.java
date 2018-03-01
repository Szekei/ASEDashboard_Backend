package com.mfg.Repository;

import com.mfg.Entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by I309908 on 4/27/2017.
 */
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    int deleteByBcpServerId(Long bcpServerId);
}
