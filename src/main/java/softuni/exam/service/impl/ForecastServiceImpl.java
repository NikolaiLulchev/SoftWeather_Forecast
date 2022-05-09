package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ForecastSeedRootDto;
import softuni.exam.models.entity.DaysOfWeek;
import softuni.exam.models.entity.Forecast;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.CityService;
import softuni.exam.service.ForecastService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ForecastServiceImpl implements ForecastService {

    private static final String FORECASTS_FILE_PATH = "src/main/resources/files/xml/forecasts.xml";

    private final ForecastRepository forecastRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final CityService cityService;

    public ForecastServiceImpl(ForecastRepository forecastRepository, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser, CityService cityService) {
        this.forecastRepository = forecastRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.cityService = cityService;
    }

    @Override
    public boolean areImported() {
        return forecastRepository.count() > 0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(Path.of(FORECASTS_FILE_PATH));
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        xmlParser.fromFile(FORECASTS_FILE_PATH, ForecastSeedRootDto.class)
                .getForecastSeedDtos()
                .stream()
                .filter(forecastSeedDto -> {
                    boolean isValid = validationUtil.isValid(forecastSeedDto)
                                      && !isEntityExist(forecastSeedDto.getDaysOfWeek(), forecastSeedDto.getCity());
                    sb.append(isValid
                                    ? String.format("Successfully import forecast %s - %.2f"
                                    , forecastSeedDto.getDaysOfWeek(), forecastSeedDto.getMaxTemperature())
                                    : "Invalid forecast")
                            .append(System.lineSeparator());
                    return isValid;
                }).map(forecastSeedDto -> {
                    Forecast forecast = modelMapper.map(forecastSeedDto, Forecast.class);
                    forecast.setCity(cityService.findById(forecastSeedDto.getCity()));
                    return forecast;
                })
                .forEach(forecastRepository::save);


        return sb.toString();
    }

    @Override
    public boolean isEntityExist(DaysOfWeek daysOfWeek, Long city) {
        return forecastRepository.existsByDaysOfWeekAndCity_Id(daysOfWeek, city);
    }

    @Override
    public String exportForecasts() {
        StringBuilder sb = new StringBuilder();

        List<Forecast> forecastList = forecastRepository.findByDaysOfWeekAndCityPopulationLessThanOrderByMaxTemperatureDescIdAsc(DaysOfWeek.SUNDAY, 150000);

        forecastList.forEach(forecast -> {
            sb.append(String.format("City: %s:\n" +
                                    "\t-min temperature: %.2f\n" +
                                    "\t--max temperature: %.2f\n" +
                                    "\t---sunrise: %s\n" +
                                    "\t----sunset: %s",
                            forecast.getCity().getCityName(),
                            forecast.getMinTemperature(),
                            forecast.getMaxTemperature(),
                            forecast.getSunrise().toString(),
                            forecast.getSunset().toString()))
                    .append(System.lineSeparator());
        });
        return sb.toString();
    }
}
