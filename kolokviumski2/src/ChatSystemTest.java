import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class NoSuchRoomException extends Exception{
    public NoSuchRoomException(String roomName){
        super("No such room: " + roomName);
    }
}
class NoSuchUserException extends Exception{
    public NoSuchUserException(String userName){
        super("No such room: " + userName);
    }
}
class ChatRoom{
    String name;
    Set<String> userNames;
    public ChatRoom(String name) {
        this.name = name;
        userNames = new TreeSet<>();
    }

    public void addUser(String username) {
        userNames.add(username);
    }

    public void removeUser(String username) {
        userNames.remove(username);
    }

    public boolean hasUser(String username) {
        return userNames.contains(username);
    }
    public int numUsers(){
        return userNames.size();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        if(userNames.isEmpty()){
            sb.append("EMPTY\n");
        }else {
            userNames.forEach(str -> sb.append(str).append("\n"));
        }
        return sb.toString();
    }
}
class ChatSystem{
    Map<String, ChatRoom> rooms;
    Set<String> users;
    public ChatSystem(){
        rooms = new TreeMap<>();
        users = new TreeSet<>();
    }
    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        ChatRoom cr = rooms.get(roomName);
        if (cr == null)
            throw new NoSuchRoomException(roomName);
        return cr;
    }
    public void addRoom(String roomName){
        rooms.putIfAbsent(roomName, new ChatRoom(roomName));
    }
    public void removeRoom(String roomName){
        rooms.remove(roomName);
    }
    public void register(String name){
        if (!users.add(name)) return;
        ChatRoom targetRoom = null;
        int minUsers = Integer.MAX_VALUE;

        for (ChatRoom room : rooms.values()) {
            int roomUsers = room.numUsers();
            if (roomUsers < minUsers) {
                minUsers = roomUsers;
                targetRoom = room;
            }
        }
        if(targetRoom != null) {
            targetRoom.addUser(name);
        }
    }

    public void joinRoom(String userName, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if (!users.contains(userName))
            throw new NoSuchUserException(userName);
        getRoom(roomName).addUser(userName);
    }
    public void registerAndJoin(String userName, String roomName) throws NoSuchRoomException, NoSuchUserException {
        if (users.add(userName)){
            joinRoom(userName, roomName);
        }
    }
    public void leaveRoom(String userName, String roomName) throws NoSuchRoomException, NoSuchUserException {
        if (!users.contains(userName))
            throw new NoSuchUserException(userName);
        getRoom(roomName).removeUser(userName);
    }
    public void followFriend(String userName, String friendUsername) throws NoSuchUserException {
        if (!users.contains(userName)) throw new NoSuchUserException(userName);
        if (!users.contains(friendUsername)) throw new NoSuchUserException(friendUsername);

        for (ChatRoom room : rooms.values()) {
            if (room.hasUser(friendUsername)) {
                room.addUser(userName);
            }
        }
    }
}

public class ChatSystemTest {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr.addUser(jin.next());
                if ( k == 1 ) cr.removeUser(jin.next());
                if ( k == 2 ) System.out.println(cr.hasUser(jin.next()));
            }
            System.out.println("");
            System.out.println(cr.toString());
            n = jin.nextInt();
            if ( n == 0 ) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr2.addUser(jin.next());
                if ( k == 1 ) cr2.removeUser(jin.next());
                if ( k == 2 ) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if ( k == 1 ) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while ( true ) {
                String cmd = jin.next();
                if ( cmd.equals("stop") ) break;
                if ( cmd.equals("print") ) {
                    System.out.println(cs.getRoom(jin.next())+"\n");continue;
                }
                for ( Method m : mts ) {
                    if ( m.getName().equals(cmd) ) {
                        String params[] = new String[m.getParameterTypes().length];
                        for ( int i = 0 ; i < params.length ; ++i ) params[i] = jin.next();
                        m.invoke(cs, (Object[])params);
                    }
                }
            }
        }
    }

}
