package tb.wca.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tb.wca.client.dto.YandexGeoResponse;
import tb.wca.exceptions.InvalidApiResponse;
import tb.wca.model.CityGeoModel;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface YandexGeoMapper {

    @Mapping(target = "city",
            expression =
                    "java( source.response()" +
                            ".geoObjectCollection()" +
                            ".featureMember().get(0)" +
                            ".geoObject().name() )")

    @Mapping(target = "lon",
            expression = "java( parseLowerCorner(source)[0] )")

    @Mapping(target = "lat",
            expression = "java( parseLowerCorner(source)[1] )")
    CityGeoModel toDto(YandexGeoResponse source);

    default BigDecimal[] parseLowerCorner(YandexGeoResponse src) {
        if (src == null || src.response() == null || src.response().geoObjectCollection() == null) {
            throw new InvalidApiResponse("YandexGeoAPI пустой ответ");
        }
        var featureMembers = src.response().geoObjectCollection().featureMember();
        if (featureMembers == null || featureMembers.isEmpty()) {
            throw new InvalidApiResponse("YandexGeoAPI пустой ответ");
        }
        var lowerCorner = featureMembers.get(0).geoObject().boundedBy().Envelope().lowerCorner();
        if (lowerCorner == null || lowerCorner.isBlank()) {
            throw new InvalidApiResponse("YandexGeoAPI пустые координаты");
        }
        String[] parts = lowerCorner.trim().split("\\s+");
        if (parts.length != 2) {
            throw new InvalidApiResponse("YandexGeoAPI некорретный формат координат");
        }
        return new BigDecimal[] { new BigDecimal(parts[0]), new BigDecimal(parts[1]) };
    }

}
