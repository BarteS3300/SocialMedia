package MAP.repository;

import MAP.business.ServiceException;
import MAP.domain.Friendship;
import MAP.domain.Tuple;
import MAP.domain.User;
import MAP.validators.Validator;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements Repository<Long, User> {

    private String url;

    private String username;

    private String password;

    private Validator<User> validator;

    public UserDBRepository(Validator<User> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = username;
    }

    public Optional<User> findOne(Long id) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                User u = new User(firstName, lastName);
                u.setId(id);
                return Optional.of(u);
            }
            return Optional.empty();
        } catch (SQLException SQLError) {
            throw new ServiceException("Database connection error!");
        }
    }

    public Iterable<User> getAll() {
        Set<User> users = new HashSet<User>();
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement("select * from users");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");

                User user = new User(firstName, lastName);
                user.setId(id);


                PreparedStatement statement2 = connection.prepareStatement("select * from friendships where user1_id = ?");
                statement2.setLong(1, id);
                ResultSet resultSet2 = statement2.executeQuery();
                while (resultSet2.next()) {
                    user.addFriend(resultSet2.getLong("user2_id"));
                }

                PreparedStatement statement3 = connection.prepareStatement("select * from friendships where user2_id = ?");
                statement3.setLong(1, id);
                ResultSet resultSet3 = statement3.executeQuery();
                while (resultSet3.next()) {
                    user.addFriend(resultSet3.getLong("user1_id"));
                }

                users.add(user);
            }
            return users;
        } catch (SQLException SQLError) {
            throw new ServiceException("Database connection error!" + SQLError);
        }
    }

    public Optional<User> save(User entity) {
        String insertSQL = "insert into users (firstname, lastname) values(?, ?)";

        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException SQLError) {
            throw new ServiceException("Database connection error! " + SQLError);
        }
    }

    public Optional<User> delete(Long id) {
        return Optional.empty();
    }

    public Optional<User> update(User entity) {
        return Optional.empty();
    }

    public Optional<Friendship> saveFriendship(Friendship friendship) {
        String insertSQL = "insert into friendships (user1_id, user2_id, friendsFrom) values(?, ?, ?)";
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setLong(1, friendship.getId().getE1());
            statement.setLong(2, friendship.getId().getE2());
            statement.setTimestamp(3, Timestamp.valueOf(friendship.getFriendsFrom()));
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(friendship) : Optional.empty();
        } catch (SQLException SQLError) {
            throw new ServiceException("Database connection error!" + SQLError);
        }
    }

    public List<Friendship> getAllFriendship() {
        List<Friendship> friendships = new ArrayList<Friendship>();
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Friendship friendship = new Friendship(resultSet.getTimestamp(3).toLocalDateTime());
                friendship.setId(new Tuple<Long, Long>(resultSet.getLong(1), resultSet.getLong(2)));
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException SQLError) {
            throw new ServiceException("Database connection error!" + SQLError);
        }
    }
}
