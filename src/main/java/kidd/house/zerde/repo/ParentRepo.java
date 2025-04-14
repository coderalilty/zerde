package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepo extends JpaRepository<Parent,Long> {
    @Query("SELECT p.chatId FROM Parent p WHERE p.id = :id")
    Optional<String> findByChatIdWhereParentId(@Param("id") Long id);
    @Query("SELECT p.chatId FROM Parent p")
    Optional<Long> findByChatId(Long chat_id);

    Parent findByParentPhoneAndParentEmail(
            String parentPhone,
            String parentEmail
    );
}
