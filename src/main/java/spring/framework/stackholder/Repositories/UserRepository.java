package spring.framework.stackholder.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.framework.stackholder.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
