package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
//    User findByAuthorities(Authorities authorities);
}
