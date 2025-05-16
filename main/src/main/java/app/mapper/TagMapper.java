package app.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    ObjectMapper objectMapper = new ObjectMapper();
    default String tagsToJsonStringTags(List<String> tags) {
        try {
            return objectMapper.writeValueAsString(tags);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert tags to JSON", e);
        }
    }
    default List<String> jsonTagsToTags(String jsonTags) {
        StringBuilder sb = new StringBuilder();

        if (jsonTags != null) {
            for (char s : jsonTags.toCharArray()) {
                if (s != '\\' && s != '[' && s != ']' && s != '\"') {
                    sb.append(s);
                }
            }

            return Arrays.asList(sb.toString().split(","));
        }

        return null;
    }
}
