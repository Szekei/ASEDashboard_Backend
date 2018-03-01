package com.mfg.Repository;

import com.mfg.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by I309908 on 5/8/2017.
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByDashboard_idAndVersion_id(Long dashboardId, Long versionId);

    List<Ticket> findByDashboard_idAndVersion_idAndType(Long dashboardId, Long versionId, String type);
}
