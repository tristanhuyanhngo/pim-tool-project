package org.elca.neosis.repository.custom.Impl;
import com.querydsl.core.BooleanBuilder;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.elca.neosis.model.dto.CountConditionDTO;
import org.elca.neosis.model.dto.ProjectDTO;
import org.elca.neosis.model.dto.SearchConditionDTO;
import org.elca.neosis.model.entity.Group;
import org.elca.neosis.model.entity.Project;
import org.elca.neosis.model.entity.QEmployee;
import org.elca.neosis.model.entity.QProject;
import org.elca.neosis.proto.ProjectStatus;
import org.elca.neosis.repository.custom.ProjectRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Project> findAllProjects() {
        QProject project = QProject.project;
        return new JPAQuery<>(em)
                .select(project)
                .from(project)
                .fetch();
    }

    @Override
    public List<Project> findAllProjectsWithCondition(SearchConditionDTO condition) {
        QProject project = QProject.project;
        // Get condition values
        String keywords = condition.getKeywords();
        boolean haveStatus = condition.getHaveStatus();
        ProjectStatus status = condition.getStatus();
        int pageSize = condition.getPageSize();
        int pageNumber = condition.getPageNumber();

        BooleanBuilder builder = buildSearchQuery(project, keywords, status);

        JPAQuery<Project> query = new JPAQuery<>(em)
                .select(project)
                .from(project)
                .where(builder)
                .orderBy(project.number.asc());

        if (haveStatus) {
            query.where(project.status.eq(status));
        }

        return query
                .limit(pageSize)
                .offset((long) pageNumber * pageSize)
                .fetch();
    }

    @Override
    public List<Long> findAllProjectIDsByProjectNumber(List<Integer> projectNumbers) {
        return new JPAQuery<>(em)
                .select(QProject.project.id)
                .from(QProject.project)
                .where(QProject.project.number.in(projectNumbers))
                .fetch();
    }


    @Override
    public long countProjectsWithCondition(CountConditionDTO condition) {
        QProject project = QProject.project;
        // Get condition values
        String keywords = condition.getKeywords();
        boolean haveStatus = condition.getHaveStatus();
        ProjectStatus status = condition.getStatus();

        BooleanBuilder builder = buildSearchQuery(project, keywords, status);

        JPAQuery<Project> query = new JPAQuery<>(em)
                .select(project)
                .from(project)
                .where(builder);

        if (haveStatus) {
            query.where(project.status.eq(status));
        }

        return query.fetchCount();
    }

    @Override
    public void deleteMultipleProjectsByIDs(List<Long> projectIDs) {
        QProject project = QProject.project;
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        queryFactory.delete(project)
                .where(project.id.in(projectIDs))
                .execute();
    }

    @Override
    public Project findProjectByID(Long id) {
        QProject project = QProject.project;

        return new JPAQuery<>(em)
                .select(project)
                .from(project)
                .where(project.id.eq(id))
                .fetchOne();
    }

    @Override
    public Project findProjectByProjectNumber(Integer number) {
        QProject project = QProject.project;

        return new JPAQuery<>(em)
                .select(project)
                .from(project)
                .where(project.number.eq(number))
                .fetchOne();
    }

    @Override
    public boolean findProjectNumber(Integer number) {
        QProject project = QProject.project;
        long count = new JPAQuery<>(em)
                .from(project)
                .where(project.number.eq(number))
                .fetchCount();
        return count > 0;
    }

    private BooleanBuilder buildSearchQuery(QProject project, String keywords, ProjectStatus status) {
        BooleanBuilder builder = new BooleanBuilder();
        // In case of the keywords is a numeric
        if (StringUtils.isNumeric(keywords)) {
            builder.or(project.number.eq(Integer.parseInt(keywords)));
        }
        // General case
        builder.or(project.name.containsIgnoreCase(keywords).or(project.customer.containsIgnoreCase(keywords)));

        return builder;
    }
}
