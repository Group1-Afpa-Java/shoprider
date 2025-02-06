package com.group1.shoprider;

import com.group1.shoprider.models.Type;
import com.group1.shoprider.repository.RepositoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@RequiredArgsConstructor
@SpringBootApplication
public class ShopriderApplication implements CommandLineRunner {

	private final RepositoryType typeRepository;

	public static void main(String[] args) {
		SpringApplication.run(ShopriderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Optional<Type> type1 = typeRepository.findByName("GUITAR");
		if (type1.isEmpty()) {
			Type guitarType = new Type();
			guitarType.setName("GUITAR");
			typeRepository.save(guitarType);
		}

		Optional<Type> type2 = typeRepository.findByName("PIANO");
		if (type2.isEmpty()) {
			Type pianoType = new Type();
			pianoType.setName("PIANO");
			typeRepository.save(pianoType);
		}

		Optional<Type> type3 = typeRepository.findByName("DRUM");
		if (type3.isEmpty()) {
			Type drumType = new Type();
			drumType.setName("DRUM");
			typeRepository.save(drumType);
		}

		Optional<Type> type4 = typeRepository.findByName("SAXOPHONE");
		if (type4.isEmpty()) {
			Type saxophoneType = new Type();
			saxophoneType.setName("SAXOPHONE");
			typeRepository.save(saxophoneType);
		}

		Optional<Type> type5 = typeRepository.findByName("VIOLON");
		if (type5.isEmpty()) {
			Type violonType = new Type();
			violonType.setName("VIOLON");
			typeRepository.save(violonType);
		}
	}
}
