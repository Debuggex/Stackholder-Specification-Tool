package spring.framework.stackholder.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.framework.stackholder.domain.Objective;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective,Long> {
}
