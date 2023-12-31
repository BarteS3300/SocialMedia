package MAP.business;

import MAP.domain.Friendship;
import MAP.domain.Tuple;
import MAP.domain.User;
import MAP.repository.InMemoryRepository;
import MAP.repository.UserDBRepository;
import MAP.repository.Repository;
import MAP.validators.Validator;
import MAP.observer.Observable;
import MAP.observer.Observer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService implements Observable<Observer>{

    private Repository<Long, User> repoUsers;

    private Repository<Tuple<Long, Long>, Friendship> repoFriendships;

    private Validator<User> validator;

    private List<Observer> observers = new ArrayList<>();

    public UserService(Repository<Long, User> repoUsers, Repository<Tuple<Long, Long>, Friendship> repoFriendships) {
        this.repoUsers = repoUsers;
        this.repoFriendships = repoFriendships;
    }

    public Iterable<User> getAll() {
        return repoUsers.getAll();
    }

    public void saveUser(String username, String firstName, String lastName) throws ServiceException{
        User u = new User(username, firstName, lastName);
        Optional<User> saved = repoUsers.save(u);
        if(saved.isPresent()){
            throw new ServiceException("User already exist!");
        }
    }

    public User removeUser(String username) throws ServiceException {
        Optional<User> deleted = Optional.empty();
        for (User user : repoUsers.getAll()) {
            if (Objects.equals(user.getUsername(), username)) {
                deleted = Optional.of(user);
                break;
            }
        }
        if (deleted.isPresent()) {
            repoUsers.delete(deleted.get().getId());
            return deleted.get();
        }
        throw new ServiceException("User doesn't exist!");
    }

    public User updateUser(String username, String newUsername, String newFirstName, String newLastName) throws ServiceException{
        Optional<User> old = Optional.empty();
        User updated = new User(newUsername, newFirstName, newLastName);
        for(User user : repoUsers.getAll()){
            if(Objects.equals(user.getUsername(), username)){
                old = Optional.of(user);
                break;
            }
        }
        if(old.isPresent()){
            updated.setId(old.get().getId());
            if(repoUsers.update(updated).isPresent())
                return old.get();
        }
        throw new ServiceException("User doesn't exist!");
    }

    public void addFriendship(String username1, String username2) throws ServiceException{
        Optional<User> u1 = Optional.empty();
        Optional<User> u2 = Optional.empty();
        for(User user : repoUsers.getAll()){
            if(u1.isEmpty() && Objects.equals(user.getUsername(), username1)){
                u1 = Optional.of(user);
            }
            if(u2.isEmpty() && Objects.equals(user.getUsername(), username2)){
                u2 = Optional.of(user);
            }
        }
        if (u1.isPresent() && u2.isPresent()) {
                Friendship friendship = new Friendship();
                friendship.setId(new Tuple<Long, Long>(u1.get().getId(), u2.get().getId()));
                if(repoFriendships.save(friendship).isEmpty())
                    return;
        }
        throw new ServiceException("Friendship was not added!");

    }

    public Friendship deleteFriendship(String username1, String username2) throws ServiceException{
        Optional<User> u1 = Optional.empty();
        Optional<User> u2 = Optional.empty();
        for(User user : repoUsers.getAll()){
            if(u1.isEmpty() && Objects.equals(user.getUsername(), username1)){
                u1 = Optional.of(user);
            }
            if(u2.isEmpty() && Objects.equals(user.getFirstName(), username2)){
                u2 = Optional.of(user);
            }
        }
        if(u1.isPresent() && u2.isPresent()){
            Tuple<Long, Long> id = new Tuple<Long, Long>(u1.get().getId(), u2.get().getId());
            Optional<Friendship> friendship = repoFriendships.findOne(id);
            if(friendship.isPresent()){
                repoFriendships.delete(id);
                return friendship.get();
            }
        }
        throw new ServiceException("Friendship was not deleted!");
    }


    public int getNumberOfCommunities() {
        ArrayList<Friendship> friendships = getFriendshipList();
        return noConnectedComponents(friendships);
    }

    public List<String> friendsFromAMonth(String username, int month){
        Optional<User> u = Optional.empty();
        for(User user : repoUsers.getAll()){
            if(Objects.equals(user.getUsername(), username)){
                u = Optional.of(user);
                break;
            }
        }
        if(u.isEmpty())
            throw new ServiceException("Nu exista un astfel de utilizator!");
        Optional<User> finalU = u;
        return StreamSupport.stream( repoFriendships.getAll().spliterator(), false)
                .filter(friendship -> (Objects.equals(friendship.getId().getE1(), finalU.get().getId()) || Objects.equals(friendship.getId().getE2(), finalU.get().getId())) && Objects.equals(friendship.getFriendsFrom().getMonthValue(), month))
                .map(friendship -> toFriendshipPrint(friendship, finalU))
                .collect(Collectors.toList());
    }

    private String toFriendshipPrint(Friendship friendship, Optional<User> finalU) {
        return Objects.equals(friendship.getId().getE1(), finalU.get().getId())
                ? repoUsers.findOne(friendship.getId().getE2()).get().getFirstName() + " | " + repoUsers.findOne(friendship.getId().getE2()).get().getLastName() + " | " + friendship.getFriendsFrom()
                : repoUsers.findOne(friendship.getId().getE1()).get().getFirstName() + " | " + repoUsers.findOne(friendship.getId().getE1()).get().getLastName() + " | " + friendship.getFriendsFrom();
    }

    public List<String> getMostSocialCommunity() {
        ArrayList<Friendship> friendships = getFriendshipList();
        return mostSocialCommunity(friendships)
                .stream()
                .map(id->repoUsers.findOne(id).get().toString())
                .collect(Collectors.toList());
    }

    private ArrayList<Friendship> getFriendshipList() {
        ArrayList<Friendship> friendships = new ArrayList<>();
        repoUsers.getAll().forEach(user-> user.getFriends().forEach(friend-> {
            Friendship friendship = new Friendship();
            friendship.setId(new Tuple<Long, Long>(user.getId(), friend));
            friendships.add(friendship);
        }));
        return friendships;
    }

    private static List<Long> mostSocialCommunity(ArrayList<Friendship> friendships) {
        Map<Long, ArrayList<Long>> graph = new HashMap<>();
        createGraph(friendships, graph);
        ArrayList<Long> longest = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        graph.keySet().forEach(node->{
            if (!visited.contains(node)) {
                dfs2(node, visited, graph, longest);
            }
        });
        return longest;
    }

    private static int noConnectedComponents(ArrayList<Friendship> friendships) {
        Map<Long, ArrayList<Long>> graph = new HashMap<>();
        createGraph(friendships, graph);
        Set<Long> visited = new HashSet<>();
        AtomicInteger components = new AtomicInteger();
        // Perform DFS to count connected components.
        graph.keySet().forEach(node->{
            if (!visited.contains(node)) {
                components.getAndIncrement();
                dfs(node, visited, graph);
            }
        });

        return components.get();
    }

    private static void createGraph(ArrayList<Friendship> friendships, Map<Long, ArrayList<Long>> graph) {
        if (friendships == null || friendships.isEmpty()) {
            return; // No friendships means no connected components.
        }

        // Iterate through each friendship tuple and build the graph.
        for (Friendship friendship : friendships) {
            Long person1 = friendship.getId().getE1();
            Long person2 = friendship.getId().getE2();

            // Assuming undirected friendships, add edges in both directions.
            addEdge(person1, person2, graph);
            addEdge(person2, person1, graph);
        }
    }


    private static void addEdge(Long source, Long destination, Map<Long, ArrayList<Long>> graph) {
        graph.computeIfAbsent(source, k -> new ArrayList<>()).add(destination);
    }

    private static void dfs(Long node, Set<Long> visited, Map<Long, ArrayList<Long>> graph) {
        visited.add(node);

        if (graph.containsKey(node)) {

            for (Long neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    dfs(neighbor, visited, graph);
                }
            }
        }
    }

    private static void dfs2(Long node, Set<Long> visited, Map<Long, ArrayList<Long>> graph, ArrayList<Long> longest) {
        visited.add(node);

        if (graph.containsKey(node)) {
            for (Long neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    dfs2(neighbor, visited, graph, longest);
                }
            }
            if(visited.size() > longest.size()){
                longest.clear();
                longest.addAll(visited);
            }

            visited.remove(node);
        }
    }

    public void addObserver(Observer e){
        observers.add(e);
    }
    public void removeObserver(Observer e){
        observers.remove(e);
    }
    public void notifyObservers(){
        observers.forEach(Observer::update);
    }

}
