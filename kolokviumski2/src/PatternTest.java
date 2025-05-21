import java.util.ArrayList;
import java.util.List;

class Song {
    String title;
    String artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title=" + title  +
                ", artist=" + artist  +
                '}';
    }
}

interface State {
    void pressPlay(MP3Player player);

    void pressStop(MP3Player player);

    void pressFWD(MP3Player player);

    void pressREW(MP3Player player);
}

class PlayingState implements vezbaState.State {

    @Override
    public void pressPlay(MP3Player player) {
        System.out.println("Song is already playing");
    }

    @Override
    public void pressStop(MP3Player player) {
        System.out.println("Song " + player.getCurrentSongIndex() + " is paused");
        player.setState(new PausedState());
    }

    @Override
    public void pressFWD(MP3Player player) {
        //pressStop(player);
        System.out.println("Forward...");
        player.nextSong();
        player.setState(new PausedState());
        //System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
    }

    @Override
    public void pressREW(MP3Player player) {

        System.out.println("Reward...");
        player.previousSong();
        player.setState(new PausedState());
        //System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
    }
}


class PausedState implements vezbaState.State {

    @Override
    public void pressPlay(MP3Player player) {
        System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
        player.setState(new PlayingState());
    }

    @Override
    public void pressStop(MP3Player player) {
        System.out.println("Songs are stopped");
        player.currentSongIndex = 0;
        player.setState(new StoppedState());
    }

    @Override
    public void pressFWD(MP3Player player) {
        System.out.println("Forward...");
        player.nextSong();
        //player.setState(new PausedState());
    }

    @Override
    public void pressREW(MP3Player player) {
        System.out.println("Reward...");
        player.previousSong();
        //player.setState(new PausedState());
    }
}

class StoppedState implements vezbaState.State {

    @Override
    public void pressPlay(MP3Player player) {
        System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
        player.setState(new PlayingState());
    }

    @Override
    public void pressStop(MP3Player player) {
        System.out.println("Songs are already stopped");
    }

    @Override
    public void pressFWD(MP3Player player) {
        System.out.println("Forward...");
        player.nextSong();
    }

    @Override
    public void pressREW(MP3Player player) {
        System.out.println("Reward...");
        player.previousSong();
    }
}

class MP3Player {
    List<Song> songs;
    int currentSongIndex;
    vezbaState.State state;

    public MP3Player(List<Song> songs) {
        this.songs = songs;
        currentSongIndex = 0;
        state = new PausedState();
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void pressPlay() {
        state.pressPlay(this);
    }

    public void pressStop() {
        state.pressStop(this);
    }

    public void pressFWD() {
        state.pressFWD(this);
    }

    public void pressREW() {
        state.pressREW(this);
    }

    public void printCurrentSong() {
        //System.out.println("Song{title="+ songs.get(currentSongIndex).title +", artist="+songs.get(currentSongIndex).artist+"}");
        System.out.println(songs.get(currentSongIndex).toString());
    }

    public void setState(vezbaState.State state) {
        this.state = state;
    }

    public void nextSong() {
        currentSongIndex = (currentSongIndex + 1) % songs.size();
    }

    public void previousSong() {
        currentSongIndex = ((currentSongIndex - 1) + songs.size()) % songs.size();
    }

    @Override
    public String toString() {
        return "MP3Player{" +
                "currentSong = " + currentSongIndex +
                ", songList = " + songs + "}";
    }

    //    @Override
//    public String toString() {
//
////        StringBuilder sb = new StringBuilder();
////        sb.append("MP3Player{" + "currentSong = ").append(currentSongIndex).append(", songList = ");
//        //IntStream.range(0, songs.size()).forEach(i->sb.append(songs.get(i).toString()).append(", "));
//
//    }
}

public class PatternTest {
    public static void main(String args[]) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player.toString());
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
    }
}

//Vasiot kod ovde