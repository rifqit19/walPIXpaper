package com.rifqi3g.wallpixpaper.wallpaper.helper;

import com.rifqi3g.wallpixpaper.wallpaper.Saved.SaveObj;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {


    Realm realm;

    public RealmHelper(Realm realm){

        this.realm = realm;
    }

    public void save(final SaveObj saveObj){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SaveObj model = realm.copyToRealm(saveObj);
//                if (realm != null){
//                    Log.e("Created", "Database was created");
//                    Number currentIdNum = realm.where(FavObj.class).max("id");
//                    int nextId;
//                    if (currentIdNum == null){
//                        nextId = 1;
//                    }else {
//                        nextId = currentIdNum.intValue() + 1;
//                    }
//                    FavObj model = realm.copyToRealm(favObj);
//                }else{
//                    Log.e("ppppp", "execute: Database not Exist");
//                }
            }
        });
    }
    public List<SaveObj> getFav(){
        RealmResults<SaveObj> results = realm.where(SaveObj.class).findAll();
        return results;
    }
    public void delete(Integer id){
        final RealmResults<SaveObj> model = realm.where(SaveObj.class).equalTo("id", id).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.deleteFromRealm(0);
            }
        });
    }
    public void deleteAll(){
        final RealmResults<SaveObj> model = realm.where(SaveObj.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.deleteAllFromRealm();

            }
        });
    }

}
