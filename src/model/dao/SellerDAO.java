package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDAO extends DataAccessObject<Seller> {

	List<Seller> findByDepartment(Department department);

}
