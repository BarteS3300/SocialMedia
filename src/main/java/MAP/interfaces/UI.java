package MAP.interfaces;

import MAP.business.ServiceException;
import MAP.business.UserService;
import MAP.validators.ValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class UI {

    private final UserService service;

    public UI(UserService service){
        this.service = service;
    }

    private void saveUser(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("The username of the user");
            String username = reader.readLine();
            System.out.println("The first name of the user:");
            String fistName = reader.readLine();
            System.out.println("The last name of the user:");
            String lastName = reader.readLine();
            service.saveUser(username, fistName, lastName);
            System.out.println("User saved!");
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e) {
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void deleteUser(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("The username of the user:");
            String username = reader.readLine();
            service.removeUser(username);
            System.out.println("User deleted!");
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e){
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void updateUser(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("The username of the user");
            String username = reader.readLine();
            System.out.println("The new username of the user");
            String newUsername = reader.readLine();
            System.out.println("The new first name of the user:");
            String newFistName = reader.readLine();
            System.out.println("The new last name of the user:");
            String newLastName = reader.readLine();
            service.updateUser(username, newUsername, newFistName, newLastName);
            System.out.println("User updated!");
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e) {
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void saveFriendship(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("The username of the first user: ");
            String username1 = reader.readLine();
            System.out.print("The username of the second user: ");
            String username2 = reader.readLine();
            service.addFriendship(username1, username2);
            System.out.println("Friendship added!");
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e){
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void deleteFriendship(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("The username of the first user: ");
            String username1 = reader.readLine();
            System.out.print("The username of the second user: ");
            String username2 = reader.readLine();
            service.deleteFriendship(username1, username2);
            System.out.println("Friendship deleted!");
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e){
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void friendsFromMonth(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.print("The username of the user: ");
            String username = reader.readLine();
            System.out.print("The month: ");
            int month = Integer.parseInt(reader.readLine());
            service.friendsFromAMonth(username, month).forEach(System.out::println);
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e){
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void nrCommunities(){
        System.out.println(service.getNumberOfCommunities());
    }

    private void mostSocialCommunity(){
        System.out.println(service.getMostSocialCommunity());
    }
    public void run(){
        while(true) {
            System.out.println("1. Save user\n2. Delete user\n3. Update user\n4. Save friendship\n5. Delete friendship\n6. Number of communities\n7. Most social community\n8. Friends from a month\n9. All users\n0. Exit");
            System.out.println("Command:");
            Scanner in = new Scanner(System.in);
            int command = Integer.parseInt(in.nextLine());
            switch(command){
                case 1:
                    saveUser();
                    break;
                case 2:
                    deleteUser();
                    break;
                case 3:
                    updateUser();
                    break;
                case 4:
                    saveFriendship();
                    break;
                case 5:
                    deleteFriendship();
                    break;
                case 6:
                    nrCommunities();
                    break;
                case 7:
                    mostSocialCommunity();
                    break;
                case 8:
                    friendsFromMonth();
                    break;
                case 9:
                    service.getAll().forEach(x->System.out.println(x.toString()));
                    break;
                default:
                    break;
            }
            if(command == 0)
                break;
        }
    }
}
