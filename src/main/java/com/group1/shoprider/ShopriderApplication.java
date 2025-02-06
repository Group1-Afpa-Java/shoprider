package com.group1.shoprider;

import com.group1.shoprider.models.Role;
import com.group1.shoprider.repository.RepositoryRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
public class ShopriderApplication implements CommandLineRunner {
	private final RepositoryRole repositoryRole;

	public static void main(String[] args) {
		SpringApplication.run(ShopriderApplication.class, args);
	}


	@Override
	public void run(String... args) {
		Role role1 = new Role();
		role1.setName("CLIENT");

		Role role2 = new Role();
		role2.setName("ADMIN");

		repositoryRole.saveAll(List.of(role1, role2));
	}


}
