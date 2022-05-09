package softuni.exam.service;


import softuni.exam.models.entity.Country;

import java.io.IOException;

public interface CountryService {

    boolean areImported();

    String readCountriesFromFile() throws IOException;

    String importCountries() throws IOException;

    boolean isEntityExist(String countryName);

    Country findById(Long country);
}
