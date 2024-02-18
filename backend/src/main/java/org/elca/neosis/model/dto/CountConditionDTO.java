package org.elca.neosis.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.elca.neosis.proto.ProjectStatus;

@Getter
@Setter
public class CountConditionDTO {
    private String keywords;
    private boolean haveStatus;
    private ProjectStatus status;
    public boolean getHaveStatus() {
        return this.haveStatus;
    }
}
