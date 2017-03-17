package com.example.vov4ik.musicplayer.data.local.realm;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.vov4ik.musicplayer.data.model.Constants;
import com.example.vov4ik.musicplayer.data.model.MusicFile;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import io.realm.Realm;
import io.realm.RealmCollection;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Volodymyr Oleshkevych on 3/9/2017.
 * Copyright (c) 2017, Rolique. All rights reserved.
 */
public class DbHelperRealm {

    private static DbHelperRealm sDbHelperRealm;

    public static DbHelperRealm getInstants(Context context) {
        if (sDbHelperRealm == null) {
            sDbHelperRealm = new DbHelperRealm(context);
        }
        return sDbHelperRealm;
    }

    private Context mContext;

    private DbHelperRealm(Context context) {
        Realm.init(context);
    }

    public void update(@NonNull final List<MusicFile> musicFiles) {
//        try {
        Flowable.fromCallable(
                new Callable<Realm>() {
                    @Override
                    public Realm call() throws Exception {
                        RealmConfiguration realmConfiguration = new RealmConfiguration
                                .Builder()
                                .deleteRealmIfMigrationNeeded()
                                .build();
                        Realm realm = Realm.getInstance(realmConfiguration);
                        if(realm.isClosed()){
                            Log.i(Constants.LOG_TAG, "CLOSED");
                            return realm;
                        }
                        realm.beginTransaction();
                        if(!realm.isEmpty())
                            realm.delete(MusicFile.class);
                        if(musicFiles.size() > 0)
                        for (Integer i = 0; i < musicFiles.size(); i++) {
                            MusicFile realmObject = realm.createObject(MusicFile.class, i);
                            realmObject.setAlbum(musicFiles.get(i).getAlbum());
                            realmObject.setArtist(musicFiles.get(i).getArtist());
                            realmObject.setFolder(musicFiles.get(i).getFolder());
                            realmObject.setMainFolder(musicFiles.get(i).getMainFolder());
                            realmObject.setPath(musicFiles.get(i).getPath());
                            realmObject.setTitle(musicFiles.get(i).getTitle());
                            realmObject.setTrackNumber(musicFiles.get(i).getTrackNumber());
                        }
                        realm.commitTransaction();
                        realm.close();
                        return realm;
                    }
                })
                .subscribeWith(new DisposableSubscriber<Realm>() {
                                     @Override
                                     public void onNext(Realm realm) {
                                         Log.i(Constants.LOG_TAG, "NEXT put");
                                         realm.close();

                                     }

                                     @Override
                                     public void onError(Throwable throwable) {
                                         Log.i(Constants.LOG_TAG, "Error put");
                                         Log.i(Constants.LOG_TAG, throwable.toString());
                                     }

                                     @Override
                                     public void onComplete() {

                                     }
                                 }

        );
    }

    public List<MusicFile> getAllMusicFiles() {
        final List<MusicFile> musicFiles = new ArrayList<>();
        Flowable.fromCallable(
                new Callable<List<MusicFile>>() {
                    @Override
                    public List<MusicFile> call() throws Exception {
                        RealmConfiguration realmConfiguration = new RealmConfiguration
                                .Builder()
                                .deleteRealmIfMigrationNeeded()
                                .build();
                        Realm realm = Realm.getInstance(realmConfiguration);
                        if(!realm.isEmpty())
                            for(MusicFile musicFile: realm.where(MusicFile.class).findAll()){
                                MusicFile m = new MusicFile(
                                        musicFile.getId(),
                                        musicFile.getTitle(),
                                        musicFile.getPath(),
                                        musicFile.getAlbum(),
                                        musicFile.getArtist(),
                                        musicFile.getFolder(),
                                        musicFile.getMainFolder(),
                                        musicFile.getTrackNumber());
                                musicFiles.add(m);
                            }

                        realm.close();
                        return musicFiles;
                    }
                })
                .subscribeWith(new DisposableSubscriber<List<MusicFile>>() {
                                     @Override
                                     public void onNext(List<MusicFile> files) {
                                         Log.i(Constants.LOG_TAG, "NEXT get");
                                         for(MusicFile musicFile: files) {
                                            if(musicFile.getMainFolder() == null){
                                                Log.e("NULL MAIN", musicFile.toString());
                                            }
                                         }

                                     }

                                     @Override
                                     public void onError(Throwable throwable) {
                                         Log.i(Constants.LOG_TAG, "Error get");
                                         Log.i(Constants.LOG_TAG, throwable.toString());

                                     }

                                     @Override
                                     public void onComplete() {

                                     }
                                 }

        );
        return musicFiles;
    }

}
