package org.elca.neosis.repository.custom;

import org.elca.neosis.model.entity.ProjectEmployee;

import java.util.List;

public interface ProjectEmployeeRepositoryCustom {
    List<ProjectEmployee> getAllByProjectID(Long projectID);
    List<ProjectEmployee> getAllByListProjectID(List<Long> projectIDs);
    void deleteProjectEmployees(List<ProjectEmployee> projectEmployees);
}
