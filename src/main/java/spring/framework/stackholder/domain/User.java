package spring.framework.stackholder.domain;

import lombok.*;

import javax.persistence.*;

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

    @Column(name="Name", length = 100)
    private String username;

    @Column(name="FirstName", length = 40)
    private String firstName;

    @Column(name="LastName", length = 40)
    private String lastName;

    @Column(name="Email", length = 100)
    private String email;

    @Column(name="Password")
    private String password;

    @Column(name="Active", length = 100)
    private Boolean isActive=false;

}
