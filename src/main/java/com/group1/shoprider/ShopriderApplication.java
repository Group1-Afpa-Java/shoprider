package com.group1.shoprider;

import com.group1.shoprider.models.Type;
import com.group1.shoprider.repository.RepositoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
public class ShopriderApplication implements CommandLineRunner {

	private final RepositoryType typeRepository;

	public static void main(String[] args) {
		SpringApplication.run(ShopriderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Type type1 = new Type();
		type1.setName("GUITAR");

		Type type2 = new Type();
		type2.setName("PiANO");

		Type type3 = new Type();
		type3.setName("DRUM");

		Type type4 = new Type();
		type4.setName("SAXOPHONE");

		Type type5 = new Type();
		type5.setName("VIOLON");

		typeRepository.saveAll(List.of(type1, type2, type3, type4, type5));
	}
}
