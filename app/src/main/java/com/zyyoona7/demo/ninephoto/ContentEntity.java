package com.zyyoona7.demo.ninephoto;

import java.io.Serializable;
import java.util.List;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/9/7.
 */
public class ContentEntity implements Serializable {

    private String mAvatar;
    private String mNickName;
    private String mContent;
    private List<String> mPhotoList;

    public ContentEntity() {
    }

    public ContentEntity(List<String> photoList) {
        mPhotoList = photoList;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public List<String> getPhotoList() {
        return mPhotoList;
    }

    public void setPhotoList(List<String> photoList) {
        mPhotoList = photoList;
    }
}
