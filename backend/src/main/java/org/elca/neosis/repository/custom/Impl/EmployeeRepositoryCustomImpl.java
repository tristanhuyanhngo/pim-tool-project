package org.elca.neosis.repository.custom.Impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.elca.neosis.model.entity.QEmployee;
import org.elca.neosis.repository.custom.EmployeeRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean validateVisa(String visa) {
        QEmployee employee = QEmployee.employee;
        long count = new JPAQuery<>(em)
                .from(employee)
                .where(employee.visa.eq(visa))
                .fetchCount();
        return count > 0;
    }
}
