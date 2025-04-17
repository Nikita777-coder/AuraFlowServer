package app.repository;

import app.dto.meditation.Status;
import app.entity.usermeditation.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity, UUID> {
    List<StatusEntity> findAllByStatusIn(List<Status> statuses);
}
