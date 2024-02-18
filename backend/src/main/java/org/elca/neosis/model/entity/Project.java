package org.elca.neosis.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.elca.neosis.proto.ProjectStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "PROJECT")
@Getter
@Setter
public class Project extends SuperClassEntity {
    @Column(name = "PROJECT_NUMBER", nullable = false, unique = true)
    private Integer number;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "CUSTOMER", length = 50, nullable = false)
    private String customer;

    @Column(name = "STATUS", length = 3, nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "project")
    private Set<ProjectEmployee> employeeProjects = new HashSet<>();
}
