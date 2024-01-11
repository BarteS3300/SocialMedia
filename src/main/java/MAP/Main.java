package MAP;

import MAP.business.UserService;
import MAP.domain.User;
import MAP.interfaces.UI;
import MAP.repository.FriendshipDBRepository;
import MAP.repository.MessageDBRepository;
import MAP.repository.UserDBRepository;
import MAP.validators.FriendshipValidator;
import MAP.validators.UserValidation;

import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        UserValidation userValidation = new UserValidation();
        FriendshipValidator friendshipValidation = new FriendshipValidator();
        //InMemoryRepository<Long, User> repo = new InMemoryRepository<>(validation);
        UserDBRepository repoUsers = new UserDBRepository(userValidation, "jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        FriendshipDBRepository repoFriendships = new FriendshipDBRepository(friendshipValidation, "jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        MessageDBRepository repoMessages = new MessageDBRepository("jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        UserService service = new UserService(repoUsers, repoFriendships, repoMessages);
        System.out.println(service.oneMessagePerUser(service.findOneUser((long)34)));
//        UI ui = new UI(service);
//        ui.run();
    }
}