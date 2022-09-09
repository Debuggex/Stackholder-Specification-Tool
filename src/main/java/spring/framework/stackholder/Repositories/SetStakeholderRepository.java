package spring.framework.stackholder.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.framework.stackholder.domain.SetStakeholder;

@Repository
public interface SetStakeholderRepository extends JpaRepository<SetStakeholder,Long> {
}
