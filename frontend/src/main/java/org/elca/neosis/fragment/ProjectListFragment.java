package org.elca.neosis.fragment;


import io.grpc.StatusRuntimeException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.elca.neosis.common.ui.NotificationAlert;
import org.elca.neosis.component.MainContentComponent;
import org.elca.neosis.factory.ObservableResourceFactory;
import org.elca.neosis.grpc.Grpc;
import org.elca.neosis.model.ListToDetailMessage;
import org.elca.neosis.model.ProjectSearchResult;
import org.elca.neosis.model.SearchConditionState;
import org.elca.neosis.proto.*;
import org.elca.neosis.util.ApplicationMapper;
import org.elca.neosis.common.ui.ConfirmationAlert;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;
import org.jacpfx.rcp.context.Context;

import java.util.*;
import java.util.stream.Stream;

import static org.elca.neosis.util.ApplicationMapper.convertToProjectStatus;

@Fragment(
        id = ProjectListFragment.ID,
        viewLocation = "/fxml/ProjectListFragment.fxml",
        scope = Scope.PROTOTYPE,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME
)
public class ProjectListFragment {
    public static final String ID = "ProjectListFragment";
    public static final int CHECKBOX_SIZE = 15;
    public static final int COLUMN_WIDTH = 50;
    public static final int PAGINATION_PREF_HEIGHT = 32;
    public static final String DELETE_ICON_PATH = "/images/delete-button.png";
    public static final String CENTER_CELL_CSS_CLASS = "table-cell-center";
    public static final String LEFT_CELL_CSS_CLASS = "table-cell-left";
    public static final String RIGHT_CELL_CSS_CLASS = "table-cell-right";
    public static final String CENTER_CELL_DELETE_CSS_CLASS = "table-cell-delete-center";
    public static final String DELETE_BUTTON_CSS_CLASS = "project-list-fragment-delete-button";
    public static final int FIRST_COLUMN_INDEX = 0;
    public static final int PAGE_SIZE = 10;
    public static final int FIRST_PAGE_INDEX = 0;
    private final List<Integer> selectedProjectNumbers = new ArrayList<>();
    private final SearchConditionState searchConditionState = SearchConditionState.getInstance();
    private long totalProjects = 0;
    private int pageNumber = 0;
    private int currentSelectedStatusIndex = -1;

    @Resource
    private Context context;

    @FXML
    private TextField searchBar;

    @FXML
    private ComboBox<String> projectStatusCombobox;

    @FXML
    private Button searchButton;
    @FXML
    private Label resetButton;

    @FXML
    private Label numberOfSelectedItems;

    @FXML
    private HBox selectedItemsContainer;

    @FXML
    private ImageView deleteAllButton;

    @FXML
    private TableView<ProjectSearchResult> projectTableView;

    @FXML
    private TableColumn<ProjectSearchResult, Integer> projectNumberColumn;

    @FXML
    private TableColumn<ProjectSearchResult, String> projectNameColumn;

    @FXML
    private TableColumn<ProjectSearchResult, String> projectStatusColumn;

    @FXML
    private TableColumn<ProjectSearchResult, String> projectCustomerColumn;

    @FXML
    private TableColumn<ProjectSearchResult, String> projectStartDateColumn;

    @FXML
    private TableColumn<ProjectSearchResult, Void> projectDeleteColumn;

    @FXML
    private Label itemsSelectedText;

    @FXML
    private Label deleteSelectedItemsText;

    @FXML
    private Label fragmentTitle;

    @FXML
    private HBox paginationContainer;

    public void init() {
        initTableCellHeight();
        initProjectStatus();
        initCheckboxColumn();
        initHyperlinkProjectNumberColumn();
        initTableViewOrderByProjectNumber();
        initDeleteButton();
        initResetButton();
        setInitialLayout();
        initPagination();
        getProjectsWithPagination(FIRST_PAGE_INDEX);
        initSearchBarListener();
        initProjectStatusListener();
        setupDeleteAllButtonAction();
    }

    private void initSearchBarListener() {
        searchBar.textProperty().addListener(((observable, oldValue, newValue) -> searchConditionState.setKeywords(newValue)));
    }

