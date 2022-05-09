package softuni.exam.service;

import softuni.exam.models.entity.DaysOfWeek;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface ForecastService {

    boolean areImported();

    String readForecastsFromFile() throws IOException;

    String importForecasts() throws IOException, JAXBException;

    boolean isEntityExist(DaysOfWeek daysOfWeek, Long city);

    String exportForecasts();
}
