package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constrainsts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {

	private Department entity;

	public void setDepartment(Department entity) {
		this.entity = entity;
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
	public void onBtSaveAction() {
		System.out.println("DepartmentFormController.onBtSaveAction()");
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
		if (this.entity == null) {
			throw new IllegalStateException("Entity is null");
		}
		if (this.entity.getId() == null) {
			this.txtId.setPromptText("not editable");
		} else {
			this.txtId.setText(String.valueOf(this.entity.getId()));
		}
		this.txtName.setText(this.entity.getName());
	}

}
