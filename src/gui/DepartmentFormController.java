package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constrainsts;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;
	private DepartmentService service;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	private Department getFormData() {
		validateFormData();
		Integer id = Utils.tryParseToInt(txtId.getText());
		String name = txtName.getText();
		return new Department(id, name);
	}

	private void validateFormData() {
		ValidationException e = new ValidationException("Validation error");
		String name = txtName.getText();

		if (name == null || name.isBlank())
			e.addError("name", "Field cannot be empty");
		if (!e.getErrors().isEmpty())
			throw e;
	}

	public void setService(DepartmentService service) {
		this.service = service;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		this.dataChangeListeners.add(listener);
	}

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Label labelErrorName;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity is null");
		}
		if (service == null) {
			throw new IllegalStateException("Service is null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DBException e) {
			Alerts.showAlert("DB Exception", "Error saving object", e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorsMessages(e.getErrors());
		}
	}

	private void notifyDataChangeListeners() {
		dataChangeListeners.forEach(DataChangeListener::onDataChanged);
	}

	@FXML
	public void onBtCancelAction() {
		System.out.println("DepartmentFormController.onBtCancelAction()");
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		Constrainsts.setTextFieldInteger(txtId);
		Constrainsts.setTextFieldMaxLength(txtName, 30);
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
	}

	private void setErrorsMessages(Map<String, String> errors) {
		errors.forEach((field, error) -> {
			if (field.equals("name"))
				labelErrorName.setText(error);
		});
	}

}
