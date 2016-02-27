package com.scavenger.jk;

import android.os.*;

/**
 * Created by JunKiat on 10/11/2015.
 */
public class Event implements Parcelable{
    private String name;
    private boolean attend;
    public Event(String a){
        name =a;
        attend = false;
    }
    public Event(Parcel a){
        name =a.readString();
        if(a.readInt()==0){
            attend = false;
        }else{
            attend = true;
        }
    }
    public void toggleAttend(){
        if(attend){
            attend = false;
        } else{
            attend = true;
        }
    }
    public String getName(){
        return name;
    }
    public boolean attending(){
        return attend;
    }
    public int describeContents(){
        return 0;
    }
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    public void writeToParcel(Parcel a, int b){
        a.writeString(name);
        if(attend){
            a.writeInt(1);
        } else{
            a.writeInt(0);
        }
    }
}
