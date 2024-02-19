package org.elca.neosis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.elca.neosis.proto.ProjectStatus;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class ProjectDTO {
    private Integer number;
    private String name;
    private String customer;
    private Long groupId;
    private List<String> members;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
