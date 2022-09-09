package spring.framework.stackholder.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SetObjective")
public class SetObjective {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name" ,length = 40)
    private String name;

    @Nullable
    @Column(name = "description" ,length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "setId")
    private Set setId;

    @OneToOne(mappedBy = "setObjective",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private SetStakeholderObjective setStakeholderObjective;

    public SetObjective addPriority(SetStakeholderObjective setStakeholderObjective){
        setStakeholderObjective.setSetObjective(this);
        this.setStakeholderObjective=setStakeholderObjective;
        return this;
    }

}