    private void initProjectStatusListener() {
        projectStatusCombobox.valueProperty().addListener(((observable, oldValue, newValue) ->
                searchConditionState.setStatus(ApplicationMapper.convertToProjectStatus(newValue))));
    }

    private void initResetButton() {
        resetButton.setOnMouseClicked(event -> {
            searchBar.setText("");
            projectStatusCombobox.setValue(null);
            searchConditionState.setKeywords("");
            searchConditionState.setStatus(null);
            currentSelectedStatusIndex = -1;
            pageNumber = FIRST_PAGE_INDEX;
            updateTableView(FIRST_PAGE_INDEX);
        });
    }

    private void setInitialLayout() {
        searchBar.setText(searchConditionState.getKeywords());
        ProjectStatus status = searchConditionState.getStatus();
        projectStatusCombobox.setValue(status != null ? ApplicationMapper.convertToProjectStatus(status) : null);
    }

    private void initTableCellHeight() {
        projectTableView.setFixedCellSize(50);
    }

    private void initTableViewOrderByProjectNumber() {
        projectNumberColumn.setComparator(Comparator.comparingInt(Integer::intValue));
    }

    private void initCheckboxColumn() {
        TableColumn<ProjectSearchResult, CheckBox> columnSelected = new TableColumn<>();
        columnSelected.getStyleClass().add(CENTER_CELL_CSS_CLASS);
        columnSelected.setPrefWidth(COLUMN_WIDTH);
        columnSelected.setSortable(false);
        columnSelected.setResizable(false);

        columnSelected.setCellValueFactory(cellData -> {
            CheckBox checkBox = new CheckBox();
            ProjectSearchResult result = cellData.getValue();
            if (Objects.equals(ApplicationMapper.convertToProjectStatus(result.getStatus()), ProjectStatus.NEW)) {
                checkBox.setSelected(selectedProjectNumbers.contains(result.getNumber()));
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (Boolean.TRUE.equals(newValue)) {
                        selectedProjectNumbers.add(result.getNumber());
                    } else {
                        selectedProjectNumbers.remove(result.getNumber());
                    }
                    observeSelectedItemsContainer();
                });
            }
            else {
                checkBox.setDisable(true);
            }
            return new SimpleObjectProperty<>(checkBox);
        });
        projectTableView.getColumns().add(FIRST_COLUMN_INDEX, columnSelected);
    }

    private void initDeleteButton() {
        configureDeleteButtonColumn();
    }

    private void configureDeleteButtonColumn() {
        projectDeleteColumn.setSortable(false);
        projectDeleteColumn.getStyleClass().add(CENTER_CELL_DELETE_CSS_CLASS);
        projectDeleteColumn.setCellFactory(col -> createDeleteButtonCell());
    }

    private TableCell<ProjectSearchResult, Void> createDeleteButtonCell() {
        return new DeleteButtonCell();
    }

    private class DeleteButtonCell extends TableCell<ProjectSearchResult, Void> {
        private final Button deleteButton;

        public DeleteButtonCell() {
            this.deleteButton = createDeleteButton();
            setupDeleteButtonAction();
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else {
                ProjectSearchResult result = (ProjectSearchResult) getTableRow().getItem();
                setGraphic(result != null && "New".equals(result.getStatus()) ? deleteButton : null);
            }
        }

        private Button createDeleteButton() {
            Button button = new Button();
            button.setGraphic(createDeleteIcon());
            button.setStyle("-fx-background-color: transparent;");
            return button;
        }

        private ImageView createDeleteIcon() {
            return new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(DELETE_ICON_PATH))));
        }

        private void setupDeleteButtonAction() {
            deleteButton.setOnAction(event -> handleDeleteButtonClick());
        }

        private void handleDeleteButtonClick() {
            ProjectSearchResult project = getTableView().getItems().get(getIndex());
            ConfirmationAlert confirmDialog = new ConfirmationAlert("Confirm Delete",
                    "Are you sure ?",
                    "Do you really want to delete the project \"" + project.getName() + "\" ? This process cannot be undone.",
                    "Delete",
                    "Cancel",
                    "#ff0000",
                    "#cccccc",
                    Alert.AlertType.WARNING);
            boolean confirmed = confirmDialog.showConfirmationDialog();

            if (confirmed) {
                // Check if the project is selected, if yes remove it out of the selected List
                if (selectedProjectNumbers.contains(project.getNumber())) {
                    selectedProjectNumbers.remove(project.getNumber());
                }
                List<Integer> projectNumbers = Arrays.asList(project.getNumber());
                deleteProjects(projectNumbers);

                // Update table view
                initPagination();
                getProjectsWithPagination(pageNumber);
                observeSelectedItemsContainer();
            }
        }
    }

    private void setupDeleteAllButtonAction() {
        deleteAllButton.setOnMouseClicked(event -> handleDeleteAllButtonClick());
    }

    private void handleDeleteAllButtonClick() {
        ConfirmationAlert confirmDialog = new ConfirmationAlert("Confirm Delete",
                "Are you sure?",
                "Do you really want to delete all projects?",
                "Delete",
                "Cancel",
                "#ff0000",
                "#cccccc",
                Alert.AlertType.WARNING);
        boolean confirmed = confirmDialog.showConfirmationDialog();

        if (confirmed) {
            deleteProjects(selectedProjectNumbers);
            updateTableView(pageNumber);
        }
    }

    private void initHyperlinkProjectNumberColumn() {
        projectNumberColumn.setCellFactory(param -> new TableCell<ProjectSearchResult, Integer>() {
            private final Hyperlink hyperlink = new Hyperlink();

            {
                hyperlink.setOnAction(event -> {
                    event.consume();
                    ProjectSearchResult project = getTableView().getItems().get(getIndex());
                    context.send(MainContentComponent.ID, new ListToDetailMessage(ProjectDetailFragment.ID, project.getNumber()));
                });
            }

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    hyperlink.setText(String.valueOf(item));
                    hyperlink.setVisited(true);
                    setGraphic(hyperlink);
                    getStyleClass().add(RIGHT_CELL_CSS_CLASS);
                }
            }
        });
    }

    private void initPagination() {
        countAllProjectsWithConditions();
        if (totalProjects > 0) {
            int bonusPage = (totalProjects % PAGE_SIZE == 0) ? 0 : 1;
            int totalPages = (int) (totalProjects / PAGE_SIZE) + bonusPage;

            if (pageNumber + 1 >= totalPages) {
                pageNumber = totalPages - 1;
            }

            Pagination pagination = new Pagination(totalPages, pageNumber);
            pagination.setPrefHeight(PAGINATION_PREF_HEIGHT);
            pagination.currentPageIndexProperty().addListener(((observable, oldValue, newValue) -> {
                pageNumber = newValue.intValue();
                getProjectsWithPagination(pageNumber);
            }));

            paginationContainer.getChildren().clear();
            paginationContainer.getChildren().add(pagination);
        } else {
            Pagination pagination = new Pagination(1, 0);
            pagination.setPrefHeight(PAGINATION_PREF_HEIGHT);
            pagination.currentPageIndexProperty().addListener(((observable, oldValue, newValue) -> {
            }));

            paginationContainer.getChildren().clear();
            paginationContainer.getChildren().add(pagination);
        }
    }

    private void initProjectStatus() {
        // Get the project status from file Proto
        projectStatusCombobox.getItems().addAll("New", "Planned", "In progress", "Finished");
        // Set the default project status
        projectStatusCombobox.setValue(currentSelectedStatusIndex == -1 ? null : projectStatusCombobox.getItems().get(currentSelectedStatusIndex));
    }

    @FXML
    private void handleSearchButtonClick(ActionEvent event) {
        updateTableView(FIRST_PAGE_INDEX);
    }

    private void getAllProjects() {
        try {
            Grpc client = Grpc.getInstance();
            ProjectServiceGrpc.ProjectServiceBlockingStub stub = client.getProjectServiceStub();
            Iterator<SearchResult> iterator = stub.getAllProjects(Empty.newBuilder().build());
            ObservableList<ProjectSearchResult> items = FXCollections.observableArrayList();
            while (iterator.hasNext()) {
                SearchResult searchResult = iterator.next();
                items.add(new ProjectSearchResult(searchResult.getNumber(),
                        searchResult.getName(), convertToProjectStatus(searchResult.getStatus()),
                        searchResult.getCustomer(),
                        searchResult.getStartDate()));
            }
            projectTableView.setItems(items);
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private void countAllProjectsWithConditions() {
        try {
            String keywords = searchBar.getText();
            String status = projectStatusCombobox.getValue();
            Grpc client = Grpc.getInstance();
            ProjectServiceGrpc.ProjectServiceBlockingStub stub = client.getProjectServiceStub();
            CountProjectResponse countProject;

            if (status != null) {
                countProject = stub.countAllProjectWithConditions(CountCondition
                        .newBuilder()
                        .setKeywords(keywords.trim())
                        .setHaveStatus(true)
                        .setStatus(convertToProjectStatus(status))
                        .build());
            } else {
                countProject = stub.countAllProjectWithConditions(CountCondition
                        .newBuilder()
                        .setKeywords(keywords.trim())
                        .setHaveStatus(false)
                        .build());
            }
            totalProjects = countProject.getQuantity();
        } catch (StatusRuntimeException e) {
            context.send(MainContentComponent.ID, ConnectionErrorFragment.ID);
        }
    }

    private void getProjectsWithConditions(SearchCondition condition) {
        try {
            Grpc client = Grpc.getInstance();
            ProjectServiceGrpc.ProjectServiceBlockingStub stub = client.getProjectServiceStub();
            Iterator<SearchResult> iterator = stub.searchProject(condition);
            ObservableList<ProjectSearchResult> items = FXCollections.observableArrayList();
            while (iterator.hasNext()) {
                SearchResult searchResult = iterator.next();
                items.add(new ProjectSearchResult(searchResult.getNumber(),
                        searchResult.getName(), convertToProjectStatus(searchResult.getStatus()),
                        searchResult.getCustomer(),
                        searchResult.getStartDate()));
            }
            projectTableView.setItems(items);
        } catch (StatusRuntimeException e) {
            context.send(MainContentComponent.ID, ConnectionErrorFragment.ID);
        }
    }

    private void getProjectsWithPagination(int pageNumber) {
        String keywords = searchBar.getText();
        String status = projectStatusCombobox.getValue();

        if (status != null) {
            getProjectsWithConditions(SearchCondition.newBuilder()
                    .setKeywords(keywords.trim())
                    .setHaveStatus(true)
                    .setStatus(convertToProjectStatus(status))
                    .setPageSize(PAGE_SIZE)
                    .setPageNumber(pageNumber)
                    .build());
        } else {
            getProjectsWithConditions(SearchCondition.newBuilder()
                    .setKeywords(keywords.trim())
                    .setHaveStatus(false)
                    .setPageSize(PAGE_SIZE)
                    .setPageNumber(pageNumber)
                    .build());
        }
    }

    private void deleteProjects(List<Integer> projectNumbers) {
        try {
            Grpc client = Grpc.getInstance();
            ProjectServiceGrpc.ProjectServiceBlockingStub stub = client.getProjectServiceStub();
            DeleteProjectResponse response = stub.deleteProject(ListProjectNumber.newBuilder()
                    .addAllNumber(projectNumbers)
                    .build());
            if (!response.getIsSuccess()) {
                NotificationAlert notificationAlert= new NotificationAlert(
                        "Error dialog",
                        "This is an error dialog",
                        "There was a new update about this project. Please try again later");
                notificationAlert.showConfirmationDialog();
            }
        } catch (StatusRuntimeException e) {
            context.send(MainContentComponent.ID, ConnectionErrorFragment.ID);
        }
    }

    private void updateTableView(int pageNumber) {
        initPagination();
        getProjectsWithPagination(pageNumber);
        selectedProjectNumbers.clear();
        selectedItemsContainer.setVisible(false);
    }

    private void observeSelectedItemsContainer() {
        if (selectedProjectNumbers.isEmpty()) {
            selectedItemsContainer.setVisible(false);
        } else {
            selectedItemsContainer.setVisible(true);
            numberOfSelectedItems.setText(selectedProjectNumbers.size() + " ");
        }
    }
}
