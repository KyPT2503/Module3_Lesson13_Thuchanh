import User.User;

import java.sql.*;

public class DBController {
    public static void main(String[] args) {
        addUserPermission(new User(105, "phanthuanhthu", "VN", "AT@gmail.com"), new int[]{1, 2, 4});
    }

    /*method create new user and add permission, using rollback & commit method of Connection instance*/
    /*if success, commit and finish, else remove all changes*/
    private static void addUserPermission(User user, int[] permissions) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatementPermission = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "250399");
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("insert into mydatabase.user (id, name, country, email) value (?,?,?,?)");
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getCountry());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.executeUpdate();

            preparedStatementPermission = connection.prepareStatement("insert into mydatabase.user_permission(user_id, permission_id) value (?,?)");
            for (int permission : permissions) {
                preparedStatementPermission.setInt(1, user.getId());
                preparedStatementPermission.setInt(2, permission);
                preparedStatementPermission.executeUpdate();
            }

            connection.commit();
        } catch (ClassNotFoundException e) {
            System.out.println("Cant load Driver.");
        } catch (SQLException e) {
            System.out.println("SQLException.");
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                preparedStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                preparedStatementPermission.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private static void addNewUser(int id, String name, String country, String email) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "250399");
            callableStatement = connection.prepareCall("{call usp_insertUser(?,?,?,?)}");
            callableStatement.setInt(1, id);
            callableStatement.setString(2, name);
            callableStatement.setString(3, country);
            callableStatement.setString(4, email);
            callableStatement.execute();
        } catch (ClassNotFoundException e) {
            System.out.println("Can't load Drive");
        } catch (SQLException e) {
            System.out.println("SQLException");
        } finally {
            try {
                callableStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*Using stored procedure*/
    private static void showAllUser() {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "250399");
            callableStatement = connection.prepareCall("{call usp_getUserById(?)}");
            callableStatement.setInt(1, 100);
            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id"));
                System.out.println(resultSet.getString("name"));
                System.out.println(resultSet.getString("country"));
                System.out.println(resultSet.getString("email"));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Cant load Driver.");
        } catch (SQLException e) {
            System.out.println("SQLException.");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (callableStatement != null) {
                try {
                    callableStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
