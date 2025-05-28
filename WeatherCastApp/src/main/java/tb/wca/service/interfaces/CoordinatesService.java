package tb.wca.service.interfaces;
import tb.wca.model.CityGeoModel;
import tb.wca.exceptions.CityNotFoundException;

public interface CoordinatesService {
    CityGeoModel getCoordinatesByCityName(String cityName) throws CityNotFoundException;
}
