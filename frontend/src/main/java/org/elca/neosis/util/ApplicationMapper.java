package org.elca.neosis.util;

import org.elca.neosis.common.ApplicationBundleKey;
import org.elca.neosis.multilingual.I18N;
import org.elca.neosis.proto.ProjectStatus;

public class ApplicationMapper {
    public static ProjectStatus convertToProjectStatus(String status) {
        if (status == null) {
            return null;
        }
        if (I18N.get(ApplicationBundleKey.PROJECT_NEW_STATUS_KEY).equals(status)) {
            return ProjectStatus.NEW;
        } else if (I18N.get(ApplicationBundleKey.PROJECT_PLANNED_STATUS_KEY).equals(status)) {
            return ProjectStatus.PLA;
        } else if (I18N.get(ApplicationBundleKey.PROJECT_IN_PROGRESS_STATUS_KEY).equals(status)) {
            return ProjectStatus.INP;
        } else if (I18N.get(ApplicationBundleKey.PROJECT_FINISHED_STATUS_KEY).equals(status)) {
            return ProjectStatus.FIN;
        }
        return null;
    }

    public static String convertToProjectStatus(ProjectStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case NEW:
                return I18N.get(ApplicationBundleKey.PROJECT_NEW_STATUS_KEY);
            case PLA:
                return I18N.get(ApplicationBundleKey.PROJECT_PLANNED_STATUS_KEY);
            case INP:
                return I18N.get(ApplicationBundleKey.PROJECT_IN_PROGRESS_STATUS_KEY);
            case FIN:
                return I18N.get(ApplicationBundleKey.PROJECT_FINISHED_STATUS_KEY);
            default:
                return null;
        }
    }
}
