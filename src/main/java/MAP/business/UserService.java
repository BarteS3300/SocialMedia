package MAP.business;

import MAP.domain.Friendship;
import MAP.domain.Tuple;
import MAP.domain.User;
import MAP.repository.InMemoryRepository;
import MAP.repository.Repository;
import MAP.business.ServiceException;

import java.util.*;

public class UserService{

    private Repository<Long, User> repo;

    public UserService(InMemoryRepository<Long, User> repo) {
        this.repo = repo;
    }

    public Iterable<User> getAll() {
        return repo.getAll();
    }

    public void saveUser(String firstName, String lastName) throws ServiceException{
        User u = new User(firstName, lastName);
        u.setId((long) u.hashCode());
        Optional<User> saved = repo.save(u);
        if(saved.isPresent()){
            throw new ServiceException("User already exist!");
        }
    }

    public User removeUser(String firstName, String lastName) throws ServiceException{
        User u = new User(firstName, lastName);
        u.setId((long) u.hashCode());
        try {
            for (Long id : repo.findOne(u.getId()).get().getFriends())
                repo.findOne(id).get().removeFriend(u.getId());
            return repo.delete(u.getId()).get();
        } catch (Exception e) {
            throw new ServiceException("User doesn't exist!");
        }
    }

    public User updateUser(String firstName, String lastName, String updatedFirstName, String updatedLastName) throws ServiceException{
        User u = new User(firstName, lastName);
        u.setId((long) u.hashCode());
        User updatedU = new User(firstName, lastName);
        Optional<User> updated = repo.update(u);
        if(updated.isEmpty()){
            throw new ServiceException("User doesn't exist!");
        }
        return updated.get();
    }

    public void addFriendship(String firstName1, String lastName1, String firstName2, String lastName2) throws ServiceException{
        User u1 = new User(firstName1, lastName1);
        u1.setId((long) u1.hashCode());
        User u2 = new User(firstName2, lastName2);
        u2.setId((long) u2.hashCode());
        try {
            if (repo.findOne(u1.getId()).isPresent() && repo.findOne(u2.getId()).isPresent()) {
                repo.findOne(u1.getId()).get().addFriend(u2.getId());
                repo.findOne(u2.getId()).get().addFriend(u1.getId());
            }
        }catch(Exception e){
            throw new ServiceException("Friendship was not added!");
        }

    }

    public void deleteFriendship(String firstName1, String lastName1, String firstName2, String lastName2) throws ServiceException{
        User u1 = new User(firstName1, lastName1);
        u1.setId((long) u1.hashCode());
        User u2 = new User(firstName2, lastName2);
        u2.setId((long) u2.hashCode());
        try {
            if (repo.findOne(u1.getId()).isPresent() && repo.findOne(u2.getId()).isPresent()) {
                repo.findOne(u1.getId()).get().removeFriend(u2.getId());
                repo.findOne(u2.getId()).get().removeFriend(u1.getId());
            }
        }catch(Exception e){
            throw new ServiceException("Friendship was not added!");
        }

    }


    public int getNumberOfCommunities() {
        ArrayList<Friendship> friendships = getFriendshipList();
        return noConnectedComponents(friendships);
    }

    public List<Long> getMostSocialCommunity() {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Friendship> friendships = getFriendshipList();
        return mostSocialCommunity(friendships);
    }

    private ArrayList<Friendship> getFriendshipList() {
        ArrayList<Friendship> friendships = new ArrayList<>();

        for (User user : repo.getAll()) {
            for (Long friend : user.getFriends()) {
                Friendship friendship = new Friendship();
                friendship.setId(new Tuple<Long, Long>(user.getId(), friend));
                friendships.add(friendship);
            }
        }

        return friendships;
    }

    private static List<Long> mostSocialCommunity(ArrayList<Friendship> friendships) {
        Map<Long, ArrayList<Long>> graph = new HashMap<>();
        createGraph(friendships, graph);
        ArrayList<Long> longest = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        for (Long node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfs2(node, visited, graph, longest);
            }
        }

        return longest;
    }

    private static List<Long> mostSocialCommunity2(ArrayList<Friendship> friendships) {
        Map<Long, ArrayList<Long>> graph = new HashMap<>();
        createGraph(friendships, graph);
        ArrayList<Long> largestComponent = new ArrayList<>();
        ArrayList<Long> currentComponent = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        for (Long node : graph.keySet()) {
            if (!visited.contains(node)) {
                currentComponent.clear();
                dfs2(node, visited, graph, largestComponent);
                if (currentComponent.size() > largestComponent.size()) {
                    largestComponent = new ArrayList<>(currentComponent);
                }
            }
        }

        return largestComponent;
    }

    private static int noConnectedComponents(ArrayList<Friendship> friendships) {
        Map<Long, ArrayList<Long>> graph = new HashMap<>();
        createGraph(friendships, graph);
        Set<Long> visited = new HashSet<>();
        int components = 0;
        // Perform DFS to count connected components.
        for (Long node : graph.keySet()) {
            if (!visited.contains(node)) {
                components++;
                dfs(node, visited, graph);
            }
        }

        return components;
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


}
