package app.mapper;

import app.dto.meditation.Status;
import app.entity.usermeditation.StatusEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatusMapper {
    List<StatusEntity> statusesToStatusEntities(List<Status> statuses);
}
