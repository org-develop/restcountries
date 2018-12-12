package com.ccmsd.restcountries.v2.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ccmsd.restcountries.v2.model.Country;
import com.ccmsd.restcountries.v2.repository.CountryDAO;

@RestController
public class CountryRestController {

	@Autowired
	private CountryDAO<Country> countryDAO;

	@GetMapping("/rest/countries")
	public Iterable<Country> getCountrys() {
		return countryDAO.findAll();
	}

	@PostMapping("/rest/countries")
	public ResponseEntity<List<Country>> createCountries(@RequestBody List<Country> countries) {
		List<Country> updatedCountries = countryDAO.saveAll(countries);
		try {
			return ResponseEntity.created(new URI("/rest/countries")).body(updatedCountries);
		} catch (URISyntaxException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/rest/country/{id}")
	public ResponseEntity<Country> getCountry(@PathVariable String id) {
		return countryDAO.findById(id).map(country -> ResponseEntity.ok().body(country))
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/rest/country")
	public List<Country> getCountryByQuery(@RequestParam(value = "name", required = false) String name) {
		return countryDAO.findByQuery(name);
	}

	@PostMapping("/rest/country")
	public ResponseEntity<Country> createCountry(@RequestBody Country country) {
		Country newCountry = countryDAO.save(country);
		try {
			return ResponseEntity.created(new URI("/rest/Country/" + newCountry.getName())).body(newCountry);
		} catch (URISyntaxException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PutMapping("/rest/country/{name}")
	public ResponseEntity<Country> updateCountry(@RequestBody Country country, @PathVariable String name) {
		country.setName(name);
		return ResponseEntity.ok().body(countryDAO.save(country));
	}

	@DeleteMapping("/rest/country/{id}")
	public ResponseEntity<?> deleteCountry(@PathVariable String name) {
		countryDAO.deleteById(name);
		return ResponseEntity.ok().build();
	}
}
