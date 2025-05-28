package tb.wca.mapper;

import org.mapstruct.*;
import tb.wca.entity.CityEntity;
import tb.wca.model.CityGeoModel;

@Mapper(componentModel = "spring")
public interface CityGeoMapper {

    CityEntity modelToEntity(CityGeoModel model);

    CityGeoModel entityToModel(CityEntity entity);
}