package model;

public class Album {
    private Integer id;
    private String name;
    private String s;
    private String desc;
    private String st;
    private String au;
    private String count;
    private String t;
    private String ut;
    private String cvid;
    private String curl;
    private String surl;
    private String lurl;
    private String dmt;
    private String alc;
    private String kw;
    private String purl;    //相册里图片js

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getAu() {
        return au;
    }

    public void setAu(String au) {
        this.au = au;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getUt() {
        return ut;
    }

    public void setUt(String ut) {
        this.ut = ut;
    }

    public String getCvid() {
        return cvid;
    }

    public void setCvid(String cvid) {
        this.cvid = cvid;
    }

    public String getCurl() {
        return curl;
    }

    public void setCurl(String curl) {
        this.curl = curl;
    }

    public String getSurl() {
        return surl;
    }

    public void setSurl(String surl) {
        this.surl = surl;
    }

    public String getLurl() {
        return lurl;
    }

    public void setLurl(String lurl) {
        this.lurl = lurl;
    }

    public String getDmt() {
        return dmt;
    }

    public void setDmt(String dmt) {
        this.dmt = dmt;
    }

    public String getAlc() {
        return alc;
    }

    public void setAlc(String alc) {
        this.alc = alc;
    }

    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", s='" + s + '\'' +
                ", desc='" + desc + '\'' +
                ", st='" + st + '\'' +
                ", au='" + au + '\'' +
                ", count='" + count + '\'' +
                ", t='" + t + '\'' +
                ", ut='" + ut + '\'' +
                ", cvid='" + cvid + '\'' +
                ", curl='" + curl + '\'' +
                ", surl='" + surl + '\'' +
                ", lurl='" + lurl + '\'' +
                ", dmt='" + dmt + '\'' +
                ", alc='" + alc + '\'' +
                ", kw='" + kw + '\'' +
                ", purl='" + purl + '\'' +
                '}';
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
