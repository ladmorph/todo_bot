package org.ladmorph.telegramBots.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DataBaseManager {

    private static final Logger log = LoggerFactory.getLogger(DataBaseManager.class);

    private static DataBaseManager instance;
    private final Connection connection;

    /**
     * Making constructor as private for singleton pattern
     */
    private DataBaseManager() {
        connection = DataBaseConnection.getConnection();
    }

    /**
     * Get singleton class
     *
     * @return DataBaseManager instance
     */
    public static DataBaseManager getInstance() {
        if (instance == null)
            instance = new DataBaseManager();
        return instance;
    }

    /**
     *
     * @param userId
     * @param status
     * @return true if the try block was executed successfully
     */
    public boolean setUserState(Integer userId, boolean status) {
        int result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (user_id, status) VALUES(?, ?)"
            );
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, status ? 1 : 0);
            try {
                result = preparedStatement.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
               updateUserState(userId, status);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return result > 0;
    }

    /**
     * @param userId
     * @return true if status == 1
     */
    public boolean getUserState(Integer userId) {
        int status = -1;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT status FROM users WHERE user_id=?"
            );
            preparedStatement.setInt(1, userId);

            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
                status = result.getInt("status");

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return status == 1;
    }

    public void updateUserState(Integer userId, boolean status) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE users SET status=? WHERE user_id=?"
            );
            preparedStatement.setInt(1, status ? 1 : 0);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }
}

