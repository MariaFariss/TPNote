package com.ensim.TP3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.ensim.TP3.model.AddressRepository;
import com.ensim.TP3.model.records.MyResponse;
import com.ensim.TP3.model.records.ResponseEtape5;

@Controller
public class MeteoController {
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	RestTemplate restTemplate;

	@PostMapping("/meteo")
	public String showAddresses(@RequestParam(name = "address") String nameGET, Model model) {
		//Recuperation des adresses
		model.addAttribute("nomTemplate", nameGET);
		//Recuperation des coordonees
		MyResponse myresponse = restTemplate
				.getForEntity("https://api-adresse.data.gouv.fr/search/?q=" + nameGET, MyResponse.class).getBody();
		
		for (int i = 0; i < myresponse.features().length; i++) {
			model.addAttribute("coordinates" + i, myresponse.features()[i].geometry().coordinates());
			model.addAttribute(nameGET, myresponse.features()[0].properties().citycode());
		}
		// Etape5
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer ae2b95fedaf1e06c2e13e7454dee3a87434e33df6ef81fc0f18f3b72a17ac825");
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEtape5 response = restTemplate.exchange("https://api.meteo-concept.com/api/forecast/daily?latlng="+myresponse.features()[0].geometry().coordinates()[0]+"%2C"+myresponse.features()[0].geometry().coordinates()[1]+"&insee="+myresponse.features()[0].properties().citycode(), HttpMethod.GET, request,
		ResponseEtape5.class).getBody();
		model.addAttribute("insee", response.city().insee());
		model.addAttribute("name", response.city().name());
		model.addAttribute("cp", response.city().cp());
		model.addAttribute("forecast", response.forecast());
		return "meteo";
	}

}
