package dataBase;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RequestDB {

//    public void createUser(String name, String login, String password) {
//        try (Connection connection = ConnectorDB.getConnect()) {
//            connection.setAutoCommit(false);
//            PreparedStatement preparedStatement = connection.prepareStatement(
//                    "INSERT INTO users (name,login,password) VALUES (?,?,?)");
//            preparedStatement.setString(1, name);
//            preparedStatement.setString(2, login);
//            preparedStatement.setString(3, password);
//            preparedStatement.execute();
//            connection.commit();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public void createUser(String name, String login, String password) {
        Connection connection = ConnectorDB.getConnect();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.
                    prepareStatement("INSERT INTO users (`name`, `login`, `password`) VALUES (?,?,?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, password);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<User> findUser(String login, String password) {
        try (Connection connection = ConnectorDB.getConnect()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    " SELECT * FROM users WHERE login=? AND password=?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(
                        new User(
                                resultSet.getString("name"),
                                resultSet.getString("login"),
                                resultSet.getString("password")
                        ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}