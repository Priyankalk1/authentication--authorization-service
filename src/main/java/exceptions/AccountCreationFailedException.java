package exceptions;

public class AccountCreationFailedException extends RuntimeException{
    public AccountCreationFailedException(String message) {
        super(message);
    }
}
