package org.elca.neosis.repository.custom.Impl;
import com.querydsl.core.BooleanBuilder;

import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.elca.neosis.model.dto.CountConditionDTO;
import org.elca.neosis.model.dto.SearchConditionDTO;
import org.elca.neosis.model.entity.Project;
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

        JPAQuery<Project> query = buildSearchQuery(project, keywords, haveStatus, status);

        return query
                .limit(pageSize)
                .offset((long) pageNumber * pageSize)
                .fetch();
    }


    @Override
    public long countProjectsWithCondition(CountConditionDTO condition) {
        QProject project = QProject.project;
        // Get condition values
        String keywords = condition.getKeywords();
        boolean haveStatus = condition.getHaveStatus();
        ProjectStatus status = condition.getStatus();

        JPAQuery<Project> query = buildSearchQuery(project, keywords, haveStatus, status);

        return query.fetchCount();
    }

    private JPAQuery<Project> buildSearchQuery(QProject project, String keywords, boolean haveStatus, ProjectStatus status) {
        BooleanBuilder builder = new BooleanBuilder();

        // In case of the keywords is a numeric
        if (StringUtils.isNumeric(keywords)) {
            builder.or(project.number.eq(Integer.parseInt(keywords)));
        }
        // General case
        builder.or(project.name.containsIgnoreCase(keywords).or(project.customer.containsIgnoreCase(keywords)));

        JPAQuery<Project> query = new JPAQuery<>(em)
                .select(project)
                .from(project)
                .where(builder);

        if (haveStatus) {
            query.where(project.status.eq(status));
        }

        return query;
    }
}
