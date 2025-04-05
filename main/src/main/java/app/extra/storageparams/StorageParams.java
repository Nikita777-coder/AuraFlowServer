package app.extra.storageparams;

import app.entity.meditation.MeditationEntity;

import java.util.Map;

public interface StorageParams {
    Map<String, String> getParams(MeditationEntity entity);
}
