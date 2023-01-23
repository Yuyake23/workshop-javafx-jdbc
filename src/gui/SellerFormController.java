package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constrainsts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;
	private SellerService sellerService;
	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	private Seller getFormData() {
		Integer id = Utils.tryParseToInt(txtId.getText());
		String name = txtName.getText();
		String email = txtEmail.getText();
		Seller obj = new Seller();
		validateFormData(name);
		obj.setId(id);
		obj.setName(name);
		obj.setEmail(email);
		return obj;
	}

	private static void validateFormData(String name) {
		ValidationException e = new ValidationException("Validation error");

		if (name == null || name.isBlank())
			e.addError("name", "Field cannot be empty");
		if (!e.getErrors().isEmpty())
			throw e;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		this.dataChangeListeners.add(listener);
	}

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	private ObservableList<Department> departments;
	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Label labelErrorBirthDate;
	@FXML
	private Label labelErrorBaseSalary;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity is null");
		}
		if (sellerService == null) {
			throw new IllegalStateException("SellerService is null");
		}
		try {
			entity = getFormData();
			sellerService.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DBException e) {
			Alerts.showAlert("Error saving object", e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			e.printStackTrace();
			setErrorsMessages(e.getErrors());
		}
	}

	private void notifyDataChangeListeners() {
		dataChangeListeners.forEach(DataChangeListener::onDataChanged);
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		Constrainsts.setTextFieldInteger(txtId);
		Constrainsts.setTextFieldMaxLength(txtName, 64);
		Constrainsts.setTextFielDouble(txtBaseSalary);
		Constrainsts.setTextFieldMaxLength(txtEmail, 256);
		Utils.setComboBoxFactory(comboBoxDepartment, (Department obj) -> obj.getName());
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity is null");
		}
		if (entity.getId() == null) {
			txtId.setPromptText("not editable");
		} else {
			txtId.setText(String.valueOf(entity.getId()));
		}
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		dpBirthDate.setValue(entity.getBirthDate() == null ? null
				: LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getDepartment() != null) {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService is null");
		}
		List<Department> list = departmentService.findAll();
		this.departments = FXCollections.observableList(list);
		this.comboBoxDepartment.setItems(this.departments);
	}

	private void setErrorsMessages(Map<String, String> errors) {
		errors.forEach((field, error) -> {
			if (field.equals("name"))
				labelErrorName.setText(error);
		});
	}

}
