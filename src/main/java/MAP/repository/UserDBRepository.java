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
        String findOneSQL = "select * from users where id = ?";
        String findFriendsSQL = "select * from friendships where (user1_id = ? or user2_id = ?) and status = 'accepted'";
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement findOneStatement = connection.prepareStatement(findOneSQL);
            findOneStatement.setLong(1, id);
            ResultSet resultFindOne = findOneStatement.executeQuery();
            if (resultFindOne.next()) {
                String username = resultFindOne.getString("username");
                String firstName = resultFindOne.getString("firstName");
                String secondName = resultFindOne.getString("lastName");
                User user = new User(username, firstName, secondName);
                user.setId(id);

                PreparedStatement findFriendsStatement = connection.prepareStatement(findFriendsSQL);
                findFriendsStatement.setLong(1, id);
                findFriendsStatement.setLong(2, id);
                ResultSet resultFindFriends = findFriendsStatement.executeQuery();
                while (resultFindFriends.next()) {
                    if (resultFindFriends.getLong(1) == id)
                        user.addFriend(resultFindFriends.getLong("user2_id"));
                    else
                        user.addFriend(resultFindFriends.getLong("user1_id"));

                }
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error!");
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
                String username = resultFindAll.getString("username");
                String firstName = resultFindAll.getString("firstname");
                String lastName = resultFindAll.getString("lastname");
                String role = resultFindAll.getString("role");
                String password = resultFindAll.getString("password");

                User user = new User(username, firstName, lastName, role, password);
                user.setId(id);

                PreparedStatement findFriendsStatement = connection.prepareStatement(findFriendsSQL);
                findFriendsStatement.setLong(1, id);
                findFriendsStatement.setLong(2, id);
                ResultSet resultFindFriends = findFriendsStatement.executeQuery();
                while (resultFindFriends.next()) {
                    if (resultFindFriends.getLong("user1_id") == id)
                        user.addFriend(resultFindFriends.getLong("user2_id"));
                    else
                        user.addFriend(resultFindFriends.getLong("user1_id"));

                }
                users.add(user);
            }
            return users;
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error!" + SQLError);
        }
    }

    public Optional<User> save(User entity) {
        validator.validate(entity);
        String insertSQL = "insert into users (username, password, firstname, lastname, role) values(?, ?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password)) {
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getFirstName());
            statement.setString(4, entity.getLastName());
            statement.setString(5, entity.getRole());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error! " + SQLError);
        }
    }

    public boolean delete(Long id) {
        String deleteSQL = "delete from users where id = ?";
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
            deleteStatement.setLong(1, id);
            int response = deleteStatement.executeUpdate();
            return response != 0;
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error! " + SQLError);
        }
    }

    public Optional<User> update(User entity) {
        validator.validate(entity);
        String updateSQL = "update users set username = ?, firstname = ?, lastname = ? where id = ?";
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
            updateStatement.setString(1, entity.getUsername());
            updateStatement.setString(2, entity.getFirstName());
            updateStatement.setString(3, entity.getLastName());
            updateStatement.setLong(4, entity.getId());
            int response =  updateStatement.executeUpdate();
            return response == 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error! " + SQLError);
        }
    }

    public Optional<User> login(String username, String password){
        String loginSQL = "select * from users where id = ? and password = ?";
        try{
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement loginStatement = connection.prepareStatement(loginSQL);
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);
            ResultSet resultLogin = loginStatement.executeQuery();
            if(resultLogin.next()){
                String firstName = resultLogin.getString("firstname");
                String lastName = resultLogin.getString("lastname");
                User user = new User(username, firstName, lastName);
                user.setId(resultLogin.getLong("id"));
                return Optional.of(user);
            }
            return Optional.empty();
        }catch (SQLException SQLError){
            throw new RepositoryException("Database connection error! " + SQLError);
        }
    }

//    public Optional<Friendship> findOneFriendship(Tuple<Long, Long> id){
//        String findOneSQL = "select * from friendships where ((user1_id = ? and user2_id = ?) or (user1_id = ? and user2_id = ?))";
//        try{
//            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
//            PreparedStatement findOneStatement = connection.prepareStatement(findOneSQL);
//            findOneStatement.setLong(1, id.getE1());
//            findOneStatement.setLong(2, id.getE2());
//            findOneStatement.setLong(3, id.getE2());
//            findOneStatement.setLong(4, id.getE1());
//            ResultSet resultFindOne = findOneStatement.executeQuery();
//            if(resultFindOne.next()){
//                Friendship friendship = new Friendship(resultFindOne.getTimestamp("friendsfrom").toLocalDateTime());
//                friendship.setId(id);
//                return Optional.of(friendship);
//            }
//            return Optional.empty();
//
//        } catch (SQLException SQLError){
//            throw  new RepositoryException("Database connection error!" + SQLError);
//        }
//    }
//
//    public List<Friendship> getAllFriendship() {
//        List<Friendship> friendships = new ArrayList<Friendship>();
//        try {
//            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
//            PreparedStatement statement = connection.prepareStatement("select * from friendships");
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                Friendship friendship = new Friendship(resultSet.getTimestamp("friendsfrom").toLocalDateTime());
//                friendship.setId(new Tuple<Long, Long>(resultSet.getLong(1), resultSet.getLong(2)));
//                friendships.add(friendship);
//            }
//            return friendships;
//        } catch (SQLException SQLError) {
//            throw new RepositoryException("Database connection error!" + SQLError);
//        }
//    }
//
//    public Optional<Friendship> saveFriendship(Friendship friendship) {
//        String insertSQL = "insert into friendships (user1_id, user2_id, friendsFrom) values(?, ?, ?)";
//        try {
//            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
//            PreparedStatement statement = connection.prepareStatement(insertSQL);
//            statement.setLong(1, friendship.getId().getE1());
//            statement.setLong(2, friendship.getId().getE2());
//            statement.setTimestamp(3, Timestamp.valueOf(friendship.getFriendsFrom()));
//            int response = statement.executeUpdate();
//            return response == 0 ? Optional.of(friendship) : Optional.empty();
//        } catch (SQLException SQLError) {
//            throw new RepositoryException("Database connection error!" + SQLError);
//        }
//    }
//
//    public boolean deleteFriendship(Tuple<Long, Long> id){
//        String deleteSQL = "delete from friendships where ((user1_id = ? and user2_id = ?) or (user1_id = ? and user2_id = ?))";
//        try{
//            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
//            PreparedStatement deleteFriendshipStatement = connection.prepareStatement(deleteSQL);
//            deleteFriendshipStatement.setLong(1, id.getE1());
//            deleteFriendshipStatement.setLong(2, id.getE2());
//            deleteFriendshipStatement.setLong(3, id.getE2());
//            deleteFriendshipStatement.setLong(4, id.getE1());
//            int result = deleteFriendshipStatement.executeUpdate();
//            return result != 0;
//        }catch (SQLException SQLError){
//            throw new RepositoryException("Database connection error" + SQLError);
//        }
//    }
}

