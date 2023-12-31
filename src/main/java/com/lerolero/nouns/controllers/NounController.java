package com.lerolero.nouns.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;

import reactor.core.publisher.Flux;
import java.util.function.Function;

import com.lerolero.nouns.services.NounService;

@RestController
@RequestMapping("/nouns")
public class NounController {

	@Autowired
	private NounService nounService;

	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> get(@RequestParam(defaultValue = "1") Integer size) {
		return nounService.randomNounList(size);
	}

	@GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> subscribe(@RequestParam(defaultValue = "200") Integer interval) {
		return nounService.randomNounProducer(interval).onBackpressureDrop();
	}

	@Bean
	public Function<Flux<String>,Flux<String>> nounfunction() {
		return flux -> flux
			.doOnNext(size -> System.out.println("NOUNS: Processing " + size))
			.flatMap(size -> nounService.randomNounList(Integer.parseInt(size)));
	}

}
