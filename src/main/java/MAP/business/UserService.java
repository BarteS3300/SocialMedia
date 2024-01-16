package MAP.business;

import MAP.domain.Friendship;
import MAP.domain.Message;
import MAP.domain.Tuple;
import MAP.domain.User;
import MAP.repository.Repository;
import MAP.validators.Validator;
import javafx.scene.control.CheckBox;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService{

    private Repository<Long, User> repoUsers;

    private Repository<Tuple<Long, Long>, Friendship> repoFriendships;

    private Repository<Long, Message> repoMessages;

    private Validator<User> validator;

    public UserService(Repository<Long, User> repoUsers, Repository<Tuple<Long, Long>, Friendship> repoFriendships, Repository<Long, Message> repoMessages){
        this.repoUsers = repoUsers;
        this.repoFriendships = repoFriendships;
        this.repoMessages = repoMessages;
    }

    public Iterable<User> getAll() {
        return repoUsers.getAll();
    }

    public User findOneUser(Long Id) throws ServiceException{
        Optional<User> user = repoUsers.findOne(Id);
        if(user.isPresent())
            return user.get();
        throw new ServiceException("User doesn't exist!");
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

    public Iterable<Friendship> getAllFriendships(){
        return repoFriendships.getAll();
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
                Friendship friendship = new Friendship("accepted");
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
            if(u2.isEmpty() && Objects.equals(user.getUsername(), username2)){
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

    public User login(String username, String password) throws ServiceException{
        for (User user : repoUsers.getAll()) {
            if (Objects.equals(user.getUsername(), username)){
                if(Objects.equals(user.getPassword(), password)) {
                    return user;
                }
                else{
                    throw new ServiceException("Wrong password!");
                }
            }
        }
        throw new ServiceException("Username doesn't exist!");
    }

    public void requestFriendship(String username1, String username2){
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

    public List<Friendship> getFriendRequests(){
        return StreamSupport.stream(repoFriendships.getAll().spliterator(), false)
                .filter(friendship -> Objects.equals(friendship.getStatus(), "pending"))
                .collect(Collectors.toList());
    }

    public void acceptFriendship(String username1, String username2) throws ServiceException{
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
        if(u1.isPresent() && u2.isPresent()){
            Tuple<Long, Long> id = new Tuple<Long, Long>(u1.get().getId(), u2.get().getId());
            Optional<Friendship> friendshipRequest = repoFriendships.findOne(id);
            if(friendshipRequest.isPresent()){
                Friendship friendship = friendshipRequest.get();
                friendship.setStatus("accepted");
                repoFriendships.update(friendship);
                return;
            }
        }
        throw new ServiceException("Friendship was not accepted!");
    }

    public ArrayList<Message> getAllMessages(User user){
        return StreamSupport.stream(repoMessages.getAll().spliterator(), false)
                .filter(message -> Objects.equals(message.getFrom(), user.getId()) || message.getTo().contains(user.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void sendMessage(User user, List<User> toUser, String text){
        Message message = new Message(user.getId(), toUser.stream().map(User::getId).collect(Collectors.toList()), text);
        repoMessages.save(message);
    }

    public void replyMessage(User user, List<User> toUser, String text, Long idMessageReplied){

        Message replyMessage = new Message(user.getId(), toUser.stream().map(User::getId).collect(Collectors.toList()), text, idMessageReplied);
        repoMessages.save(replyMessage);
    }

    public ArrayList<Message> oneMessagePerUser(User user){
        ArrayList<Message> allMessages = getAllMessages(user);
        ArrayList<Message> oneMessage = new ArrayList<>();
        allMessages.sort(Comparator.comparing(Message::getData).reversed());
        for(Message message : allMessages){
//            if(Objects.equals(message.getFrom(), user.getId()) && oneMessage.stream().noneMatch(mes -> Objects.equals(mes.getTo(), message.getTo()))){
//                oneMessage.add(message);
//            }
//            else if(message.getTo().contains(user.getId()) && oneMessage.stream().noneMatch(mes -> Objects.equals(mes.getFrom(), message.getFrom()) && Objects.equals(mes.getTo(), message.getTo()))){
//                oneMessage.add(message);
//            }
            if(message.getTo().contains(user.getId())
                    && oneMessage.stream().noneMatch(mes -> Objects.equals(mes.getFrom(), message.getFrom())
                            || mes.getTo().contains(message.getFrom()))){
                oneMessage.add(message);
            }
            else if(Objects.equals(message.getFrom(), user.getId())
                    && oneMessage.stream().noneMatch(mes -> mes.getTo().contains(message.getTo().get(0))
                            || message.getTo().contains(mes.getFrom()))){
                    oneMessage.add(message);
            }
//            if(message.getTo().contains(user.getId()) &&
//                    oneMessage.stream().noneMatch(mes -> Objects.equals(mes.getFrom(), message.getFrom())
//                            || message.getTo().contains(mes.getFrom()))){
//                oneMessage.add(message);
//            }
//            else if(Objects.equals(message.getFrom(), user.getId())){
//                oneMessage.forEach(mes -> {
//                    for (long toUser : message.getTo())
//                        if (mes.getTo().contains(toUser))
//                            message.getTo().remove(toUser);
//                });
//                if(!message.getTo().isEmpty())
//                    oneMessage.add(message);
//            }
        }
        return oneMessage;
    }

    public List<Message> getMessagesWith(User user, Long with_user_id){
        ArrayList<Message> allMessages = getAllMessages(user);
        ArrayList<Message> messagesWith = new ArrayList<>();
        for(Message message : allMessages){
            if(Objects.equals(message.getFrom(), user.getId()) && message.getTo().contains(with_user_id) || Objects.equals(message.getFrom(), with_user_id) && message.getTo().contains(user.getId()))
                messagesWith.add(message);
        }
        messagesWith.sort(Comparator.comparing(Message::getData));
        return messagesWith;
    }

    public Message findMessage(Long id){
        Optional<Message> message = repoMessages.findOne(id);
        if(message.isPresent())
            return message.get();
        throw new ServiceException("Message doesn't exist!");
    }

    public void updateMessage(Long id, String text){
        Optional<Message> old = repoMessages.findOne(id);
        if(old.isPresent()){
            Message updated = old.get();
            updated.setMessage(text);
            repoMessages.update(updated);
            return;
        }
        throw new ServiceException("Message doesn't exist!");
    }

    public List<Tuple<User, CheckBox>> getAllCheck(){
        Iterable<User> users = repoUsers.getAll();
        List<Tuple<User, CheckBox>> usersCheck = new ArrayList<>();
        for(User user : users){
            Tuple<User, CheckBox> userCheck = new Tuple<User, CheckBox>(user, new CheckBox(){
                @Override
                public void arm() {
                }
            });
            usersCheck.add(userCheck);
        }
        return usersCheck;
    }
}
