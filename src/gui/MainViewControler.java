package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class MainViewControler implements Initializable {

	// Components
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;

	// Actions
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("MainViewControler.onMenuItemSellerAction()");
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		System.out.println("MainViewControler.onMenuItemDepartmentAction()");
	}

	@FXML
	public void onMenuItemAboutAction() {
		System.out.println("MainViewControler.onMenuItemAboutAction()");
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {

	}

}
