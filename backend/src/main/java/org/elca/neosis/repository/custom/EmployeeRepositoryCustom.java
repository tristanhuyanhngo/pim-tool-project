package org.elca.neosis.repository.custom;

import org.elca.neosis.model.entity.Employee;

import java.util.List;
import java.util.Set;

public interface EmployeeRepositoryCustom {
    boolean validateVisa(String visa);
    List<Employee> findAllByVisa(Set<String> visas);
}
