package org.elca.neosis.fragment;

import com.sun.javafx.css.converters.StringConverter;
import io.grpc.StatusRuntimeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.converter.CharacterStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import org.elca.neosis.component.MainContentComponent;
import org.elca.neosis.factory.ObservableResourceFactory;
import org.elca.neosis.grpc.Grpc;
import org.elca.neosis.model.ProjectSearchResult;
import org.elca.neosis.proto.*;
import org.elca.neosis.util.ApplicationMapper;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;
import org.jacpfx.rcp.context.Context;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static org.elca.neosis.util.ApplicationMapper.convertToProjectStatus;

@Fragment(
        id = ProjectDetailFragment.ID,
        viewLocation = "/fxml/ProjectDetailFragment.fxml",
        scope = Scope.PROTOTYPE,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME
)
public class ProjectDetailFragment {
    public static final String ID = "ProjectDetailFragment";
    public static final String FIELD_ERROR_CSS_CLASS = "project-detail-fragment-text-input-error";
    public static final String MEMBER_SEPARATOR_SYMBOL = ",";
    public static final String DATE_TIME_FORMAT_PATTER = "dd.MM.yyyy";
    private int currentSelectedStatusIndex = 0;
    @Resource
    private Context context;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label closeButton;

    @FXML
    private HBox errorContainer;

    @FXML
    private TextField projectNumberInput;

    @FXML
    private TextField projectNameInput;

    @FXML
    private TextField customerInput;

    @FXML
    private ComboBox<Long> groupInput;

    @FXML
    private TextField membersInput;

    @FXML
    private ComboBox<String> statusInput;

    @FXML
    private DatePicker startDateInput;

    @FXML
    private DatePicker endDateInput;

