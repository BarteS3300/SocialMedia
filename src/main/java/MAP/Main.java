package MAP;

import MAP.business.UserService;
import MAP.domain.User;
import MAP.interfaces.UI;
import MAP.repository.InMemoryRepository;
import MAP.repository.UserDBRepository;
import MAP.validators.UserValidation;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        UserValidation validation = new UserValidation();
        //InMemoryRepository<Long, User> repo = new InMemoryRepository<>(validation);
        UserDBRepository repo = new UserDBRepository(validation, "jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        UserService service = new UserService(repo);
        System.out.println(service.getAll());

        UI ui = new UI(service);
        ui.run();
    }
}