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
        String findAllSQL = ("select * from users");
        String findFriendsSQL = ("select * from friendships where(user1_id = ? or user2_id = ?)");

        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(findAllSQL);
            ResultSet resultFindAll = statement.executeQuery();
            while (resultFindAll.next()) {

                long id = resultFindAll.getLong("id");
                String firstName = resultFindAll.getString("firstName");
                String lastName = resultFindAll.getString("lastName");

                User user = new User(firstName, lastName);
                user.setId(id);

                PreparedStatement findFriendsStatement = connection.prepareStatement(findFriendsSQL);
                findFriendsStatement.setLong(1, id);
                findFriendsStatement.setLong(2, id);
                ResultSet resultFindFriends = findFriendsStatement.executeQuery();
                while (resultFindFriends.next()) {
                    if (resultFindFriends.getLong(1) == id)
                        user.addFriend(resultFindFriends.getLong(2));
                    else
                        user.addFriend(resultFindFriends.getLong(1));

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
        String findOneSQL = "select * from users where id = ?";
        String findFriendsSQL = "select * from friendships where (user1_id = ? or user2_id = ?)";
        String deleteSQL = "delete from users where id = ?";
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement findOneStatement = connection.prepareStatement(findOneSQL);
            findOneStatement.setLong(1, id);
            ResultSet resultFindOne = findOneStatement.executeQuery();
            if (resultFindOne.next()) {
                String firstName = resultFindOne.getString(2);
                String secondName = resultFindOne.getString(3);
                User deletedUser = new User(firstName, secondName);
                deletedUser.setId(id);

                PreparedStatement findFriendsStatement = connection.prepareStatement(findFriendsSQL);
                findFriendsStatement.setLong(1, id);
                findFriendsStatement.setLong(2, id);
                ResultSet resultFindFriends = findFriendsStatement.executeQuery();
                while (resultFindFriends.next()) {
                    if (resultFindFriends.getLong(1) == id)
                        deletedUser.addFriend(resultFindFriends.getLong(2));
                    else
                        deletedUser.addFriend(resultFindFriends.getLong(1));

                }

                PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
                deleteStatement.setLong(1, id);
                int response = deleteStatement.executeUpdate();
                return response == 0 ? Optional.empty() : Optional.of(deletedUser);
            }
            return Optional.empty();
        } catch (SQLException SQLError) {
            throw new ServiceException("Database connection error! " + SQLError);
        }
    }

    public Optional<User> update(User entity) {
        String deleteSQL = "delete from user where ";
        return Optional.empty();
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

    public Optional<Friendship> deleteFriendship(Friendship friendship){
        String findOneSQL = "select * from friendships where ((user1_id = ? and user2_id = ?) or (user1_id = ? and user2_id = ?))";
        String deleteSQL = "delete from friendships where ((user1_id = ? and user2_id = ?) or (user1_id = ? and user2_id = ?))";
        try{
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);

            PreparedStatement findOneStatement = connection.prepareStatement(findOneSQL);
            findOneStatement.setLong(1, friendship.getId().getE1());
            findOneStatement.setLong(2, friendship.getId().getE2());
            findOneStatement.setLong(3, friendship.getId().getE2());
            findOneStatement.setLong(4, friendship.getId().getE1());
            ResultSet resultFindOne = findOneStatement.executeQuery();
            if(resultFindOne.next()) {
                friendship.setFriendsFrom(resultFindOne.getTimestamp(3).toLocalDateTime());

                PreparedStatement deleteFriendshipStatement = connection.prepareStatement(deleteSQL);
                deleteFriendshipStatement.setLong(1, friendship.getId().getE1());
                deleteFriendshipStatement.setLong(2, friendship.getId().getE2());
                deleteFriendshipStatement.setLong(3, friendship.getId().getE2());
                deleteFriendshipStatement.setLong(4, friendship.getId().getE1());
                int result = deleteFriendshipStatement.executeUpdate();
                return result == 0 ? Optional.empty() : Optional.of(friendship);
            }
            return Optional.empty();
        }catch (SQLException SQLError){
            throw new ServiceException("Database connection error" + SQLError);
        }
    }
}

