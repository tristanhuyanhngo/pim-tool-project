package org.elca.neosis.repository.custom.Impl;

import com.querydsl.jpa.impl.JPAQuery;
import org.elca.neosis.model.entity.QGroup;
import org.elca.neosis.repository.custom.GroupRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class GroupRepositoryCustomImpl implements GroupRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Long> getAllGroupIDs() {
        QGroup group = QGroup.group;
        return new JPAQuery<>(em)
             .select(group.id)
             .from(group)
             .fetch();
    }
}
