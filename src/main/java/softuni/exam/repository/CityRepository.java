package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.models.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {

    boolean existsCityByCityName(String cityName);


}
