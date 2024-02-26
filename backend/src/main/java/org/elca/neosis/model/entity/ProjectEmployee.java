package org.elca.neosis.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "PROJECT_EMPLOYEE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEmployee {
    @EmbeddedId
    private ProjectEmployeeID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "PROJECT_ID", referencedColumnName = "id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "id")
    private Employee employee;
}
