package spring.framework.stackholder.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.framework.stackholder.domain.SetStakeholderObjective;

@Repository
public interface SetStakeholderObjectiveRespository extends JpaRepository<SetStakeholderObjective,Long> {
}
