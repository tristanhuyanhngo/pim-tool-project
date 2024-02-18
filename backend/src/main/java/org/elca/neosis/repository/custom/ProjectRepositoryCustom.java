package org.elca.neosis.repository.custom;

import org.elca.neosis.model.dto.CountConditionDTO;
import org.elca.neosis.model.dto.SearchConditionDTO;
import org.elca.neosis.model.entity.Project;

import java.util.List;

public interface ProjectRepositoryCustom {
    List<Project> findAllProjects();

    List<Project> findAllProjectsWithCondition(SearchConditionDTO condition);
    List<Long> findAllProjectIDsByProjectNumber(List<Integer> projectNumbers);

    long countProjectsWithCondition(CountConditionDTO condition);

    void deleteMultipleProjectsByIDs(List<Long> projectIDs);
    Project findProjectByProjectNumber(Integer number);
}
