package MAP.validators;

import MAP.domain.User;

public class UserValidation implements Validator<User>{
    @Override
    public void validate(User entity) throws ValidationException
    {
        if (entity.getFirstName().length() < 3 || entity.getFirstName().length() < 3)
            throw new ValidationException("Name have to have at lest 3 letters\n");
        if (entity.getLastName().contains("1234567890") || entity.getFirstName().contains("1234567890"))
            throw new ValidationException("Name can't contain numbers\n");
    }
}
