package tb.wca.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YandexGeoResponse(Response response) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            @JsonProperty("GeoObjectCollection")
            GeoObjectCollection geoObjectCollection) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeoObjectCollection(
            @JsonProperty("metaDataProperty")
            MetaDataProperty metaDataProperty,
            List<FeatureMember> featureMember) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MetaDataProperty(
            @JsonProperty("GeocoderResponseMetaData")
            GeocoderResponseMetaData geocoderResponseMetaData) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeocoderResponseMetaData(String found) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FeatureMember(
            @JsonProperty("GeoObject")
            GeoObject geoObject) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeoObject(
            String name,
            @JsonProperty("boundedBy")
            BoundedBy boundedBy) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BoundedBy(Envelope Envelope) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Envelope(String lowerCorner) { }
}
