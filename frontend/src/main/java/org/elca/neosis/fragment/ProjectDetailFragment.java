package org.elca.neosis.fragment;

import com.sun.javafx.css.converters.StringConverter;
import io.grpc.StatusRuntimeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;
import org.jacpfx.rcp.context.Context;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.elca.neosis.util.ApplicationMapper.convertToProjectStatus;

@Fragment(
        id = ProjectDetailFragment.ID,
        viewLocation = "/fxml/ProjectDetailFragment.fxml",
        scope = Scope.PROTOTYPE,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME
)
public class ProjectDetailFragment {
    public static final String ID = "ProjectDetailFragment";

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


    public void init() {
        initProjectNumberFormatter();
        initProjectNameFormatter();
        initCustomerFormatter();
        initMembersInput();
        initGroupInput();
        initDatePickerFormat();
        initDatePickers();
        initStatusProject();
    }

    private void initGroupInput() {
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

    private void initDatePickerFormat() {
        startDateInput.setConverter(new LocalDateStringConverter(DateTimeFormatter.ofPattern("dd.MM.yyyy"), null));
        endDateInput.setConverter(new LocalDateStringConverter(DateTimeFormatter.ofPattern("dd.MM.yyyy"), null));
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

    private boolean isValidInteger(String text) {
        try {
            int value = Integer.parseInt(text);
            return value <= Integer.MAX_VALUE;
        } catch (NumberFormatException e) {
            return false;
        }
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
