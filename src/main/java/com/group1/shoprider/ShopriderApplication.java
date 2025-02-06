package com.group1.shoprider;

import com.group1.shoprider.models.Role;
import com.group1.shoprider.models.Type;
import com.group1.shoprider.repository.RepositoryRole;
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
	private final RepositoryRole repositoryRole;

	public static void main(String[] args) {
		SpringApplication.run(ShopriderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// create user roles
		Role role1 = new Role();
		role1.setName("CLIENT");
		repositoryRole.save(role1);

		Role role2 = new Role();
		role1.setName("ADMIN");
		repositoryRole.save(role2);

		// create instrument types
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
