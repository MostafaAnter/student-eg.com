package com.student_eg.student_egcom.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mostafa on 11/03/16.
 */
public class FeedPOJO implements Parcelable {

    private String  id;
    private String title;
    private String timeStamp;
    private String description;
    private String content;
    private String linkAttachedWithContent;
    private String imageUrl;

    public FeedPOJO(String id, String title, String timeStamp,
                    String description, String content, String linkAttachedWithContent,
                    String imageUrl){
        this.id = id;
        this.title = title;
        this.timeStamp = timeStamp;
        this.description = description;
        this.content = content;
        this.linkAttachedWithContent = linkAttachedWithContent;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinkAttachedWithContent() {
        return linkAttachedWithContent;
    }

    public void setLinkAttachedWithContent(String linkAttachedWithContent) {
        this.linkAttachedWithContent = linkAttachedWithContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    protected FeedPOJO(Parcel in) {
        id = in.readString();
        title = in.readString();
        timeStamp = in.readString();
        description = in.readString();
        content = in.readString();
        linkAttachedWithContent = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(timeStamp);
        dest.writeString(description);
        dest.writeString(content);
        dest.writeString(linkAttachedWithContent);
        dest.writeString(imageUrl);
    }

    @SuppressWarnings("unused")
    public static final Creator<FeedPOJO> CREATOR = new Creator<FeedPOJO>() {
        @Override
        public FeedPOJO createFromParcel(Parcel in) {
            return new FeedPOJO(in);
        }

        @Override
        public FeedPOJO[] newArray(int size) {
            return new FeedPOJO[size];
        }
    };
}
