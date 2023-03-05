package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        //artist.setLikes(0);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist1 = null;

        for(Artist artist:artists){
            if(artist.getName()==artistName){
                artist1=artist;
                break;
            }
        }
        if(artist1==null){
            artist1 = createArtist(artistName);

            Album album = new Album();

            album.setTitle(title);
            album.setReleaseDate(new Date());

            albums.add(album);

            List<Album> list = new ArrayList<>();
            list.add(album);
            artistAlbumMap.put(artist1,list);
            return album;
        }else {
            Album album = new Album();
            album.setTitle(title);
            album.setReleaseDate(new Date());
            albums.add(album);

            List<Album> list = artistAlbumMap.get(artist1);
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(album);
            artistAlbumMap.put(artist1,list);
            return album;
        }
    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        Album album = null;
        for(Album album1:albums){
            if(album1.getTitle()==albumName){
                album=album1;
                break;
            }
        }
        if(album==null) {
            throw new Exception("Album does not exist");
        }
        else {
            Song song = new Song(title,length);
            songs.add(song);
            if(albumSongMap.containsKey(album)){
                List<Song> songList = albumSongMap.get(album);
                songList.add(song);
                albumSongMap.put(album,songList);
            }else{
                List<Song> songList = new ArrayList<>();
                songList.add(song);
                albumSongMap.put(album,songList);
            }

            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null) {
            throw new Exception("User does not exist");
        }
        else {
            Playlist playlist = new Playlist(title);
            playlists.add(playlist);

            List<Song> list1 = new ArrayList<>();
            for(Song song:songs){
                if(song.getLength()==length){
                    list1.add(song);
                }
            }
            playlistSongMap.put(playlist,list1);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(playlist,list);

            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> playlists1 = new ArrayList<>();
                playlists1.add(playlist);
                userPlaylistMap.put(user,playlists1);
            }

            return playlist;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> l = new ArrayList<>();
            for(Song song:songs){
                if(songTitles.contains(song.getTitle())){
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist,l);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(playlist,list);

            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> playlists1 = new ArrayList<>();
                playlists1.add(playlist);
                userPlaylistMap.put(user,playlists1);
            }

            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;
        for(User u:users){
            if(u.getMobile()==mobile){
                user=u;
                break;
            }
        }
        if(user==null) {
            throw new Exception("User does not exist");
        }
        Playlist playlist = null;
        for(Playlist p:playlists){
            if(p.getTitle()==playlistTitle){
                playlist=p;
                break;
            }
        }
        if(playlist==null) {
            throw new Exception("Playlist does not exist");
        }
        if(creatorPlaylistMap.containsKey(user))
            return playlist;

        List<User> list = playlistListenerMap.get(playlist);
        for(User user1:list){
            if(user1==user)
                return playlist;
        }
        list.add(user);
        playlistListenerMap.put(playlist,list);

        List<Playlist> playlistList1 = userPlaylistMap.get(user);
        if(playlistList1 == null){
            playlistList1 = new ArrayList<>();
        }
        playlistList1.add(playlist);
        userPlaylistMap.put(user,playlistList1);

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for(User u:users){
            if(u.getMobile()==mobile){
                user=u;
                break;
            }
        }
        if(user==null) {
            throw new Exception("User does not exist");
        }
        Song song = null;
        for(Song s:songs){
            if(s.getTitle()==songTitle){
                song=s;
                break;
            }
        }
        if (song==null) {
            throw new Exception("Song does not exist");
        }
        if(songLikeMap.containsKey(song)){
            List<User> list = songLikeMap.get(song);
            if(list.contains(user)){
                return song;
            }else {
                int count = song.getLikes() + 1;
                song.setLikes(count);
                list.add(user);
                songLikeMap.put(song,list);

                Album album=null;
                for(Album a:albumSongMap.keySet()){
                    List<Song> songList = albumSongMap.get(a);
                    if(songList.contains(song)){
                        album = a;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist artist1:artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(artist1);
                    if (albumList.contains(album)){
                        artist = artist1;
                        break;
                    }
                }
                int like = artist.getLikes() +1;
                artist.setLikes(like);
                artists.add(artist);
                return song;
            }
        }else {
            int count1 = song.getLikes() + 1;
            song.setLikes(count1);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song,list);

            Album album=null;
            for(Album album1:albumSongMap.keySet()){
                List<Song> songlist = albumSongMap.get(album1);
                if(songlist.contains(song)){
                    album = album1;
                    break;
                }
            }
            Artist artist = null;
            for(Artist a:artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(a);
                if (albumList.contains(album)){
                    artist = a;
                    break;
                }
            }
            int count = artist.getLikes() +1;
            artist.setLikes(count);
            artists.add(artist);

            return song;
        }
    }
    public String mostPopularArtist() {
        int max = Integer.MIN_VALUE;
        Artist artist1=null;
        for(Artist artist:artists){
            if(artist.getLikes()>=max){
                artist1=artist;
                max = artist.getLikes();
            }
        }
        return artist1.getName();
    }

    public String mostPopularSong() {
        int max=Integer.MIN_VALUE;
        Song song = null;
        for(Song song1:songLikeMap.keySet()){
            if(song1.getLikes()>=max){
                song=song1;
                max = song1.getLikes();
            }
        }
        return song.getTitle();
    }
}
