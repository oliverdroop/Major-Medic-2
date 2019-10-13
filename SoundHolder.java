package client;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundHolder {
    public SoundHolder(Game game) {
        super();
        this.Game = game;
    }
    Game Game;
    List<Clip> ClipsPlaying = new ArrayList<Clip>();
    //AudioInputStream[] Streams = new AudioInputStream[1];
    //Clip[] Clips = new Clip[1];

    public void Play(String ref) {
        try {
            String path = this.Game.getRouteDirectory() + "Sounds\\" + ref + ".wav";
            File file = new File(path);
            AudioInputStream strm = AudioSystem.getAudioInputStream(file);
            Clip c = AudioSystem.getClip();
            c.open(strm);
            c.start();
            this.ClipsPlaying.add(c);
            //while(c.isActive()){
            //    
            //}
            //this.ClipsPlaying.remove(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void StopAll(){
        for(Clip c: this.getClipsPlaying()){
            if (c.isActive()){
                c.stop();
                c.close();
                //this.getClipsPlaying().remove(c);
            }
        }
    }
    public void Stop(Clip c){
        if (c.isActive()){
            c.stop();
            c.close();
            this.getClipsPlaying().remove(c);
        }
    }
    public List<Clip> getClipsPlaying(){
        return this.ClipsPlaying;
    }
}
