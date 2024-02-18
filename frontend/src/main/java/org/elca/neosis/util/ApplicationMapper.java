package org.elca.neosis.util;

import org.elca.neosis.proto.ProjectStatus;

public class ApplicationMapper {
    public static String convertToProjectStatus(ProjectStatus status) {
        switch (status) {
            case NEW:
                return "New";
            case PLA:
                return "Planned";
            case INP:
                return "In progress";
            case FIN:
                return "Finished";
            default:
                return null;
        }
    }

    public static ProjectStatus convertToProjectStatus(String status) {
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
