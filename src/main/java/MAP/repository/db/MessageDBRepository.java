package MAP.repository.db;

import MAP.domain.Message;
import MAP.repository.Repository;
import MAP.repository.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDBRepository implements Repository<Long, Message> {

    private String url;

    private String username;

    private String password;

    public MessageDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        String findOneSQL = "select * from message where id = ?";
        String findOne2SQL = "select * from tofrom where id = ?";
        try{
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement findOneStatement = connection.prepareStatement(findOneSQL);
            findOneStatement.setLong(1, id);
            ResultSet resultFindOne = findOneStatement.executeQuery();
            if(resultFindOne.next()){
                Message message = new Message(resultFindOne.getString("message"), resultFindOne.getTimestamp("data").toLocalDateTime(), (Long) resultFindOne.getObject("reply"));
                message.setId(id);
                PreparedStatement findOne2Statement = connection.prepareStatement(findOne2SQL);
                findOne2Statement.setLong(1, id);
                ResultSet resultFindOne2 = findOne2Statement.executeQuery();
                while(resultFindOne2.next())
                {
                    message.setFrom(resultFindOne2.getLong("from_id"));
                    message.addTo(resultFindOne2.getLong("to_id"));
                }
                return Optional.of(message);
            }
            return Optional.empty();

        } catch (SQLException SQLError){
            throw new RepositoryException("Database connection error!" + SQLError);
        }
    }

    @Override
    public Iterable<Message> getAll() {
        List<Message> messages = new ArrayList<Message>();
        String getAllSQL = "select * from message";
        String getAll2SQL = "select * from tofrom where id = ?";
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(getAllSQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Message messageInfo = new Message(resultSet.getString("message"), resultSet.getTimestamp("data").toLocalDateTime(), (Long) resultSet.getObject("reply"));
                messageInfo.setId(resultSet.getLong("id"));
                PreparedStatement statement1 = connection.prepareStatement(getAll2SQL);
                statement1.setLong(1, resultSet.getLong("id"));
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()) {
                    Message message = new Message(messageInfo.getMessage(), messageInfo.getData(), messageInfo.getReply());
                    message.setId(messageInfo.getId());
                    message.setFrom(resultSet1.getLong("from_id"));
                    message.addTo(resultSet1.getLong("to_id"));
                    messages.add(message);
                }
            }
            return messages;

        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error!" + SQLError);
        }
    }

    @Override
    public Optional<Message> save(Message message) {
        String insertSQL = "insert into message(message, data, reply) values(?, ?, ?)";
        String insert2SQL = "insert into tofrom(id, from_id, to_id) values(?, ?, ?)";
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, message.getMessage());
            statement.setTimestamp(2, Timestamp.valueOf(message.getData()));
            statement.setObject(3, message.getReply());
            int response = statement.executeUpdate();
            if(response == 0)
                return Optional.of(message);
            ResultSet resultSet = statement.getGeneratedKeys();
            while(resultSet.next())
            {
                message.setId(resultSet.getLong(1));
            }
            PreparedStatement statement1 = connection.prepareStatement(insert2SQL);
            statement1.setLong(1, message.getId());
            statement1.setLong(2, message.getFrom());
            for(Long i : message.getTo())
            {
                statement1.setLong(3, i);
                int response1 = statement1.executeUpdate();
                if(response1 == 0)
                    return Optional.of(message);
            }
            return Optional.empty();
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error!" + SQLError);
        }
    }

    @Override
    public boolean delete(Long aLong) {
        return false;
    }

    @Override
    public Optional<Message> update(Message entity) {
        String updateSQL = "update message set message = ?, data = ?, reply = ? where id = ?";
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setString(1, entity.getMessage());
            statement.setTimestamp(2, Timestamp.valueOf(entity.getData()));
            statement.setObject(3, entity.getReply());
            statement.setLong(4, entity.getId());
            int response = statement.executeUpdate();
            if(response == 0)
                return Optional.of(entity);
            return Optional.empty();
        } catch (SQLException SQLError) {
            throw new RepositoryException("Database connection error!" + SQLError);
        }
    }
}
