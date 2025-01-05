package app.mapper;

import app.dto.premium.PremiumData;
import app.entity.payment.PremiumEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PremiumMapper {
    List<PremiumData> premiumEntitiesToPremiumDatas(List<PremiumEntity> premiumEntities);
}
