package app.extra.storageparams;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class StorageParamsManager {
    private final Map<String, StorageParams> params = new HashMap<>() {{
        put("kinescope", new KinescopeSrorageParams());
        put("yandexcloud", new YandexcloudStorageParams());
    }};
}
