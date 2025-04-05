package app.extra.storageparams;

import app.entity.meditation.MeditationEntity;

import java.util.Map;

public class KinescopeSrorageParams implements StorageParams {
    @Override
    public Map<String, String> getParams(MeditationEntity entity) {
        return Map.of("video-id", entity.getVideoId().toString());
    }
}
