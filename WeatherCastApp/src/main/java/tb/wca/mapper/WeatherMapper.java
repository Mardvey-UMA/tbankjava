package tb.wca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tb.wca.entity.WeatherCastEntity;
import tb.wca.model.WeatherModel;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    @Mapping(target = "id", ignore = true)
    WeatherCastEntity modelToEntity(WeatherModel model);
    List<WeatherCastEntity> modelToEntity(List<WeatherModel> models);

    WeatherModel entityToModel(WeatherCastEntity entity);
    List<WeatherModel> entityToModel(List<WeatherCastEntity> entities);

    default BigDecimal mapDoubleToBigDecimal(double value) {
        return BigDecimal.valueOf(value);
    }

    default double mapBigDecimalToDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }
}
