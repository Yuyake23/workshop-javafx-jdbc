package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DBException;
import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;

public class SellerDAO_JDBC implements SellerDAO {

	private Connection conn;

	public SellerDAO_JDBC(Connection conn) {
		super();
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("""
					INSERT INTO seller
					(name, email, birthDate, baseSalary, departmentId)
					VALUES
					(?, ?, ?, ?, ?);
					""", Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
					obj.getDepartment()
							.setName(new DepartmentDAO_JDBC(conn).findById(obj.getDepartment().getId()).getName());
				}
				DB.closeResultSet(rs);
			} else {
				throw new DBException("Unexpected error! No rows affected!");
			}

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("""
					UPDATE seller
					SET name = ?, email = ?, birthDate = ?, baseSalary = ?, departmentId = ?
					WHERE id = ?;
					""", Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
					obj.getDepartment()
							.setName(new DepartmentDAO_JDBC(conn).findById(obj.getDepartment().getId()).getName());
				}
				DB.closeResultSet(rs);
			} else {
				throw new DBException("Unexpected error! No rows affected!");
			}

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE id = ?;", Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, id);

			int rows = st.executeUpdate();

			if (rows == 0)
				throw new DBException("No row affected");

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("""
					SELECT s.*, d.name as DepName
					FROM seller s JOIN department d
					ON s.departmentId = d.id
					WHERE s.id = ?;
					""");

			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next())
				return instanciateSeller(rs, instanciateDepartment(rs));
			return null;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("""
					SELECT s.*, d.name as DepName
					FROM seller s JOIN department d
					ON s.departmentId = d.id
					ORDER BY s.name ASC;
					""");

			rs = st.executeQuery();

			List<Seller> sellers = new ArrayList<>();
			Map<Integer, Department> departments = new HashMap<>();
			while (rs.next()) {
				Department dep = departments.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instanciateDepartment(rs);
					departments.put(rs.getInt("DepartmentId"), dep);
				}

				sellers.add(instanciateSeller(rs, dep));
			}
			return sellers;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("""
					SELECT s.*, d.name as DepName
					FROM seller s JOIN department d
					ON s.departmentId = d.id
					WHERE s.departmentId = ?
					ORDER BY s.name ASC;
					""");

			st.setInt(1, department.getId());
			rs = st.executeQuery();

			List<Seller> sellers = new ArrayList<>();
			if (rs.next()) {
				department = instanciateDepartment(rs);
				while (rs.next()) {
					sellers.add(instanciateSeller(rs, department));
				}
			}
			return sellers;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private static Seller instanciateSeller(ResultSet rs, Department department) throws SQLException {
		Seller seller = new Seller();

		seller.setId(rs.getInt("id"));
		seller.setName(rs.getString("name"));
		seller.setEmail(rs.getString("email"));
		seller.setBaseSalary(rs.getDouble("baseSalary"));
		seller.setBirthDate(new java.util.Date(rs.getDate("birthDate").getTime()));
		seller.setDepartment(department);
		return seller;
	}

	private static Department instanciateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("departmentId"), rs.getString("DepName"));
	}

}
