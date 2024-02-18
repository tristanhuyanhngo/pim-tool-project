package org.elca.neosis.util;

import org.elca.neosis.model.dto.CountConditionDTO;
import org.elca.neosis.model.dto.SearchConditionDTO;
import org.elca.neosis.proto.CountCondition;
import org.elca.neosis.proto.SearchCondition;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.elca.neosis.service.ProjectServiceImpl.DATE_TIME_FORMAT_PATTER;

public class ApplicationMapper {
    public static String formatDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER));
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
}
