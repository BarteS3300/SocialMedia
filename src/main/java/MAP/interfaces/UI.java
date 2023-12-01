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
            System.out.println("The first name of the first user:");
            String fistName1 = reader.readLine();
            System.out.println("The last name of the first user:");
            String lastName1 = reader.readLine();
            service.saveUser(fistName1, lastName1);
            System.out.println("User saved!");
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e) {
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void deleteUser(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("The first name of the first user:");
            String fistName1 = reader.readLine();
            System.out.println("The last name of the first user:");
            String lastName1 = reader.readLine();
            service.removeUser(fistName1, lastName1);
            System.out.println("User deleted!");
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e){
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void saveFriendship(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("The first name of the first user: ");
            String fistName1 = reader.readLine();
            System.out.print("The last name of the first user: ");
            String lastName1 = reader.readLine();
            System.out.print("The first name of the second user: ");
            String fistName2 = reader.readLine();
            System.out.print("The last name of the second user: ");
            String lastName2 = reader.readLine();
            service.addFriendship(fistName1, lastName1, fistName2, lastName2);
            System.out.println("Friendship added!");
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e){
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void deleteFriendship(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("The first name of the first user: ");
            String fistName1 = reader.readLine();
            System.out.print("The last name of the first user: ");
            String lastName1 = reader.readLine();
            System.out.print("The first name of the second user: ");
            String fistName2 = reader.readLine();
            System.out.print("The last name of the second user: ");
            String lastName2 = reader.readLine();
            service.deleteFriendship(fistName1, lastName1, fistName2, lastName2);
            System.out.println("Friendship deleted!");
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e){
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void friendsFromMonth(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.print("The first name of the user: ");
            String firstName = reader.readLine();
            System.out.print("The last name of the user: ");
            String lastName = reader.readLine();
            System.out.print("The month: ");
            int month = Integer.parseInt(reader.readLine());
            service.friendsFromAMonthOfTheYear(firstName, lastName, month).forEach(System.out::println);
        }
        catch(IOException | ValidationException | IllegalArgumentException | ServiceException e){
            System.out.println("Error reading input: \n" + e.getMessage());
        }
    }

    private void nrComunities(){
        System.out.println(service.getNumberOfCommunities());
    }

    private void mostSocialComunity(){
        System.out.println(service.getMostSocialCommunity());
    }
    public void run(){
        while(true) {
            System.out.println("1. Save user\n2. Delete user\n3. Save friendship\n4. Delete friendship\n5. Number of comunities\n6. Most social comunity\n7. Friends from a month\n0. Exit");
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
                    saveFriendship();
                    break;
                case 4:
                    deleteFriendship();
                    break;
                case 5:
                    nrComunities();
                    break;
                case 6:
                    mostSocialComunity();
                    break;
                case 7:
                    friendsFromMonth();
                default:
                    break;
            }
            if(command == 0)
                break;
        }
    }
}
