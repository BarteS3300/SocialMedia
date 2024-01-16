package MAP.repository.db;

import MAP.domain.User;
import MAP.repository.paging.Page;
import MAP.repository.paging.PageImplementation;
import MAP.repository.paging.Pageable;
import MAP.repository.paging.PagingRepository;
import MAP.validators.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDBPagingRepository extends UserDBRepository implements PagingRepository<Long, User> {
    public UserDBPagingRepository(Validator<User> validator, String url, String username, String password) {
        super(validator, url, username, password);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {

        Set<User> users = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");
        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, (pageable.getPageNumber() - 1) * pageable.getPageSize());

            ResultSet resultFindAll = statement.executeQuery();

            while (resultFindAll.next())
            {
                Long id = resultFindAll.getLong("id");
                String username = resultFindAll.getString("username");
                String firstName = resultFindAll.getString("firstname");
                String lastName = resultFindAll.getString("lastname");
                String role = resultFindAll.getString("role");
                String password = resultFindAll.getString("password");
                User user = new User(username, firstName, lastName, role, password);
                user.setId(id);
                users.add(user);

            }
            return new PageImplementation<>(pageable, users.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
