package com.antaresnav.navigation.demo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "style_url")
public class StyleUrl implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String name;
    @NonNull
    private String url;

    public StyleUrl(@NonNull String name, @NonNull String url) {
        this.name = name;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "StyleUrl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StyleUrl styleUrl = (StyleUrl) o;

        if (id != styleUrl.id) return false;
        if (!name.equals(styleUrl.name)) return false;
        return url.equals(styleUrl.url);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
    }

    protected StyleUrl(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.url = in.readString();
    }

    public static final Creator<StyleUrl> CREATOR = new Creator<StyleUrl>() {
        @Override
        public StyleUrl createFromParcel(Parcel source) {
            return new StyleUrl(source);
        }

        @Override
        public StyleUrl[] newArray(int size) {
            return new StyleUrl[size];
        }
    };
}
