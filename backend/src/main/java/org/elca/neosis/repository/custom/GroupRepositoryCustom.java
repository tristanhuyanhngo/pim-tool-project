package org.elca.neosis.repository.custom;

import org.elca.neosis.model.entity.Group;

import java.util.List;

public interface GroupRepositoryCustom {
    List<Long> getAllGroupIDs();
    Group getGroupById(Long id);
}
