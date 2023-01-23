package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;

	public void setService(DepartmentService service) {
		this.service = service;
	}

	private Department getSelectedDepartment() {
		int i = this.tableViewDepartments.getSelectionModel().getFocusedIndex();
		Department obj = this.tableViewDepartments.getItems().get(i);
		return obj;
	}

	// Components
	@FXML
	private TableView<Department> tableViewDepartments;
	private ObservableList<Department> obsList;
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private TableColumn<Department, Department> tableColumnEdit;
	@FXML
	private Button btNew;
	@FXML
	private Button btEdit;
	@FXML
	private Button btDelete;

	// Actions
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm("/gui/DepartmentForm.fxml", obj, parentStage);
	}

	@FXML
	public void onBtEditAction(ActionEvent event) {
		Department obj = getSelectedDepartment();
		createDialogForm("/gui/DepartmentForm.fxml", obj, Utils.currentStage(event));
	}

	@FXML
	public void onBtDeleteAction(ActionEvent event) {
		if (service == null) {
			throw new IllegalStateException("Service is null");
		}
		Department obj = getSelectedDepartment();

		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation",
				"Are you sure to delete %s?".formatted(obj.getName()));
		if (result.get() == ButtonType.OK) {
			try {
				service.deleteById(obj.getId());
				updateTableView();
			} catch (DBException e) {
				Alerts.showAlert("Error removing object", e.getMessage(), AlertType.ERROR);
			}
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());
	}

	private void createDialogForm(String absoluteName, Department obj, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.updateFormData();
			controller.setService(new DepartmentService());
			controller.subscribeDataChangeListener(this);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("service is null");
		}
		List<Department> list = service.findAll();
		list.sort((d1, d2) -> d1.getId() - d2.getId());
		this.obsList = FXCollections.observableArrayList(list);
		this.tableViewDepartments.setItems(obsList);
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

}
