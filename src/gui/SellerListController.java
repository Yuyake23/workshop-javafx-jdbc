package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;

	public void setService(SellerService service) {
		this.service = service;
	}

	private Seller getSelectedSeller() {
		int i = this.tableViewSellers.getSelectionModel().getFocusedIndex();
		Seller obj = this.tableViewSellers.getItems().get(i);
		return obj;
	}

	// Components
	@FXML
	private TableView<Seller> tableViewSellers;
	private ObservableList<Seller> obsList;
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	@FXML
	private Button btNew;
	@FXML
	private Button btEdit;
	@FXML
	private Button btDelete;

	// Actions
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Seller obj = new Seller();
		createDialogForm("/gui/SellerForm.fxml", obj, Utils.currentStage(event));
	}

	@FXML
	public void onBtEditAction(ActionEvent event) {
		Seller obj = getSelectedSeller();
		createDialogForm("/gui/SellerForm.fxml", obj, Utils.currentStage(event));
	}

	@FXML
	public void onBtDeleteAction(ActionEvent event) {
		if (service == null) {
			throw new IllegalStateException("Service is null");
		}
		Seller obj = getSelectedSeller();

		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation",
				"Are you sure to delete %s?".formatted(obj.getName()));
		if (result.get() == ButtonType.OK) {
			try {
				service.deleteById(obj.getId());
				updateTableView();
			} catch (DBException e) {
				e.printStackTrace();
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
		this.tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		this.tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(this.tableColumnBirthDate, "MM-dd-yyyy");
		this.tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(this.tableColumnBaseSalary, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSellers.prefHeightProperty().bind(stage.heightProperty());
	}

	private void createDialogForm(String absoluteName, Seller obj, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			SellerFormController controller = loader.getController();
			controller.setSeller(obj);
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service is null");
		}
		List<Seller> list = service.findAll();
		list.sort((d1, d2) -> d1.getId() - d2.getId());
		this.obsList = FXCollections.observableArrayList(list);
		this.tableViewSellers.setItems(this.obsList);
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

}
