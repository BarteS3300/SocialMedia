package MAP;

import MAP.business.UserService;
import MAP.domain.Friendship;
import MAP.interfaces.GUIApplication;
import MAP.interfaces.GUIController;
import MAP.interfaces.UI;
import MAP.repository.FriendshipDBRepository;
import MAP.repository.InMemoryRepository;
import MAP.repository.UserDBRepository;
import MAP.validators.FriendshipValidator;
import MAP.validators.UserValidation;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        UserValidation userValidation = new UserValidation();
        FriendshipValidator friendshipValidation = new FriendshipValidator();
        //InMemoryRepository<Long, User> repo = new InMemoryRepository<>(validation);
        UserDBRepository repoUsers = new UserDBRepository(userValidation, "jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        FriendshipDBRepository repoFriendships = new FriendshipDBRepository(friendshipValidation, "jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        UserService service = new UserService(repoUsers, repoFriendships);
//        GUIApplication.setUserService(service);
        GUIApplication.main(args);
        UI ui = new UI(service);
        ui.run();
    }
}