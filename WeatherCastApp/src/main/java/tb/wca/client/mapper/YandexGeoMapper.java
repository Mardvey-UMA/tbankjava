package tb.wca.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tb.wca.client.dto.YandexGeoResponse;
import tb.wca.exceptions.InvalidApiResponse;
import tb.wca.model.CityGeoModel;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface YandexGeoMapper {

    @Mapping(target = "city", source = "source", qualifiedByName = "extractCity")
    @Mapping(target = "lon",  source = "source", qualifiedByName = "extractLon")
    @Mapping(target = "lat",  source = "source", qualifiedByName = "extractLat")
    CityGeoModel toDto(YandexGeoResponse source);

    @Named("extractCity")
    default String extractCity(YandexGeoResponse src) {
        return src.response()
                .geoObjectCollection()
                .featureMember().get(0)
                .geoObject()
                .name();
    }

    @Named("extractLon")
    default BigDecimal extractLon(YandexGeoResponse src) {
        return parseLowerCorner(src)[0];
    }

    @Named("extractLat")
    default BigDecimal extractLat(YandexGeoResponse src) {
        return parseLowerCorner(src)[1];
    }


    default BigDecimal[] parseLowerCorner(YandexGeoResponse src) {
        if (src == null || src.response() == null || src.response().geoObjectCollection() == null) {
            throw new InvalidApiResponse("YandexGeoAPI empty answer");
        }

        var featureMembers = src.response().geoObjectCollection().featureMember();
        if (featureMembers == null || featureMembers.isEmpty()) {
            throw new InvalidApiResponse("YandexGeoAPI empty answer");
        }

        var lowerCorner = featureMembers.get(0).geoObject().boundedBy().Envelope().lowerCorner();
        if (lowerCorner == null || lowerCorner.isBlank()) {
            throw new InvalidApiResponse("YandexGeoAPI empty coordinates");
        }

        String[] parts = lowerCorner.trim().split("\\s+");
        if (parts.length != 2) {
            throw new InvalidApiResponse("YandexGeoAPI invalid coordinate format");
        }

        return new BigDecimal[] { new BigDecimal(parts[0]), new BigDecimal(parts[1]) };
    }

}
