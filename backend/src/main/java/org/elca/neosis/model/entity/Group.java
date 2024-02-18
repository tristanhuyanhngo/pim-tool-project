package org.elca.neosis.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "GROUP_TBL")
@Getter
@Setter
public class Group extends SuperClassEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_LEADER_ID", nullable = false)
    private Employee leader;

    @OneToMany(mappedBy = "group")
    private Set<Project> projects = new HashSet<>();
}
