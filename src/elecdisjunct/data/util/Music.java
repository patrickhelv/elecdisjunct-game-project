package elecdisjunct.data.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

/**
 * Wrapper class for JavaFX MediaPlayer.
 *
 * @author Victoria Blichfeldt
 */

public class Music {
    private static MediaPlayer mediaPlayer;

    public static void playMusic(){
        String musicFile = "resources/Loopster.wav";
        Media sound = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.seek(Duration.INDEFINITE);
        mediaPlayer.play();
        System.out.println("Music");

    }

    public static void muteMusic(){
        if (mediaPlayer != null) mediaPlayer.setMute(true);
    }

    public static void unMuteMusic(){
        if (mediaPlayer != null) mediaPlayer.setMute(false);
    }

    public static void setVolume(double volume){
        if (mediaPlayer != null) mediaPlayer.setVolume(volume);
    }
}
