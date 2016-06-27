package com.daggerok;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ratpack.handling.Handler;
import ratpack.spring.config.EnableRatpack;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.stream.Stream;

@Slf4j
@EnableRatpack
@SpringBootApplication
public class GtplApplication extends RepositoryRestConfigurerAdapter {

    @Controller
    static class IndexPage {
        @RequestMapping("/")
        public String index(Model model) {
            model.addAttribute("message", new Message()
                    .setId(-1L)
                    .setText("ololo")
                    .setSummary("great!"));
            return "index";
        }
    }

    @Bean public Handler handler() {
        return context -> context.render("Hello World");
    }

    @Override public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(User.class);
    }

    @Bean CommandLineRunner runner(UserRepository userRepository) {

        Stream.of("Max", "Fax", "Bax")
                .map(User::new)
                .forEach(userRepository::save);

        return args -> userRepository.findAll().stream()
                .map(Object::toString);
    }

    public static void main(String[] args) {
        SpringApplication.run(GtplApplication.class, args);
    }
}

@Data
@NoArgsConstructor
@Accessors(chain = true)
class Message implements Serializable {

    Long id;
    @NotEmpty(message = "text is required") String text;
    @NotEmpty(message = "summary is required") String summary;
    LocalDate created = LocalDate.now();
}


@RepositoryRestResource
interface UserRepository extends MongoRepository<User, String> {}

@Data
@Document
@NoArgsConstructor
@RequiredArgsConstructor
class User implements Serializable {

    @Id String id;
    @NonNull String username;
}
