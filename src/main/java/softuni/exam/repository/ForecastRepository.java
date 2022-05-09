package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import softuni.exam.models.entity.DaysOfWeek;
import softuni.exam.models.entity.Forecast;

import java.util.List;

public interface ForecastRepository extends JpaRepository<Forecast, Long> {

    boolean existsByDaysOfWeekAndCity_Id(DaysOfWeek daysOfWeek, Long city);


    @Query("""
            select f from Forecast f
            where f.daysOfWeek = ?1 and f.city.population < ?2
            order by f.maxTemperature DESC, f.id""")
    List<Forecast> findByDaysOfWeekAndCityPopulationLessThanOrderByMaxTemperatureDescIdAsc(DaysOfWeek daysOfWeek, int city_population);
}
