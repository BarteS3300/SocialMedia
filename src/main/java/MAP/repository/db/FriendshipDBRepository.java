package MAP.repository.db;

import MAP.business.ServiceException;
import MAP.domain.Friendship;
import MAP.domain.Tuple;
import MAP.repository.Repository;
import MAP.repository.RepositoryException;
import MAP.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship> {

    private String url;

    private String username;

    private String password;

    private Validator<Friendship> validator;


    public FriendshipDBRepository(Validator<Friendship> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        String findOneSQL = "select * from friendships where ((user1_id = ? and user2_id = ?) or (user1_id = ? and user2_id = ?))";
        try{
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement findOneStatement = connection.prepareStatement(findOneSQL);
            findOneStatement.setLong(1, id.getE1());
            findOneStatement.setLong(2, id.getE2());
            findOneStatement.setLong(3, id.getE2());
            findOneStatement.setLong(4, id.getE1());
            ResultSet resultFindOne = findOneStatement.executeQuery();
            if(resultFindOne.next()){
                Friendship friendship = new Friendship(resultFindOne.getTimestamp("friendsfrom").toLocalDateTime(), resultFindOne.getString("status"));
                friendship.setId(id);
                return Optional.of(friendship);
            }
            return Optional.empty();

        } catch (SQLException SQLError){
            throw new RepositoryException("Database connection error!" + SQLError);
        }
    }

    @Override
    public Iterable<Friendship> getAll() {
        List<Friendship> friendships = new ArrayList<Friendship>();
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Friendship friendship = new Friendship(resultSet.getTimestamp("friendsfrom").toLocalDateTime(), resultSet.getString("status"));
                friendship.setId(new Tuple<Long, Long>(resultSet.getLong(1), resultSet.getLong(2)));
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error!" + SQLError);
        }
    }

    @Override
    public Optional<Friendship> save(Friendship friendship) {
        validator.validate(friendship);
        String insertSQL = "insert into friendships (user1_id, user2_id, friendsFrom, status) values(?, ?, ?, ?)";
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setLong(1, friendship.getId().getE1());
            statement.setLong(2, friendship.getId().getE2());
            statement.setTimestamp(3, Timestamp.valueOf(friendship.getFriendsFrom()));
            statement.setString(4, friendship.getStatus());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(friendship) : Optional.empty();
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error!" + SQLError);
        }
    }

    @Override
    public boolean delete(Tuple<Long, Long> id) {
        String deleteSQL = "delete from friendships where ((user1_id = ? and user2_id = ?) or (user2_id = ? and user1_id = ?))";
        try{
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement deleteFriendshipStatement = connection.prepareStatement(deleteSQL);
            deleteFriendshipStatement.setLong(1, id.getE1());
            deleteFriendshipStatement.setLong(2, id.getE2());
            deleteFriendshipStatement.setLong(3, id.getE2());
            deleteFriendshipStatement.setLong(4, id.getE1());
            int result = deleteFriendshipStatement.executeUpdate();
            return result != 0;
        }catch (SQLException SQLError){
            throw new RepositoryException("Database connection error" + SQLError);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship friendship) {
        String updateSQL = "update friendships set status = ? where (user1_id = ? and user2_id = ?) or (user1_id = ? and user2_id = ?)";
        try{
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setString(1, friendship.getStatus());
            statement.setLong(2, friendship.getId().getE1());
            statement.setLong(3, friendship.getId().getE2());
            statement.setLong(4, friendship.getId().getE2());
            statement.setLong(5, friendship.getId().getE1());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.empty() : Optional.of(friendship);
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error!" + SQLError);
        }
    }
}
