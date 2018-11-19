package model;

public class Photo {
    private String id;
    private String s;
    private String ourl;    //原图
    private String ow;
    private String oh;
    private String murl;    //打开的图片
    private String surl;
    private String turl;
    private String qurl;
    private String desc;
    private String t;
    private String kw;
    private String picsetids;
    private String t1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getOurl() {
        return ourl;
    }

    public void setOurl(String ourl) {
        this.ourl = ourl;
    }

    public String getOw() {
        return ow;
    }

    public void setOw(String ow) {
        this.ow = ow;
    }

    public String getOh() {
        return oh;
    }

    public void setOh(String oh) {
        this.oh = oh;
    }

    public String getMurl() {
        return murl;
    }

    public void setMurl(String murl) {
        this.murl = murl;
    }

    public String getSurl() {
        return surl;
    }

    public void setSurl(String surl) {
        this.surl = surl;
    }

    public String getTurl() {
        return turl;
    }

    public void setTurl(String turl) {
        this.turl = turl;
    }

    public String getQurl() {
        return qurl;
    }

    public void setQurl(String qurl) {
        this.qurl = qurl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }

    public String getPicsetids() {
        return picsetids;
    }

    public void setPicsetids(String picsetids) {
        this.picsetids = picsetids;
    }

    public String getT1() {
        return t1;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", s='" + s + '\'' +
                ", ourl='" + ourl + '\'' +
                ", ow='" + ow + '\'' +
                ", oh='" + oh + '\'' +
                ", murl='" + murl + '\'' +
                ", surl='" + surl + '\'' +
                ", turl='" + turl + '\'' +
                ", qurl='" + qurl + '\'' +
                ", desc='" + desc + '\'' +
                ", t='" + t + '\'' +
                ", kw='" + kw + '\'' +
                ", picsetids='" + picsetids + '\'' +
                ", t1='" + t1 + '\'' +
                '}';
    }
}
