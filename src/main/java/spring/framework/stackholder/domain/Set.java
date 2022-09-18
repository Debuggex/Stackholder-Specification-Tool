package spring.framework.stackholder.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.lang.Nullable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Sets")
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name",length = 40)
    private String name;

    @Nullable
    @Column(name = "description",length = 500)
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;

    @OneToMany(mappedBy = "setId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SetStakeholder> setStakeholders =new ArrayList<>();

    @OneToMany(mappedBy = "setId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SetObjective> setObjectives=new ArrayList<>();

    @OneToMany(mappedBy = "setId",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<SetStakeholderObjective> setStakeholderObjective=new ArrayList<>();


    public Set addStakeholder(SetStakeholder setStakeholder){
        setStakeholder.setSetId(this);
        this.setStakeholders.add(setStakeholder);
        return this;
    }

    public Set addObjective(SetObjective setObjective){
        setObjective.setSetId(this);
        this.setObjectives.add(setObjective);
        return this;
    }

    public Set addPriority(SetStakeholderObjective setStakeholderObjective){
        setStakeholderObjective.setSetId(this);
        this.setStakeholderObjective.add(setStakeholderObjective);
        return this;
    }

}
