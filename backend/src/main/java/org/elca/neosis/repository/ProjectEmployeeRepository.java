package org.elca.neosis.repository;

import org.elca.neosis.model.entity.Group;
import org.elca.neosis.model.entity.Project;
import org.elca.neosis.model.entity.ProjectEmployee;
import org.elca.neosis.repository.custom.ProjectEmployeeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectEmployeeRepository extends JpaRepository<ProjectEmployee, Long>, ProjectEmployeeRepositoryCustom {

}
