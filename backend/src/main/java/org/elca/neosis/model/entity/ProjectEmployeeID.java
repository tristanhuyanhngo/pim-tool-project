package org.elca.neosis.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEmployeeID implements Serializable {
    @Column(name = "PROJECT_ID")
    private Long projectId;

    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;
}

