package tb.wca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tb.wca.entity.CityWeatherEntity;
import tb.wca.entity.WeatherCastEntity;
import tb.wca.model.WeatherModel;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "temp", target = "temperature")
    @Mapping(source = "windDir", target = "windDirection")
    WeatherCastEntity modelToEntity(WeatherModel model);

    List<WeatherCastEntity> modelToEntity(List<WeatherModel> models);

    @Mapping(source = "temperature", target = "temp")
    @Mapping(source = "windDirection", target = "windDir")
    WeatherModel entityToModel(WeatherCastEntity entity);

    List<WeatherModel> entityToModel(List<WeatherCastEntity> entities);

    default BigDecimal mapDoubleToBigDecimal(double value) {
        return BigDecimal.valueOf(value);
    }

    default double mapBigDecimalToDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }

    default WeatherModel cityWeatherEntityToWeatherModel(CityWeatherEntity cityWeather) {
        if (cityWeather == null) {
            return null;
        }
        WeatherCastEntity weather = cityWeather.getWeather();
        if (weather == null) {
            return null;
        }
        WeatherModel weatherModel = entityToModel(weather);
        return new WeatherModel(
                cityWeather.getDate(),
                cityWeather.getTime(),
                weatherModel.temp(),
                weatherModel.feelsLike(),
                weatherModel.windSpeed(),
                weatherModel.windDir(),
                weatherModel.humidity(),
                weatherModel.pressure(),
                weatherModel.uvIndex()
        );
    }

    default CityWeatherEntity weatherModelToCityWeatherEntity(WeatherModel model) {
        if (model == null) {
            return null;
        }

        CityWeatherEntity cityWeather = new CityWeatherEntity();
        cityWeather.setDate(model.date());
        cityWeather.setTime(model.time());

        cityWeather.setWeather(modelToEntity(model));

        return cityWeather;
    }


}

