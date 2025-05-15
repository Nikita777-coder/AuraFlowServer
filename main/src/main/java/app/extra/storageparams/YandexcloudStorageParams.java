package app.extra.storageparams;

import app.entity.MeditationEntity;

import java.util.HashMap;
import java.util.Map;

public class YandexcloudStorageParams implements StorageParams {
    @Override
    public Map<String, String> getParams(MeditationEntity entity) {
        var map = new HashMap<String, String>() {{
            put("video-link", entity.getVideoLink());
        }};

        if (entity.isWasUploadedFromUrl()) {
            map.put("video-id", entity.getVideoId().toString());
        }

        return map;
    }
}
