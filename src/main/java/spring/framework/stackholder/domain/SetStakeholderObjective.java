package spring.framework.stackholder.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SetStakeholderObjective")
public class SetStakeholderObjective {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "setId")
    private Set setId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "setObjective")
    private SetObjective setObjective;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "setStakeholder")
    private SetStakeholder setStakeholder;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;




}
