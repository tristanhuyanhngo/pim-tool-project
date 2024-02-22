package org.elca.neosis.fragment;

import io.grpc.StatusRuntimeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.converter.LocalDateStringConverter;
import org.elca.neosis.common.ApplicationBundleKey;
import org.elca.neosis.common.ui.ConfirmationAlert;
import org.elca.neosis.common.ui.NotificationAlert;
import org.elca.neosis.component.MainContentComponent;
import org.elca.neosis.grpc.Grpc;
import org.elca.neosis.multilingual.I18N;
import org.elca.neosis.proto.*;
import org.elca.neosis.util.ApplicationMapper;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;
import org.jacpfx.rcp.context.Context;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Fragment(id = ProjectDetailFragment.ID, viewLocation = "/fxml/ProjectDetailFragment.fxml", resourceBundleLocation = I18N.BUNDLE_NAME, scope = Scope.PROTOTYPE)
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
    private HBox errorContainer;

    @FXML
    private TextField projectNumberInput;

    @FXML
    private Label fragmentProjectNumberInputLabel;

    @FXML
    private TextField projectNameInput;
    @FXML
    private Label fragmentProjectNameInputLabel;

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
    private Label projectVisaNotValidMessage;
    @FXML
    private Label projectNumberExistedMessage;
    @FXML
    private Label missingRequiredFieldsMessage;
    @FXML
    private Label invalidVisas;
    @FXML
    private Label closeButtonErrorContainer;

    @FXML
    private Label fragmentCustomerInputLabel;

    @FXML
    private Label fragmentGroupInputLabel;

    @FXML
    private Label fragmentMembersInputLabel;

    @FXML
    private Label fragmentStatusInputLabel;

    @FXML
    private Label fragmentStartDateInputLabel;

    @FXML
    private Label fragmentEndDateInputLabel;

    @FXML
    private Label fragmentTitle;

    @FXML
    private Label startDateAfterEndDateErrors;


    private void initMultilingual() {
        // Init multilingual for labels
        fragmentProjectNumberInputLabel.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_PROJECT_NUMBER_INPUT_TITLE));
        fragmentProjectNameInputLabel.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_PROJECT_NAME_INPUT));
        fragmentCustomerInputLabel.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_CUSTOMER_INPUT));
        fragmentGroupInputLabel.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_GROUP_INPUT));
        fragmentMembersInputLabel.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_MEMBERS_INPUT));
        fragmentStatusInputLabel.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_STATUS_INPUT));
        fragmentStartDateInputLabel.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_START_DATE_INPUT));
        fragmentEndDateInputLabel.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_END_DATE_INPUT));
        projectVisaNotValidMessage.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_EMPLOYEE_VISAS_NOT_EXISTED));
        missingRequiredFieldsMessage.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_MISSING_REQUIRED_FIELDS));
        projectNumberExistedMessage.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_PROJECT_NUMBER_EXISTED));

        // Init multilingual for buttons
        cancelButton.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_CANCEL_BUTTON));

        // Init multilingual for datepicker
        I18N.localeProperty().addListener((observable, oldValue, newValue) -> {
            startDateInput.setChronology(MinguoChronology.INSTANCE);
            startDateInput.setChronology(Chronology.ofLocale(newValue));
            endDateInput.setChronology(MinguoChronology.INSTANCE);
            endDateInput.setChronology(Chronology.ofLocale(newValue));
        });
    }

    private void initMultilingualCreateProject() {
        initMultilingual();
        submitButton.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_CREATE_BUTTON));
        fragmentTitle.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_CREATE_TITLE));
    }

    private void initMultilingualEditProject() {
        initMultilingual();
        submitButton.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_EDIT_BUTTON));
        fragmentTitle.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_PROJECT_DETAIL_FRAGMENT_EDIT_TITLE));
    }

    private void handleCreateProject() {
        hideErrorMessage();
        removeErrorClassFromTextFields();
        List<Node> requiredEmptyTextFields = getEmptyRequiredInputFields();
        if (!requiredEmptyTextFields.isEmpty()) {
            errorContainer.setVisible(true);
            requiredEmptyTextFields.forEach(textField -> textField.getStyleClass().add(FIELD_ERROR_CSS_CLASS));
        } else {
            ConfirmationAlert confirmDialog = new ConfirmationAlert(I18N.get(ApplicationBundleKey.LABEL_TITLE_CREATE_ALERT), I18N.get(ApplicationBundleKey.LABEL_HEADER_CREATE_ALERT), I18N.get(ApplicationBundleKey.LABEL_CONTENT_CREATE_ALERT), I18N.get(ApplicationBundleKey.BUTTON_CREATE_CREATE_ALERT), I18N.get(ApplicationBundleKey.BUTTON_CANCEL_CREATE_ALERT), Alert.AlertType.CONFIRMATION);
            boolean confirmed = confirmDialog.showConfirmationDialog();

            if (confirmed) {
                LocalDate endDate = endDateInput.getValue();
                Set<String> members = Arrays.stream(membersInput.getText().split(MEMBER_SEPARATOR_SYMBOL)).map(String::trim).filter(visa -> !visa.isEmpty()).collect(Collectors.toSet());
                NewProject request = NewProject.newBuilder().setNumber(Integer.parseInt(projectNumberInput.getText())).setName(projectNameInput.getText()).setCustomer(customerInput.getText()).setGroupId(groupInput.getValue()).setStatus(ApplicationMapper.convertToProjectStatus(statusInput.getValue())).addAllMembers(members).setStartDate(startDateInput.getValue().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER))).setEndDate(!Objects.isNull(endDate) ? endDate.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)) : "").build();
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
    }

    private void handleEditProject(Project project) {
        hideErrorMessage();
        removeErrorClassFromTextFields();
        List<Node> requiredEmptyTextFields = getEmptyRequiredInputFields();
        if (!requiredEmptyTextFields.isEmpty()) {
            errorContainer.setVisible(true);
            requiredEmptyTextFields.forEach(textField -> textField.getStyleClass().add(FIELD_ERROR_CSS_CLASS));
        } else {
            ConfirmationAlert confirmDialog = new ConfirmationAlert(I18N.get(ApplicationBundleKey.LABEL_TITLE_UPDATE_ALERT), I18N.get(ApplicationBundleKey.LABEL_HEADER_UPDATE_ALERT), I18N.get(ApplicationBundleKey.LABEL_CONTENT_UPDATE_ALERT), I18N.get(ApplicationBundleKey.BUTTON_UPDATE_UPDATE_ALERT), I18N.get(ApplicationBundleKey.BUTTON_CANCEL_UPDATE_ALERT), Alert.AlertType.CONFIRMATION);
            boolean confirmed = confirmDialog.showConfirmationDialog();

            if (confirmed) {
                LocalDate endDate = endDateInput.getValue();
                Set<String> members = Arrays.stream(membersInput.getText().split(MEMBER_SEPARATOR_SYMBOL)).map(String::trim).filter(visa -> !visa.isEmpty()).collect(Collectors.toSet());
                Project request = Project.newBuilder().setId(project.getId()).setVersion(project.getVersion()).setNumber(Integer.parseInt(projectNumberInput.getText())).setName(projectNameInput.getText()).setCustomer(customerInput.getText()).setGroupId(groupInput.getValue()).setStatus(ApplicationMapper.convertToProjectStatus(statusInput.getValue())).addAllMembers(members).setStartDate(startDateInput.getValue().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER))).setEndDate(!Objects.isNull(endDate) ? endDate.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER)) : "").build();
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
    }

    public void init(Integer projectNumber) {
        // Init formatter
        initProjectNumberFormatter();
        initStringTextFieldFormatter();
        initDatePickerFormatter();
        // Init input
        initDatePickers();
        initMembersInput();
        initGroupComboboxInput();
        initProjectStatus();
        // Init handling
        bindNodeVisible();
        closeButtonErrorContainer.setOnMouseClicked(event -> errorContainer.setVisible(false));
        cancelButton.setOnAction(event -> context.send(MainContentComponent.ID, ProjectListFragment.ID));
        initRequiredTextFieldChangeListener(projectNameInput, projectNameInput);
        initRequiredTextFieldChangeListener(customerInput, customerInput);
        initRequiredTextFieldChangeListener(startDateInput.getEditor(), startDateInput);
        if (Objects.isNull(projectNumber)) {
            initRequiredTextFieldChangeListener(projectNumberInput, projectNumberInput);
            initMultilingualCreateProject();
            submitButton.setOnAction(event -> handleCreateProject());
        } else {
            Project project = initLayout(projectNumber);
            initMultilingualEditProject();
            submitButton.setOnAction(event -> handleEditProject(project));
        }
        statusInput.valueProperty().addListener(((observable, oldValue, newValue) -> currentSelectedStatusIndex = statusInput.getSelectionModel().getSelectedIndex()));
    }

    private Project initLayout(Integer projectNumber) {
        try {
            Grpc client = Grpc.getInstance();
            ProjectServiceGrpc.ProjectServiceBlockingStub stub = client.getProjectServiceStub();
            Project response = stub.getProjectByNumber(ProjectNumber.newBuilder().setNumber(projectNumber).build());
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

    private void initProjectStatus() {
        I18N.localeProperty().addListener(((observable, oldValue, newValue) -> {
            statusInput.getItems().setAll(
                    I18N.get(ApplicationBundleKey.PROJECT_NEW_STATUS_KEY),
                    I18N.get(ApplicationBundleKey.PROJECT_PLANNED_STATUS_KEY),
                    I18N.get(ApplicationBundleKey.PROJECT_IN_PROGRESS_STATUS_KEY),
                    I18N.get(ApplicationBundleKey.PROJECT_FINISHED_STATUS_KEY)
            );
        }));
        statusInput.getItems().setAll(
                I18N.get(ApplicationBundleKey.PROJECT_NEW_STATUS_KEY),
                I18N.get(ApplicationBundleKey.PROJECT_PLANNED_STATUS_KEY),
                I18N.get(ApplicationBundleKey.PROJECT_IN_PROGRESS_STATUS_KEY),
                I18N.get(ApplicationBundleKey.PROJECT_FINISHED_STATUS_KEY)
        );
        statusInput.getSelectionModel().selectFirst();
    }

    private void initMembersInput() {
        membersInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Z,]*")) {
                // Remove non-uppercase characters or commas from the string
                newValue = newValue.replaceAll("[^A-Z,]", "");
                membersInput.setText(newValue);
            }
        });
    }

    private void initDatePickers() {
        startDateInput.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.getYear() < YearMonth.now().getYear() - 1);
            }
        });

        // Ensure that endDate is always at least 1 day greater than startDate
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
        limitDatePickerInput(startDateInput);
        limitDatePickerInput(endDateInput);
        startDateInput.setConverter(new LocalDateStringConverter(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER), null));
        endDateInput.setConverter(new LocalDateStringConverter(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER), null));
    }

    private void limitDatePickerInput(DatePicker datePicker) {
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[\\d.]*")) {
                return change;
            } else {
                return null;
            }
        });
        datePicker.getEditor().setTextFormatter(textFormatter);
    }

    private void initStringTextFieldFormatter() {
        projectNameInput.setTextFormatter(initStringTextFieldInput());
        customerInput.setTextFormatter(initStringTextFieldInput());
    }

    private TextFormatter<String> initStringTextFieldInput() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.length() <= 50) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(filter);
    }


    private void initProjectNumberFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Check if newText contains numbers
            if (newText.matches("\\d*") && (newText.isEmpty() || isValidInteger(newText))) {
                return change; // Allowing input
            }
            return null; // Restricting invalid characters
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        projectNumberInput.setTextFormatter(textFormatter);
    }

    private void bindNodeVisible() {
        errorContainer.managedProperty().bind(errorContainer.visibleProperty());
        projectNumberExistedMessage.managedProperty().bind(projectNumberExistedMessage.visibleProperty());
        projectVisaNotValidMessage.managedProperty().bind(projectVisaNotValidMessage.visibleProperty());
        invalidVisas.managedProperty().bind(invalidVisas.visibleProperty());
    }

    private void hideErrorMessage() {
        errorContainer.setVisible(false);
        projectNumberExistedMessage.setVisible(false);
        projectVisaNotValidMessage.setVisible(false);
        invalidVisas.setVisible(false);
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
                    projectVisaNotValidMessage.setVisible(true);
                    invalidVisas.setVisible(true);
                    List<String> invalidVisasList = response.getInvalidVisaList();
                    String formattedResult = " {" + String.join(",", invalidVisasList) + "}";
                    invalidVisas.setText(formattedResult);
                    break;
                case START_DATE_AFTER_END_DATE:
                    startDateInput.getStyleClass().add(FIELD_ERROR_CSS_CLASS);
                    endDateInput.getStyleClass().add(FIELD_ERROR_CSS_CLASS);
                    startDateAfterEndDateErrors.setVisible(true);
                    break;
                case CAN_NOT_UPDATE_PROJECT:
                    NotificationAlert notificationAlert = new NotificationAlert(I18N.get(ApplicationBundleKey.LABEL_ERROR_UPDATING_DIALOG_TITLE), I18N.get(ApplicationBundleKey.LABEL_ERROR_UPDATING_DIALOG_HEADER), I18N.get(ApplicationBundleKey.LABEL_ERROR_UPDATING_DIALOG_CONTENT));
                    notificationAlert.showConfirmationDialog();
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
