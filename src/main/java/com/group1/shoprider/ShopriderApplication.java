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
	private final PasswordEncoder passwordEncoder;
	private final RepositoryType typeRepository;
	private final RepositoryRole repositoryRole;
	private final RepositoryUser repositoryUser;
	private final EntityManager entityManager;
	public static void main(String[] args) {
		SpringApplication.run(ShopriderApplication.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) throws Exception {

		// create user roles
		Role roleClient = new Role();
		roleClient.setName("CLIENT");
		repositoryRole.save(roleClient);

		Role roleAdmin = new Role();
		roleAdmin.setName("ADMIN");
		repositoryRole.save(roleAdmin);

		Role roleSuperAdmin = new Role();
		roleSuperAdmin.setName("SUPERADMIN");
		repositoryRole.save(roleSuperAdmin);


		User clien = new User();
		clien.setFirstName("client");
		clien.setLastName("sterk");
		clien.setEmail("bidonne@gmail.com");
		clien.setUsername("client59");
		clien.setPassword(passwordEncoder.encode("client69"));
		clien.setAddress("50 rue de la rue");
		clien.setRole(entityManager.merge(roleClient));
		repositoryUser.save(clien);


		User admin = new User();
		admin.setFirstName("adminnn");
		admin.setLastName("stark");
		admin.setEmail("bidon@gmail.com");
		admin.setUsername("admin59");
		admin.setPassword(passwordEncoder.encode("admin69"));
		admin.setAddress("59 rue de la rue");
		admin.setRole(entityManager.merge(roleAdmin));
		repositoryUser.save(admin);

		User superAdmin = new User();
		superAdmin.setFirstName("superadminn");
		superAdmin.setLastName("stark");
		superAdmin.setEmail("bigdon@gmail.com");
		superAdmin.setUsername("superAdmin59");
		superAdmin.setPassword(passwordEncoder.encode("superadmin69"));
		superAdmin.setAddress("69 rue de la rue");
		superAdmin.setRole(entityManager.merge(roleSuperAdmin));
		repositoryUser.save(superAdmin);

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
