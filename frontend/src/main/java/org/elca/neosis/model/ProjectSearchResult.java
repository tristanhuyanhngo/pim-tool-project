package org.elca.neosis.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
@Setter
public class ProjectSearchResult {
    private int number;
    private String name;
    private String status;
    private String customer;
    private String startDate;
}
