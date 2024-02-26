package org.elca.neosis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.elca.neosis.proto.ProjectStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class ProjectDTO {
    private Integer number;
    private String name;
    private String customer;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long groupId;
    private Set<String> members;
}
