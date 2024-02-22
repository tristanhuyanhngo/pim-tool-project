package org.elca.neosis.util;

import org.elca.neosis.common.ApplicationBundleKey;
import org.elca.neosis.fragment.ProjectDetailFragment;
import org.elca.neosis.multilingual.I18N;
import org.elca.neosis.proto.ProjectStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ApplicationMapper {
    public static ProjectStatus convertToProjectStatus(String status) {
        if (status == null) {
            return null;
        }

        List<String> newStatusList = I18N.getAll(ApplicationBundleKey.PROJECT_NEW_STATUS_KEY);
        List<String> plaStatusList = I18N.getAll(ApplicationBundleKey.PROJECT_PLANNED_STATUS_KEY);
        List<String> inpStatusList = I18N.getAll(ApplicationBundleKey.PROJECT_IN_PROGRESS_STATUS_KEY);
        List<String> finStatusList = I18N.getAll(ApplicationBundleKey.PROJECT_FINISHED_STATUS_KEY);

        if (newStatusList.contains(status)) {
            return ProjectStatus.NEW;
        } else if (plaStatusList.contains(status)) {
            return ProjectStatus.PLA;
        } else if (inpStatusList.contains(status)) {
            return ProjectStatus.INP;
        } else if (finStatusList.contains(status)) {
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
