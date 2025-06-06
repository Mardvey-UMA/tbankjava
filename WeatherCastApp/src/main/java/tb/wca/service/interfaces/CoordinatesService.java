package tb.wca.service.interfaces;
import tb.wca.entity.CityEntity;
import tb.wca.model.CityGeoModel;
import tb.wca.exceptions.CityNotFoundException;

public interface CoordinatesService {
    CityGeoModel getCoordinatesByCityName(String cityName) throws CityNotFoundException;
    CityEntity getCoordinatesByCityNameReturnSavedEntity(String cityName) throws CityNotFoundException;
}
