package tb.wca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tb.wca.dto.SubscriptionRequestDTO;
import tb.wca.dto.SubscriptionResponseDTO;
import tb.wca.entity.SubscriptionEntity;
import tb.wca.model.SubscriptionModel;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {


    @Mapping(target = "id", source = "id")
    @Mapping(target = "notificationTime", source = "notificationTime")
    @Mapping(target = "timeZone", source = "timeZone")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    SubscriptionModel entityToModel(SubscriptionEntity entity);


    @Mapping(target = "user", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "notificationTime", source = "notificationTime")
    @Mapping(target = "timeZone", source = "timeZone")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "id", source = "id")
    SubscriptionEntity modelToEntity(SubscriptionModel model);



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "notificationTime", source = "notificationTime")
    @Mapping(target = "timeZone", source = "timeZone")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SubscriptionEntity requestDtoToEntity(SubscriptionRequestDTO dto);


    @Mapping(target = "cityName", source = "entity.city.name")
    @Mapping(target = "notificationTime", source = "notificationTime")
    @Mapping(target = "timeZone", source = "timeZone")
    SubscriptionRequestDTO entityToRequestDto(SubscriptionEntity entity);


    @Mapping(target = "success", constant = "true")
    @Mapping(target = "expectedNextNotificationDateTime", ignore = true)
    SubscriptionResponseDTO entityToResponseDto(SubscriptionEntity entity);


    @Mapping(target = "cityName", ignore = true)
    @Mapping(target = "notificationTime", source = "notificationTime")
    @Mapping(target = "timeZone", source = "timeZone")
    SubscriptionRequestDTO modelToRequestDto(SubscriptionModel model);


    @Mapping(target = "success", constant = "true")
    @Mapping(target = "expectedNextNotificationDateTime", ignore = true)
    SubscriptionResponseDTO modelToResponseDto(SubscriptionModel model);
}

