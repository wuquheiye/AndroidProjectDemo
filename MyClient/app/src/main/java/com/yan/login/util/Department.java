package com.yan.login.util;

import java.io.Serializable;

public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer did;

    /**
     * 部门名称
     */
    private String dname;

    /**
     * 创建时间
     */
    private String dcreateTime;

    /**
     * 所属公司
     */
    private int artsVision;

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDcreateTime() {
        return dcreateTime;
    }

    public void setDcreateTime(String dcreateTime) {
        this.dcreateTime = dcreateTime;
    }

    public int getArtsVision() {
        return artsVision;
    }

    public void setArtsVision(int artsVision) {
        this.artsVision = artsVision;
    }

    @Override
    public String toString() {
        return "Department{" +
                "did=" + did +
                ", dname='" + dname + '\'' +
                ", dcreateTime='" + dcreateTime + '\'' +
                ", artsVision=" + artsVision +
                '}';
    }
}
