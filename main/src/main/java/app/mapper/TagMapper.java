package app.mapper;

import app.dto.meditation.Tag;
import app.entity.meditation.TagEntity;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TagMapper {
    List<TagEntity> tagsToTagsEntities(List<Tag> tags);
}
