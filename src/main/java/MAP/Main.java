package MAP;

import MAP.business.UserService;
import MAP.repository.db.FriendshipDBRepository;
import MAP.repository.db.MessageDBRepository;
import MAP.repository.db.UserDBPagingRepository;
import MAP.repository.db.UserDBRepository;
import MAP.validators.FriendshipValidator;
import MAP.validators.UserValidation;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        UserValidation userValidation = new UserValidation();
        FriendshipValidator friendshipValidation = new FriendshipValidator();
        //InMemoryRepository<Long, User> repo = new InMemoryRepository<>(validation);
        UserDBPagingRepository repoUsers = new UserDBPagingRepository(userValidation, "jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        FriendshipDBRepository repoFriendships = new FriendshipDBRepository(friendshipValidation, "jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        MessageDBRepository repoMessages = new MessageDBRepository("jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        UserService service = new UserService(repoUsers, repoFriendships, repoMessages);
        service.updateMessage((long)14, "test2!");

//        UI ui = new UI(service);
//        ui.run();
    }
}