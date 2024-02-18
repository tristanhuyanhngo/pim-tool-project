package org.elca.neosis.service;

import io.grpc.stub.StreamObserver;
import org.elca.neosis.model.dto.CountConditionDTO;
import org.elca.neosis.model.dto.SearchConditionDTO;
import org.elca.neosis.model.entity.Project;
import org.elca.neosis.proto.*;
import org.elca.neosis.repository.ProjectRepository;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.elca.neosis.util.ApplicationMapper;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@GRpcService
public class ProjectServiceImpl extends ProjectServiceGrpc.ProjectServiceImplBase {
    public static final String DATE_TIME_FORMAT_PATTER = "dd.MM.yyyy";
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public void getAllProjects(Empty request, StreamObserver<SearchResult> responseObserver) {
        List<Project> projects = projectRepository.findAllProjects();
        projects.forEach(project -> responseObserver.onNext(
                SearchResult.newBuilder()
                     .setNumber(project.getNumber())
                     .setName(project.getName())
                     .setStatus(project.getStatus())
                     .setCustomer(project.getCustomer())
                     .setStartDate(project.getStartDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)))
                     .build()));
        responseObserver.onCompleted();
    }

    @Override
    public void countAllProjectWithConditions(CountCondition condition, StreamObserver<CountProjectResponse> responseObserver) {
        CountConditionDTO dto = ApplicationMapper.mapCountConditionProtoToDTO(condition);
        long count = projectRepository.countProjectsWithCondition(dto);
        CountProjectResponse response = CountProjectResponse.newBuilder()
                .setQuantity(count)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void searchProject(SearchCondition condition, StreamObserver<SearchResult> responseObserver) {
        SearchConditionDTO dto = ApplicationMapper.mapSearchConditionProtoToDTO(condition);
        List<Project> projects = projectRepository.findAllProjectsWithCondition(dto);
                projects.forEach(project -> responseObserver.onNext(
                SearchResult.newBuilder()
                        .setNumber(project.getNumber())
                        .setName(project.getName())
                        .setStatus(project.getStatus())
                        .setCustomer(project.getCustomer())
                        .setStartDate(project.getStartDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)))
                        .build()));
        responseObserver.onCompleted();
    }
}
