package spring.framework.stackholder.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.framework.stackholder.domain.SetObjective;

@Repository
public interface SetObjectiveRespository extends JpaRepository<SetObjective,Long> {
}
