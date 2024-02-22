package org.elca.neosis.service;

import io.grpc.stub.StreamObserver;
import org.elca.neosis.model.dto.CountConditionDTO;
import org.elca.neosis.model.dto.ProjectDTO;
import org.elca.neosis.model.dto.SearchConditionDTO;
import org.elca.neosis.model.entity.*;
import org.elca.neosis.model.entity.Group;
import org.elca.neosis.model.entity.Project;
import org.elca.neosis.proto.*;
import org.elca.neosis.repository.EmployeeRepository;
import org.elca.neosis.repository.GroupRepository;
import org.elca.neosis.repository.ProjectEmployeeRepository;
import org.elca.neosis.repository.ProjectRepository;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.elca.neosis.util.ApplicationMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@GRpcService
public class ProjectServiceImpl extends ProjectServiceGrpc.ProjectServiceImplBase {
    public static final String DATE_TIME_FORMAT_PATTER = "dd.MM.yyyy";
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectEmployeeRepository projectEmployeeRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private GroupRepository groupRepository;

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
    public void getProjectByNumber(ProjectNumber request, StreamObserver<org.elca.neosis.proto.Project> responseObserver) {
        int projectNumber = request.getNumber();
        Project project = projectRepository.findProjectByProjectNumber(projectNumber);
        if (project == null) {
            responseObserver.onNext(org.elca.neosis.proto.Project.newBuilder()
                    .setIsExisted(false)
                    .build());
        } else {
            Set<ProjectEmployee> projectEmployees = project.getEmployeeProjects();
            List<Long> employeeIds = projectEmployees.stream()
                    .map(projectEmployee -> projectEmployee.getId().getEmployeeId())
                    .collect(Collectors.toList());
            List<String> employeeVisas = employeeRepository.getAllEmployeeVisasByIdIn(employeeIds);
            responseObserver.onNext(org.elca.neosis.proto.Project.newBuilder()
                    .setId(project.getId())
                    .setNumber(projectNumber)
                    .setName(project.getName())
                    .setCustomer(project.getCustomer())
                    .setStartDate(project.getStartDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)))
                    .setEndDate(Objects.isNull(project.getEndDate()) ? "" : project.getEndDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)))
                    .setVersion(project.getVersion())
                    .setGroupId(project.getGroup().getId())
                    .setStatus(project.getStatus())
                    .addAllMembers(employeeVisas)
                    .setIsExisted(true)
                    .build());
        }
        responseObserver.onCompleted();
    }


    @Override
    public void countAllProjectWithConditions(CountCondition request, StreamObserver<CountProjectResponse> responseObserver) {
        CountConditionDTO dto = ApplicationMapper.mapCountConditionProtoToDTO(request);
        long count = projectRepository.countProjectsWithCondition(dto);
        CountProjectResponse response = CountProjectResponse.newBuilder()
                .setQuantity(count)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void searchProject(SearchCondition request, StreamObserver<SearchResult> responseObserver) {
        SearchConditionDTO dto = ApplicationMapper.mapSearchConditionProtoToDTO(request);
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

    @Override
    public void deleteProject(ListProjectNumber request, StreamObserver<DeleteProjectResponse> responseObserver) {
        try {
            List<Integer> projectNumbers = request.getNumberList();
            List<Long> projectIDs = projectRepository.findAllProjectIDsByProjectNumber(projectNumbers);

            if (projectIDs.size() != projectNumbers.size()) {
                sendDeleteProjectResponse(false, ResponseStatus.CAN_NOT_DELETE_PROJECT, responseObserver);
            } else {
                List<ProjectEmployee> projectEmployees = projectEmployeeRepository.getAllByListProjectID(projectIDs);
                projectEmployeeRepository.deleteProjectEmployees(projectEmployees);
                projectRepository.deleteMultipleProjectsByIDs(projectIDs);
                sendDeleteProjectResponse(true, ResponseStatus.OPERATION_SUCCESS, responseObserver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendDeleteProjectResponse(false, ResponseStatus.OTHER_ERROR, responseObserver);
        }
    }

    @Override
    public void createProject(NewProject request, StreamObserver<CreateUpdateProjectResponse> responseObserver) {
        CreateUpdateProjectResponse.Builder responseBuilder = createBusinessValidationBuilder(request, false);
        if (responseBuilder.getIsSuccess()) {
            try {
                ProjectDTO projectDTO = ApplicationMapper.mapProjectProtoToDTO(request);

                Project project = new Project();
                project.setNumber(projectDTO.getNumber());
                project.setName(projectDTO.getName());
                project.setCustomer(projectDTO.getCustomer());
                project.setStatus(projectDTO.getStatus());
                project.setStartDate(projectDTO.getStartDate());
                project.setEndDate(projectDTO.getEndDate());

                project.addGroup(groupRepository.getGroupById(projectDTO.getGroupId()));
                Project savedProject = projectRepository.save(project);
                List<Employee> employees = employeeRepository.findAllByVisa(new HashSet<>(request.getMembersList()));
                List<ProjectEmployee> projectEmployees = employees.stream()
                        .map(employee -> new ProjectEmployee(
                                new ProjectEmployeeID(
                                        savedProject.getId(),
                                        employee.getId()),
                                savedProject,
                                employee))
                        .collect(Collectors.toList());

                projectEmployeeRepository.saveAll(projectEmployees);
                responseBuilder.addStatus(ResponseStatus.OPERATION_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                responseBuilder.setIsSuccess(false);
                responseBuilder.addStatus(ResponseStatus.OTHER_ERROR);
            }
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateProject(org.elca.neosis.proto.Project request, StreamObserver<CreateUpdateProjectResponse> responseObserver) {
        CreateUpdateProjectResponse.Builder responseBuilder = createBusinessValidationBuilder(
                NewProject.newBuilder()
                        .setName(request.getName())
                        .setNumber(request.getNumber())
                        .setCustomer(request.getCustomer())
                        .setStartDate(request.getStartDate())
                        .setEndDate(request.getEndDate())
                        .addAllMembers(request.getMembersList())
                        .build(),
                true
        );

        if (responseBuilder.getIsSuccess()) {
            try {
                Project existedProject = projectRepository.findProjectByID(request.getId());
                // In case the project is not found in the database or is deleted just before the update commit
                if (existedProject == null || existedProject.getVersion() != request.getVersion()) {
                    responseBuilder
                            .setIsSuccess(false)
                            .addStatus(ResponseStatus.CAN_NOT_UPDATE_PROJECT);
                } else {
                    existedProject.setName(request.getName());
                    existedProject.setCustomer(request.getCustomer());
                    existedProject.setStatus(request.getStatus());
                    existedProject.setStartDate(ApplicationMapper.convertToLocalDate(request.getStartDate()));
                    existedProject.setEndDate(ApplicationMapper.convertToLocalDate(request.getEndDate()));
                    existedProject.addGroup(groupRepository.getGroupById(request.getGroupId()));

                    Project savedProject = projectRepository.save(existedProject);

                    List<ProjectEmployee> existingProjectEmployees = projectEmployeeRepository.getAllByProjectID(request.getId());
                    projectEmployeeRepository.deleteAll(existingProjectEmployees);

                    List<Employee> employees = employeeRepository.findAllByVisa(new HashSet<>(request.getMembersList()));
                    List<ProjectEmployee> projectEmployees = employees.stream()
                            .map(employee -> new ProjectEmployee(
                                    new ProjectEmployeeID(
                                            savedProject.getId(),
                                            employee.getId()),
                                    savedProject,
                                    employee))
                            .collect(Collectors.toList());

                    projectEmployeeRepository.saveAll(projectEmployees);
                    responseBuilder.addStatus(ResponseStatus.OPERATION_SUCCESS);
                }
            } catch (Exception e) {
                e.printStackTrace();
                responseBuilder.setIsSuccess(false);
                responseBuilder.addStatus(ResponseStatus.OTHER_ERROR);
            }
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    private CreateUpdateProjectResponse.Builder createBusinessValidationBuilder(NewProject newProject, boolean validProjectNumber) {
        CreateUpdateProjectResponse.Builder responseBuilder = CreateUpdateProjectResponse.newBuilder();
        responseBuilder.setIsSuccess(true);
        // Validation for Project Number
        if (!validProjectNumber && (projectRepository.findProjectNumber(newProject.getNumber()))) {
            responseBuilder
                    .setIsSuccess(false)
                    .addStatus(ResponseStatus.PROJECT_NUMBER_EXISTED);

        }
        // Validation for Visa
        Set<String> invalidVisas = getInvalidVisas(new HashSet<>(newProject.getMembersList()));
        if (!invalidVisas.isEmpty()) {
            responseBuilder
                    .setIsSuccess(false)
                    .addStatus(ResponseStatus.EMPLOYEE_VISAS_NOT_EXISTED)
                    .addAllInvalidVisa(invalidVisas);
        }
        // Validation for Dates
        if (isStartDateAfterEndDate(ApplicationMapper.convertToLocalDate(newProject.getStartDate()), ApplicationMapper.convertToLocalDate(newProject.getEndDate()))) {
            responseBuilder.setIsSuccess(false);
            responseBuilder.addStatus(ResponseStatus.START_DATE_AFTER_END_DATE);
        }

        return responseBuilder;
    }

    private Set<String> getInvalidVisas(Set<String> projectMembersVisas) {
        return projectMembersVisas.stream()
                .filter(visa -> !employeeRepository.validateVisa(visa))
                .collect(Collectors.toSet());
    }

    private void sendDeleteProjectResponse(boolean isSuccess, ResponseStatus status, StreamObserver<DeleteProjectResponse> responseObserver) {
        DeleteProjectResponse response = DeleteProjectResponse.newBuilder()
                .setIsSuccess(isSuccess)
                .setStatus(status)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public boolean isStartDateAfterEndDate(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && (startDate.isAfter(endDate) || startDate.isEqual(endDate));
    }
}
