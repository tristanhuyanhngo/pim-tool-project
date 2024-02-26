package org.elca.neosis.util;

import org.elca.neosis.model.dto.CountConditionDTO;
import org.elca.neosis.model.dto.ProjectDTO;
import org.elca.neosis.model.dto.SearchConditionDTO;
import org.elca.neosis.proto.CountCondition;
import org.elca.neosis.proto.NewProject;
import org.elca.neosis.proto.SearchCondition;
import org.elca.neosis.service.ProjectServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static org.elca.neosis.service.ProjectServiceImpl.DATE_TIME_FORMAT_PATTER;

public class ApplicationMapper {
    public static LocalDate convertToLocalDate(String date) {
        return date.isEmpty() ? null : LocalDate.parse(date, DateTimeFormatter.ofPattern(ProjectServiceImpl.DATE_TIME_FORMAT_PATTER));
    }

    public static SearchConditionDTO mapSearchConditionProtoToDTO(SearchCondition proto) {
        SearchConditionDTO dto = new SearchConditionDTO();

        dto.setKeywords(proto.getKeywords());
        dto.setHaveStatus(proto.getHaveStatus());
        dto.setStatus(proto.getStatus());
        dto.setPageSize(proto.getPageSize());
        dto.setPageNumber(proto.getPageNumber());

        return dto;
    }

    public static CountConditionDTO mapCountConditionProtoToDTO(CountCondition proto) {
        CountConditionDTO dto = new CountConditionDTO();

        dto.setKeywords(proto.getKeywords());
        dto.setHaveStatus(proto.getHaveStatus());
        dto.setStatus(proto.getStatus());

        return dto;
    }

    public static ProjectDTO mapProjectProtoToDTO(NewProject proto) {
        ProjectDTO dto = new ProjectDTO();

        dto.setNumber(proto.getNumber());
        dto.setName(proto.getName());
        dto.setStatus(proto.getStatus());
        dto.setCustomer(proto.getCustomer());
        dto.setStartDate(convertToLocalDate(proto.getStartDate()));
        dto.setEndDate(convertToLocalDate(proto.getEndDate()));
        dto.setGroupId(proto.getGroupId());
        dto.setMembers(new HashSet<>(proto.getMembersList()));

        return dto;
    }
}
