package tb.wca.mapper;

import org.mapstruct.*;
import tb.wca.entity.CityEntity;
import tb.wca.model.CityGeoModel;

@Mapper(componentModel = "spring")
public interface CityGeoMapper {

    @Mapping(source = "city", target = "name")
    @Mapping(source = "lat", target = "latitude")
    @Mapping(source = "lon", target = "longitude")
    CityEntity modelToEntity(CityGeoModel model);

    @Mapping(source = "name", target = "city")
    @Mapping(source = "latitude", target = "lat")
    @Mapping(source = "longitude", target = "lon")
    CityGeoModel entityToModel(CityEntity entity);
}