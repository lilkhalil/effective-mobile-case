package ru.mirea.lilkhalil.tasks;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.repository.UserRepository;

@SpringBootApplication
public class TasksManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasksManagementApplication.class, args);
    }

    @Bean
    @SuppressWarnings("java:S6437")
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                User admin = User.builder()
                        .firstName("Тестовый")
                        .lastName("Пользователь")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("Test123456!"))
                        .role(User.Role.ROLE_ADMIN)
                        .build();
                userRepository.save(admin);
            }
        };
    }

}
