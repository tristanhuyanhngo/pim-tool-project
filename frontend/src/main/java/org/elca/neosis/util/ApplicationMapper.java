package org.elca.neosis.util;

import org.elca.neosis.common.ApplicationBundleKey;
import org.elca.neosis.proto.ProjectStatus;

import java.util.ResourceBundle;

public class ApplicationMapper {
    public static ProjectStatus convertToProjectStatus(String status, ResourceBundle resourceBundle) {
        if (resourceBundle.getString(ApplicationBundleKey.PROJECT_NEW_STATUS_KEY).equals(status)) {
            return ProjectStatus.NEW;
        }
        if (resourceBundle.getString(ApplicationBundleKey.PROJECT_PLANNED_STATUS_KEY).equals(status)) {
            return ProjectStatus.PLA;
        }
        if (resourceBundle.getString(ApplicationBundleKey.PROJECT_IN_PROGRESS_STATUS_KEY).equals(status)) {
            return ProjectStatus.INP;
        }
        if (resourceBundle.getString(ApplicationBundleKey.PROJECT_FINISHED_STATUS_KEY).equals(status)) {
            return ProjectStatus.FIN;
        }
        return null;
    }
    public static String convertToProjectStatus(ProjectStatus status, ResourceBundle resourceBundle) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case NEW:
                return resourceBundle.getString(ApplicationBundleKey.PROJECT_NEW_STATUS_KEY);
            case PLA:
                return resourceBundle.getString(ApplicationBundleKey.PROJECT_PLANNED_STATUS_KEY);
            case INP:
                return resourceBundle.getString(ApplicationBundleKey.PROJECT_IN_PROGRESS_STATUS_KEY);
            case FIN:
                return resourceBundle.getString(ApplicationBundleKey.PROJECT_FINISHED_STATUS_KEY);
            default:
                return null;
        }
    }

    public static ProjectStatus convertToProjectStatus(String status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case "New":
                return ProjectStatus.NEW;
            case "Planned":
                return ProjectStatus.PLA;
            case "In progress":
                return ProjectStatus.INP;
            case "Finished":
                return ProjectStatus.FIN;
            default:
                return null;
        }
    }
}
