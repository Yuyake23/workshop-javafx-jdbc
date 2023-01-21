package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constrainsts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable {

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

}