    @FXML
    private Label projectVisaNotValid;
    @FXML
    private Label projectNumberExistedMessage;
    @FXML
    private Label invalidVisasMessage;
    @FXML
    private Label closeBtnErrorContainer;
    @FXML
    public void handleCreateProject() {
        hideErrorMessage();
        removeErrorClassFromTextFields();
        List<Node> requiredEmptyTextFields = getEmptyRequiredInputFields();
        if (!requiredEmptyTextFields.isEmpty()) {
            errorContainer.setVisible(true);
            requiredEmptyTextFields.forEach(textField ->
                    textField.getStyleClass().add(FIELD_ERROR_CSS_CLASS));
        } else {
            LocalDate endDate = endDateInput.getValue();
            Set<String> members = Arrays.stream(membersInput.getText().split(MEMBER_SEPARATOR_SYMBOL))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            NewProject request = NewProject.newBuilder()
                    .setNumber(Integer.parseInt(projectNumberInput.getText()))
                    .setName(projectNameInput.getText())
                    .setCustomer(customerInput.getText())
                    .setGroupId(groupInput.getValue())
                    .setStatus(ApplicationMapper.convertToProjectStatus(statusInput.getValue()))
                    .addAllMembers(members)
                    .setStartDate(startDateInput.getValue().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)))
                    .setEndDate(!Objects.isNull(endDate) ? endDate.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)) : "")
                    .build();
            try {
                Grpc client = Grpc.getInstance();
                ProjectServiceGrpc.ProjectServiceBlockingStub stub = client.getProjectServiceStub();
                CreateUpdateProjectResponse response = stub.createProject(request);
                handleProjectResponse(response);
            } catch (StatusRuntimeException e) {
                context.send(MainContentComponent.ID, ConnectionErrorFragment.ID);
            }
        }
    }

    public void editProject(Project project) {
        hideErrorMessage();
        removeErrorClassFromTextFields();
        List<Node> requiredEmptyTextFields = getEmptyRequiredInputFields();
        if (!requiredEmptyTextFields.isEmpty()) {
            errorContainer.setVisible(true);
            requiredEmptyTextFields.forEach(textField ->
                    textField.getStyleClass().add(FIELD_ERROR_CSS_CLASS));
        } else {
            LocalDate endDate = endDateInput.getValue();
            Set<String> members = Arrays.stream(membersInput.getText().split(MEMBER_SEPARATOR_SYMBOL))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            Project request = Project.newBuilder()
                    .setId(project.getId())
                    .setVersion(project.getVersion())
                    .setNumber(Integer.parseInt(projectNumberInput.getText()))
                    .setName(projectNameInput.getText())
                    .setCustomer(customerInput.getText())
                    .setGroupId(groupInput.getValue())
                    .setStatus(ApplicationMapper.convertToProjectStatus(statusInput.getValue()))
                    .addAllMembers(members)
                    .setStartDate(startDateInput.getValue().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)))
                    .setEndDate(!Objects.isNull(endDate) ? endDate.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)) : "")
                    .build();
            try {
                Grpc client = Grpc.getInstance();
                ProjectServiceGrpc.ProjectServiceBlockingStub stub = client.getProjectServiceStub();
                CreateUpdateProjectResponse response = stub.updateProject(request);
                handleProjectResponse(response);
            } catch (StatusRuntimeException e) {
                context.send(MainContentComponent.ID, ConnectionErrorFragment.ID);
            }
        }
    }

    public void init(Integer projectNumber) {
        projectNumber = null;
        // Init formatter
        initProjectNumberFormatter();
        initProjectNameFormatter();
        initCustomerFormatter();
        initDatePickerFormatter();
        // Init input
        initDatePickers();
        initMembersInput();
        initGroupComboboxInput();
        initStatusProject();
        // Init handling
        closeBtnErrorContainer.setOnMouseClicked(event -> errorContainer.setVisible(false));
        cancelButton.setOnAction(event -> context.send(MainContentComponent.ID, ProjectListFragment.ID));
        initRequiredTextFieldChangeListener(projectNameInput, projectNameInput);
        initRequiredTextFieldChangeListener(customerInput, customerInput);
        initRequiredTextFieldChangeListener(startDateInput.getEditor(), startDateInput);
        if (Objects.isNull(projectNumber)) {
            initRequiredTextFieldChangeListener(projectNumberInput, projectNumberInput);
        } else {
            Project project = initLayout(projectNumber);
            submitButton.setOnAction(event -> editProject(project));
        }
        statusInput.valueProperty().addListener(((observable, oldValue, newValue) ->
                currentSelectedStatusIndex = statusInput.getSelectionModel().getSelectedIndex()));
    }

    private Project initLayout(Integer projectNumber) {
        try {
            Grpc client = Grpc.getInstance();
            ProjectServiceGrpc.ProjectServiceBlockingStub stub = client.getProjectServiceStub();
            Project response = stub.getProjectByNumber(ProjectNumber.newBuilder()
                    .setNumber(projectNumber)
                    .build());
            if (response.getIsExisted()) {
                projectNumberInput.setText(String.valueOf(projectNumber));
                projectNameInput.setText(response.getName());
                customerInput.setText(response.getCustomer());
                groupInput.setValue(response.getGroupId());
                statusInput.setValue(ApplicationMapper.convertToProjectStatus(response.getStatus()));
                membersInput.setText(String.join(MEMBER_SEPARATOR_SYMBOL, response.getMembersList()));
                startDateInput.setValue(LocalDate.parse(response.getStartDate(), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)));
                endDateInput.setValue(StringUtils.isEmpty(response.getEndDate()) ? null : LocalDate.parse(response.getEndDate(), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)));
                projectNumberInput.setDisable(true);
                currentSelectedStatusIndex = response.getStatus().getNumber();
                return response;
            } else {
                context.send(MainContentComponent.ID, ProjectListFragment.ID);
            }
        } catch (StatusRuntimeException e) {
            context.send(MainContentComponent.ID, ConnectionErrorFragment.ID);
        }
        return null;
    }

    private void initGroupComboboxInput() {
        getAllGroupIDs();
    }

    private void initStatusProject() {
        statusInput.getItems().addAll("New", "Planned", "In progress", "Finished");
        statusInput.getSelectionModel().selectFirst();
    }

    private void initMembersInput() {
        membersInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Z,]*")) {
                // Loại bỏ các ký tự không phải là chữ hoa hoặc dấu ',' từ chuỗi
                newValue = newValue.replaceAll("[^A-Z,]", "");
                membersInput.setText(newValue);
            }
        });
    }

    private void initDatePickers() {
        restrictToNumericInput(startDateInput);
        restrictToNumericInput(endDateInput);

        startDateInput.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.getYear() != YearMonth.now().getYear());
            }
        });

        // Luôn giữ cho endDate lớn hơn startDate ít nhất 1 ngày
        startDateInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                endDateInput.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(empty || date.isBefore(newValue.plusDays(1)));
                    }
                });
            }
        });
    }

    private void initDatePickerFormatter() {
        startDateInput.setConverter(new LocalDateStringConverter(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER), null));
        endDateInput.setConverter(new LocalDateStringConverter(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER), null));
    }

    private void restrictToNumericInput(DatePicker datePicker) {
        datePicker.getEditor().addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char inputChar = event.getCharacter().charAt(0);
            if (!Character.isDigit(inputChar)) {
                event.consume();
            }
        });
    }

    private void initProjectNameFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            // Kiểm tra xem newText có độ dài không vượt quá 50 ký tự hay không
            if (newText.length() <= 50) {
                return change; // Cho phép nhập
            }

            return null; // Ngăn chặn ký tự không hợp lệ
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        projectNameInput.setTextFormatter(textFormatter);
    }

    private void initCustomerFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            // Kiểm tra xem newText có độ dài không vượt quá 50 ký tự hay không
            if (newText.length() <= 50) {
                return change; // Cho phép nhập
            }

            return null; // Ngăn chặn ký tự không hợp lệ
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        customerInput.setTextFormatter(textFormatter);
    }


    private void initProjectNumberFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Kiểm tra xem newText có chứa số hay không
            if (newText.matches("\\d*")) {
                // Kiểm tra giới hạn của Integer
                if (newText.isEmpty() || isValidInteger(newText)) {
                    return change; // Cho phép nhập
                }
            }
            return null; // Ngăn chặn ký tự không hợp lệ
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        projectNumberInput.setTextFormatter(textFormatter);
    }

    private void bindNodeVisible() {
        errorContainer.managedProperty().bind(errorContainer.visibleProperty());
        projectNumberExistedMessage.managedProperty().bind(projectNumberExistedMessage.visibleProperty());
        projectVisaNotValid.managedProperty().bind(projectVisaNotValid.visibleProperty());
        invalidVisasMessage.managedProperty().bind(invalidVisasMessage.visibleProperty());
    }

    private void hideErrorMessage() {
        errorContainer.setVisible(false);
        projectNumberExistedMessage.setVisible(false);
        projectVisaNotValid.setVisible(false);
        invalidVisasMessage.setVisible(false);
    }

    private boolean isValidInteger(String text) {
        try {
            int value = Integer.parseInt(text);
            return value <= Integer.MAX_VALUE;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void handleProjectResponse(CreateUpdateProjectResponse response) {
        response.getStatusList().forEach(status -> {
            switch (status) {
                case PROJECT_NUMBER_EXISTED:
                    projectNumberInput.getStyleClass().add(FIELD_ERROR_CSS_CLASS);
                    projectNumberExistedMessage.setVisible(true);
                    break;
                case EMPLOYEE_VISAS_NOT_EXISTED:
                    membersInput.getStyleClass().add(FIELD_ERROR_CSS_CLASS);
                    projectVisaNotValid.setVisible(true);
                    invalidVisasMessage.setVisible(true);
                    List<String> invalidVisasList = response.getInvalidVisaList();
                    String formattedResult = " {" + String.join(",", invalidVisasList) + "}";
                    invalidVisasMessage.setText(formattedResult);
                    break;
                case CAN_NOT_UPDATE_PROJECT:
                    break;
                default:
                    context.send(MainContentComponent.ID, ProjectListFragment.ID);
            }
        });
    }

    private List<Node> getEmptyRequiredInputFields() {
        List<Node> nodes = new ArrayList<>();

        if (StringUtils.isEmpty(projectNumberInput.getText())) {
            nodes.add(projectNumberInput);
        }
        if (StringUtils.isEmpty(projectNameInput.getText())) {
            nodes.add(projectNameInput);
        }
        if (StringUtils.isEmpty(customerInput.getText())) {
            nodes.add(customerInput);
        }
        if (Objects.isNull(startDateInput.getValue())) {
            nodes.add(startDateInput);
        }

        return nodes;
    }

    private void initRequiredTextFieldChangeListener(TextField textField, Node targetNode) {
        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                targetNode.getStyleClass().add(FIELD_ERROR_CSS_CLASS);
            } else {
                targetNode.getStyleClass().remove(FIELD_ERROR_CSS_CLASS);
            }
        }));
    }

    private void removeErrorClassFromTextFields() {
        projectNumberInput.getStyleClass().remove(FIELD_ERROR_CSS_CLASS);
        membersInput.getStyleClass().remove(FIELD_ERROR_CSS_CLASS);
        startDateInput.getStyleClass().remove(FIELD_ERROR_CSS_CLASS);
        endDateInput.getStyleClass().remove(FIELD_ERROR_CSS_CLASS);
        projectNameInput.getStyleClass().remove(FIELD_ERROR_CSS_CLASS);
        customerInput.getStyleClass().remove(FIELD_ERROR_CSS_CLASS);
    }

    private void getAllGroupIDs() {
        try {
            Grpc client = Grpc.getInstance();
            GroupServiceGrpc.GroupServiceBlockingStub stub = client.getGroupServiceStub();
            GroupResponse groupIDs = stub.getAllGroupIDs(GroupRequest.newBuilder().build());

            List<Long> groupIDList = groupIDs.getIdList();
            ObservableList<Long> observableGroupIDs = FXCollections.observableArrayList(groupIDList);
            groupInput.setItems(observableGroupIDs);
            groupInput.getSelectionModel().selectFirst();
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            context.send(MainContentComponent.ID, ConnectionErrorFragment.ID);
        }
    }
}
