package org.elca.neosis.model.entity;

import lombok.Data;

import javax.persistence.*;

@MappedSuperclass
@Data
public class SuperClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Version
    @Column(name = "VERSION", nullable = false)
    protected Integer version;
}