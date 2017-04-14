package per.lijuan.meituan.Bean;

import io.realm.RealmObject;

/**
 * Created by admin on 2017/3/22.
 */

public class Command  extends RealmObject {
    private String name;
    private int id;
    private int res;
    private String com;
    private boolean isFolder;
    private int filePosition;
    private boolean swaped;

    public boolean isSwaped() {
        return swaped;
    }

    public void setSwaped(boolean swaped) {
        this.swaped = swaped;
    }

    public int getFilePosition() {
        return filePosition;
    }

    public void setFilePosition(int filePosition) {
        this.filePosition = filePosition;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }
}
