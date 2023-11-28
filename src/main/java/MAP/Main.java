package MAP;

import MAP.business.UserService;
import MAP.domain.User;
import MAP.interfaces.UI;
import MAP.repository.InMemoryRepository;
import MAP.validators.UserValidation;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        UserValidation validation = new UserValidation();
        InMemoryRepository<Long, User> repo = new InMemoryRepository<>(validation);
        UserService service = new UserService(repo);
        service.saveUser("nume1", "prenume1");
        service.saveUser("nume2", "prenume2");
        service.saveUser("nume3", "prenume3");
        service.saveUser("nume4", "prenume4");
        service.addFriendship("nume1", "prenume1", "nume2", "prenume2");
        service.addFriendship("nume1", "prenume1", "nume3", "prenume3");
        service.addFriendship("nume4", "prenume4", "nume3", "prenume3");
        service.saveUser("nume5", "prenume5");
        service.saveUser("nume6", "prenume6");
        service.saveUser("nume7", "prenume7");
        service.saveUser("nume8", "prenume8");
        service.saveUser("nume9", "prenume9");
        service.addFriendship("nume5", "prenume5", "nume6", "prenume6");
        service.addFriendship("nume5", "prenume5", "nume7", "prenume7");
        service.addFriendship("nume5", "prenume5", "nume8", "prenume8");
        service.addFriendship("nume6", "prenume6", "nume9", "prenume9");
        System.out.println(service.getAll());

        UI ui = new UI(service);
        ui.run();
    }
}