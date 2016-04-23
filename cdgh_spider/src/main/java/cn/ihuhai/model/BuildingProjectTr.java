package cn.ihuhai.model;

import java.util.List;

/**
 * Created by huhai on 2016/4/21.
 */
public class BuildingProjectTr {
    private String id;
    private String pageUrl;
    private String date;
    private String detailUrl;
    private String projectName;
    private List<String> imageUrls;

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BuildingProjectTr{" +
                "id='" + id + '\'' +
                ", pageUrl='" + pageUrl + '\'' +
                ", date='" + date + '\'' +
                ", detailUrl='" + detailUrl + '\'' +
                ", projectName='" + projectName + '\'' +
                ", imageUrls=" + imageUrls +
                '}';
    }
}
