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
import org.elca.neosis.component.MainContentComponent;
import org.elca.neosis.factory.ObservableResourceFactory;
import org.elca.neosis.grpc.Grpc;
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
        initHyperlinkProjectNumberColumn(); // Not yet implemented
        initTableViewOrderByProjectNumber();
        initDeleteButton();
        initResetButton();
        setInitialLayout();
        initPagination();
        getProjectsWithPagination(FIRST_PAGE_INDEX);
        initSearchBarListener();
        initProjectStatusListener();
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
            initPagination();
            getProjectsWithPagination(FIRST_PAGE_INDEX);
            selectedItemsContainer.setVisible(false);
            selectedProjectNumbers.clear();
        });
    }

    private void setInitialLayout() {
        searchBar.setText(searchConditionState.getKeywords());
        projectStatusCombobox.setValue(Objects.isNull(searchConditionState.getStatus()) ? null
                : ApplicationMapper.convertToProjectStatus(searchConditionState.getStatus()));
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
            return new SimpleObjectProperty<>(checkBox);
        });
        columnSelected.setCellFactory(col -> new TableCell<ProjectSearchResult, CheckBox>() {
            @Override
            protected void updateItem(CheckBox checkBox, boolean empty) {
                super.updateItem(checkBox, empty);
                if (empty || checkBox == null) {
                    setGraphic(null);
                } else {
                    ProjectSearchResult result = (ProjectSearchResult) getTableRow().getItem();
                    if (result != null && "New".equals(result.getStatus())) {
                        setGraphic(checkBox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
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

    private static class DeleteButtonCell extends TableCell<ProjectSearchResult, Void> {
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
                    null,
                    "Are you sure you want to delete the project: " + project.getName() + " ?",
                    "Delete",
                    "Cancel",
                    "#ff0000",
                    "#ffffff");
            boolean confirmed = confirmDialog.showConfirmationDialog();

            if (confirmed) {
                System.out.println("User choose delete");
            } else {
               System.out.println("User choose cancel");
            }
        }
    }

    private void initHyperlinkProjectNumberColumn() {
        projectNumberColumn.getStyleClass().add("CENTER_RIGHT_CELL_CSS_CLASS");
    }

    private void initPagination() {
        countAllProjectsWithConditions();
        if (totalProjects > 0) {
            int bonusPage = (totalProjects % PAGE_SIZE == 0) ? 0 : 1;
            int totalPages = (int) (totalProjects / PAGE_SIZE) + bonusPage;

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
        projectStatusCombobox.getItems().setAll(Stream.of(ProjectStatus.values())
                .map(ApplicationMapper::convertToProjectStatus)
                .toArray(String[]::new));
        // Set the default project status
        projectStatusCombobox.setValue(currentSelectedStatusIndex == -1 ? null : projectStatusCombobox.getItems().get(currentSelectedStatusIndex));
    }

    @FXML
    private void handleSearchButtonClick(ActionEvent event) {
        initPagination();
        getProjectsWithPagination(FIRST_PAGE_INDEX);
        selectedItemsContainer.setVisible(false);
        selectedProjectNumbers.clear();
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
}
