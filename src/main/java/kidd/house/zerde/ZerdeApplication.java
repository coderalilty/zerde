package kidd.house.zerde;

import kidd.house.zerde.model.entity.User;
import kidd.house.zerde.model.role.Authorities;
import kidd.house.zerde.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ZerdeApplication implements CommandLineRunner {
    @Autowired
    private UserRepo userRepo;

    public static void main(String[] args) {
        SpringApplication.run(ZerdeApplication.class, args);
    }
    public void run(String... args){
        User adminAccount = userRepo.findByAuthorities(Authorities.ADMIN);
        if (null == adminAccount){
            User user = new User();

            user.setEmail("admin@gmail.com");
            user.setName("admin");
            user.setLogin("admin");
            user.setAuthorities(Authorities.ADMIN);
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            userRepo.save(user);
        }
    }
}
