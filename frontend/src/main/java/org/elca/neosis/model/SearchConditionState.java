package org.elca.neosis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.elca.neosis.proto.ProjectStatus;

@Getter
@Setter
@AllArgsConstructor
public class SearchConditionState {
    private static SearchConditionState state;
    private String keywords;
    private ProjectStatus status;
    public static SearchConditionState getInstance() {
        if (state == null) {
            state = new SearchConditionState("", null);
        }
        return state;
    }
}
