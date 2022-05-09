package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.models.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsCountryByCountryName(String countryName);
}
