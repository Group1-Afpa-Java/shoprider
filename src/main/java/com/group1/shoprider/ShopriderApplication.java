package com.group1.shoprider;

import com.group1.shoprider.models.Role;
import com.group1.shoprider.models.Type;
import com.group1.shoprider.models.User;
import com.group1.shoprider.repository.RepositoryRole;
import com.group1.shoprider.repository.RepositoryType;
import com.group1.shoprider.repository.RepositoryUser;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@RequiredArgsConstructor
@SpringBootApplication
public class ShopriderApplication implements CommandLineRunner {

	private final RepositoryType typeRepository;
	private final RepositoryRole repositoryRole;
	private final PasswordEncoder passwordEncoder;
	private final RepositoryUser userRepository;
	private final EntityManager entityManager;

	public static void main(String[] args) {
		SpringApplication.run(ShopriderApplication.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {

		// create user roles
		Role clientRole = new Role();
		clientRole.setName("CLIENT");
		repositoryRole.save(clientRole);

		Role adminRole = new Role();
		adminRole.setName("ADMIN");
		repositoryRole.save(adminRole);

		Role superAdminRole = new Role();
		superAdminRole.setName("SUPER-ADMIN");
		repositoryRole.save(superAdminRole);


		// create Admin and Super-admin Users
		User admin = User.builder()
				.firstName("James")
				.lastName("Noells")
				.username("jamesnoells")
				.password(passwordEncoder.encode("adminpassword"))
				.email("jamesnoells@gmail.com")
				.address("1 Rue Luxembourg, 5900 Lille")
				.role(entityManager.merge(adminRole))
				.build();
		userRepository.save(admin);

		User superAdmin = User.builder()
				.firstName("Kira")
				.lastName("Brooks")
				.username("kirabrooks")
				.password(passwordEncoder.encode("superadminpassword"))
				.email("kirabrooks@gmail.com")
				.address("49 Rue Edouard, 59200 Tourcoing")
				.role(entityManager.merge(superAdminRole))
				.build();
		userRepository.save(superAdmin);

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
