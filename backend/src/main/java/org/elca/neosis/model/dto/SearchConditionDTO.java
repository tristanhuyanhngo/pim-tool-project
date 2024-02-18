package org.elca.neosis.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.elca.neosis.proto.ProjectStatus;

@Getter
@Setter
public class SearchConditionDTO {
    private String keywords;
    private boolean haveStatus;
    private ProjectStatus status;
    private int pageSize;
    private int pageNumber;
    public boolean getHaveStatus() {
        return this.haveStatus;
    }
}
