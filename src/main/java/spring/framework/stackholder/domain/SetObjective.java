package spring.framework.stackholder.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "setId")
    private Set setId;

    @OneToMany(mappedBy = "setObjective",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<SetStakeholderObjective> setStakeholderObjective=new ArrayList<>();

    public SetObjective addPriority(SetStakeholderObjective setStakeholderObjective){
        setStakeholderObjective.setSetObjective(this);
        this.setStakeholderObjective.add(setStakeholderObjective);
        return this;
    }

}
