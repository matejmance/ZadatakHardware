package hr.java.web.zadatakhardware.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authority")
@Getter
@Setter
@NoArgsConstructor
public class UserRole {

    @Id
    private Long id;

    @Column(name = "authority_name", nullable = false, unique = true)
    private String name;
}