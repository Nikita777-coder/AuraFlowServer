package app.mapper;

import app.dto.meditation.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StatusMapper {
    ObjectMapper objectMapper = new ObjectMapper();
    default String listOfStatusesToString(List<Status> tags) {
        try {
            return objectMapper.writeValueAsString(tags);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert tags to JSON", e);
        }
    }
    default Set<Status> stringStatusesToSetOfStatuses(String jsonTags) {
        StringBuilder sb = new StringBuilder();

        if (jsonTags != null) {
            for (char s : jsonTags.toCharArray()) {
                if (s != '\\' && s != '[' && s != ']' && s != '\"') {
                    sb.append(s);
                }
            }

            return Arrays.stream(sb.toString().split(",")).map(Status::valueOf).collect(Collectors.toSet());
        }

        return null;
    }
}
