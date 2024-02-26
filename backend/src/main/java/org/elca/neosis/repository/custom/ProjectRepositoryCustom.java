package org.elca.neosis.repository.custom;

import org.elca.neosis.model.dto.CountConditionDTO;
import org.elca.neosis.model.dto.ProjectDTO;
import org.elca.neosis.model.dto.SearchConditionDTO;
import org.elca.neosis.model.entity.Group;
import org.elca.neosis.model.entity.Project;

import java.util.List;

public interface ProjectRepositoryCustom {
    Project findProjectByID(Long id);
    Project findProjectByProjectNumber(Integer number);
    boolean findProjectNumber(Integer number);
    List<Project> findAllProjects();
    List<Project> findAllProjectsWithCondition(SearchConditionDTO condition);
    List<Long> findAllProjectIDsByProjectNumber(List<Integer> projectNumbers);
    long countProjectsWithCondition(CountConditionDTO condition);
    void deleteMultipleProjectsByIDs(List<Long> projectIDs);
}
