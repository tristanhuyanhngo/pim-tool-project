package org.elca.neosis.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "EMPLOYEE")
@Getter
@Setter
public class Employee extends SuperClassEntity {
    @Column(name = "VISA", length = 3, unique = true, nullable = false)
    private String visa;

    @Column(name = "FIRST_NAME", length = 50, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 50, nullable = false)
    private String lastName;

    @Column(name = "BIRTH_DATE", nullable = false)
    private LocalDate birthDate;

    @OneToOne(mappedBy = "leader", fetch = FetchType.LAZY)
    private Group managedGroup;

    @OneToMany(mappedBy = "employee")
    private Set<ProjectEmployee> projectEmployees = new HashSet<>();
}
