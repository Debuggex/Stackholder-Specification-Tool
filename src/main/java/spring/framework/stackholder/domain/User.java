package spring.framework.stackholder.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Customer")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Name", length = 100)
    private String username;

    @Column(name = "FirstName", length = 40)
    private String firstName;

    @Column(name = "LastName", length = 40)
    private String lastName;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "Password")
    private String password;

    @Column(name = "Active", length = 100)
    private Boolean isActive = false;

    @Column(name = "Admin")
    private Boolean isAdmin = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "userId")
    private List<Set> sets=new ArrayList<>();


    public User addSet(Set set){
        set.setUserId(this);
        this.sets.add(set);
        return this;
    }

}
