package org.elca.neosis.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ListToDetailMessage {
    private String fragmentId;
    private Integer projectNumber;
}
