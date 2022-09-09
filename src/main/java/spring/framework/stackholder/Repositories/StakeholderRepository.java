package spring.framework.stackholder.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.framework.stackholder.domain.Stakeholder;

@Repository
public interface StakeholderRepository extends JpaRepository<Stakeholder,Long> {
}
