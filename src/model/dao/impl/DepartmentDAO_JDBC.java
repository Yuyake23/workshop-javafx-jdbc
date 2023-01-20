package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DBException;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartmentDAO_JDBC implements DepartmentDAO {

	private Connection conn;

	public DepartmentDAO_JDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("""
					INSERT INTO department
					(name)
					VALUES
					(?);
					""", Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
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
	public void update(Department obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("""
					UPDATE department
					SET name = ?
					WHERE id = ?;
					""", Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
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
			st = conn.prepareStatement("DELETE FROM department WHERE id = ?;", Statement.RETURN_GENERATED_KEYS);
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
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("""
					SELECT *
					FROM department
					WHERE id = ?;
					""");

			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next())
				return new Department(id, rs.getString("name"));
			return null;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("""
					SELECT *
					FROM department
					ORDER BY name ASC;
					""");

			rs = st.executeQuery();

			List<Department> departments = new ArrayList<>();
			while (rs.next()) {
				departments.add(new Department(rs.getInt("id"), rs.getString("name")));
			}
			return departments;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
