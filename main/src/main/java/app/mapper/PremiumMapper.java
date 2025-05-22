package app.mapper;

import app.dto.premium.PremiumData;
import app.dto.premium.PremiumIntegrationServiceResponse;
import app.entity.payment.PremiumEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PremiumMapper {
    List<PremiumData> premiumEntitiesToPremiumDatas(List<PremiumEntity> premiumEntities);
    @Mapping(target = "paymentToken", source = "confirmation.paymentToken")
    PremiumEntity premiumIntegrationServiceResponseToPremiumEntity(PremiumIntegrationServiceResponse premiumIntegrationServiceResponse);
}
