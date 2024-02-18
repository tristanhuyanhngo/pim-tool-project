package org.elca.neosis.repository.custom.Impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.elca.neosis.model.entity.ProjectEmployee;
import org.elca.neosis.model.entity.ProjectEmployeeID;
import org.elca.neosis.model.entity.QProjectEmployee;
import org.elca.neosis.repository.custom.ProjectEmployeeRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectEmployeeRepositoryCustomImpl implements ProjectEmployeeRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ProjectEmployee> getAllByProjectID(Long projectID) {
        QProjectEmployee projectEmployee = QProjectEmployee.projectEmployee;

        return new JPAQuery<>(em)
                .select(projectEmployee)
                .from(projectEmployee)
                .where(projectEmployee.project.id.eq(projectID))
                .fetch();
    }

    @Override
    public List<ProjectEmployee> getAllByListProjectID(List<Long> projectIDs) {
        QProjectEmployee projectEmployee = QProjectEmployee.projectEmployee;

        return new JPAQuery<>(em)
                .select(projectEmployee)
                .from(projectEmployee)
                .where(projectEmployee.project.id.in(projectIDs))
                .fetch();
    }

    @Override
    public void deleteProjectEmployees(List<ProjectEmployee> projectEmployees) {
        QProjectEmployee projectEmployee = QProjectEmployee.projectEmployee;
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<ProjectEmployeeID> projectEmployeeIDs = projectEmployees.stream()
                .map(ProjectEmployee::getId)
                .collect(Collectors.toList());

        queryFactory
                .delete(projectEmployee)
                .where(projectEmployee.id.in(projectEmployeeIDs))
                .execute();
    }
}
