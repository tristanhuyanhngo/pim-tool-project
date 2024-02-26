package org.elca.neosis.repository;

import org.elca.neosis.model.entity.Employee;
import org.elca.neosis.model.entity.Project;
import org.elca.neosis.repository.custom.EmployeeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
}
