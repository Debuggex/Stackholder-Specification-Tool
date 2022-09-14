package spring.framework.stackholder.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.framework.stackholder.domain.Set;

@Repository
public interface SetRespository extends JpaRepository<Set,Long> {
}
