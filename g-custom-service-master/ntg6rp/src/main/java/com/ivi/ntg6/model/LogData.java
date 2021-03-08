package com.ivi.ntg6.model;


/**
 * [  LOG TABLE INSERT DATA   ]
 * 로그 테이블 데이터
 * <p>
 * GCID	 ::::	관리번호UUID
 * MID      ::::   몽고DB ID
 * GC_TID	 ::::	GC트랜잭션아이디
 * SKEY	 ::::	서비스키
 * CIP	 	 ::::	클라이언트IP
 * GCSVC	 ::::	GC서비스명
 * GCREQ	 ::::	GC요청시간
 * GCRES	 ::::	GC응답시간
 * GCDELAY	 ::::	GC지체시간
 * GM_TID	 ::::	GM트랜잭션아이디
 * GMREQ	 ::::	GM요청시간
 * GMRES	 ::::	GM응답시간
 * GMDELAY	 ::::	GM지체시간
 * GMSTAT	 ::::	GM상태값
 * REQST	 ::::	REQ시작점
 * REQDT	 ::::	REQ도착점
 * REQWPCNT	 ::::	REQ경유지갯수
 * REQAVIDCNT	 ::::	REQ회피구간갯수
 * RES_TDIS	 ::::	RES총거리
 * RES_TTOL	 ::::	RES총톨비
 * RES_TDUR	 ::::	RES총시간
 * RES_TRDUR	 ::::	RES총리얼시간
 * GCERCODE	 ::::	GC에러코드
 * GCTRYCNT	 ::::	GC시도횟수
 *
 * @author user
 */
public class LogData {


    public long maxElapsedTime; // GeoMaster 최대 소요시간
    public long avgElapsedTime; // GeoMaster 평균 소요시간
    public long minElapsedTime; // GeoMaster 최소 소요시간

    String GCID;  //= "관리번호UUID";
    String MID;
    String SKEY;  //= "서비스키";
    String CIP;  // = // "클라이언트IP";

    String GC_TID;  // = "GC트랜잭션아이디";
    String GCSVC;  // = "NTG6";
    String GCREQ;  // = "GC요청시간";
    String GCRES;  // = "GC응답시간";
    Long GCDELAY;  // = "GC지체시간";

    String GM_TID;  // =  ; //"GM트랜잭션아이디";
    String GMREQ;  // = "GM요청시간";
    String GMRES;  // =  ; // "GM응답시간";
    Long GMDELAY;  // = "GM지체시간";
    String GMSTAT;  // = "200"; // 기본 200 / 에러시 변경 "GM상태값";


    String REQST;  // =  ; // "REQ시작점";
    String REQDT;  // =  ; // "REQ도착점";

    String REQWPCNT;  // =  "REQ경유지갯수";

    String REQAVIDCNT;  // =  ;// "REQ회피구간갯수";

    String GCERCODE;  // = ""; // "GC에러코드";
    int GCTRYCNT;  // = "0"; // "GC시도횟수";

    //new 2020-02-17
    String GCKEY;
    String GMKEY;
    String REQAVCNT;

    Double RES_TDIS;
    Double RES_TTOL;
    Double RES_TDUR;
    Double RES_TRDUR;

    int RES_CNT;
    String RES_ROUTE_TYPE;


    public String getGCID() {
        return GCID;
    }

    public void setGCID(String gCID) {
        GCID = gCID;
    }

    public String getSKEY() {
        return SKEY;
    }

    public void setSKEY(String sKEY) {
        SKEY = sKEY;
    }

    public String getCIP() {
        return CIP;
    }

    public void setCIP(String cIP) {
        CIP = cIP;
    }

    public String getGC_TID() {
        return GC_TID;
    }

    public void setGC_TID(String gC_TID) {
        GC_TID = gC_TID;
    }

    public String getGCSVC() {
        return GCSVC;
    }

    public void setGCSVC(String gCSVC) {
        GCSVC = gCSVC;
    }

    public String getGCREQ() {
        return GCREQ;
    }

    public void setGCREQ(String gCREQ) {
        GCREQ = gCREQ;
    }

    public String getGCRES() {
        return GCRES;
    }

    public void setGCRES(String gCRES) {
        GCRES = gCRES;
    }

    public Long getGCDELAY() {
        return GCDELAY;
    }

    public void setGCDELAY(Long gCDELAY) {
        GCDELAY = gCDELAY;
    }

    public String getGM_TID() {
        return GM_TID;
    }

    public void setGM_TID(String gM_TID) {
        GM_TID = gM_TID;
    }

    public String getGMREQ() {
        return GMREQ;
    }

    public void setGMREQ(String gMREQ) {
        GMREQ = gMREQ;
    }

    public String getGMRES() {
        return GMRES;
    }

    public void setGMRES(String gMRES) {
        GMRES = gMRES;
    }

    public Long getGMDELAY() {
        return GMDELAY;
    }

    public void setGMDELAY(Long gMDELAY) {
        GMDELAY = gMDELAY;
    }

    public String getGMSTAT() {
        return GMSTAT;
    }

    public void setGMSTAT(String gMSTAT) {
        GMSTAT = gMSTAT;
    }

    public String getREQST() {
        return REQST;
    }

    public void setREQST(String rEQST) {
        REQST = rEQST;
    }

    public String getREQDT() {
        return REQDT;
    }

    public void setREQDT(String rEQDT) {
        REQDT = rEQDT;
    }

    public String getREQWPCNT() {
        return REQWPCNT;
    }

    public void setREQWPCNT(String rEQWPCNT) {
        REQWPCNT = rEQWPCNT;
    }

    public String getREQAVIDCNT() {
        return REQAVIDCNT;
    }

    public void setREQAVIDCNT(String rEQAVIDCNT) {
        REQAVIDCNT = rEQAVIDCNT;
    }

    public String getGCERCODE() {
        return GCERCODE;
    }

    public void setGCERCODE(String gCERCODE) {
        GCERCODE = gCERCODE;
    }

    public int getGCTRYCNT() {
        return GCTRYCNT;
    }

    public void setGCTRYCNT(int gCTRYCNT) {
        GCTRYCNT = gCTRYCNT;
    }

    public String getGCKEY() {
        return GCKEY;
    }

    public void setGCKEY(String gCKEY) {
        GCKEY = gCKEY;
    }

    public String getGMKEY() {
        return GMKEY;
    }

    public void setGMKEY(String gMKEY) {
        GMKEY = gMKEY;
    }

    public String getREQAVCNT() {
        return REQAVCNT;
    }

    public void setREQAVCNT(String rEQAVCNT) {
        REQAVCNT = rEQAVCNT;
    }

    public int getRES_CNT() {
        return RES_CNT;
    }

    public void setRES_CNT(int rES_CNT) {
        RES_CNT = rES_CNT;
    }

    public String getRES_ROUTE_TYPE() {
        return RES_ROUTE_TYPE;
    }

    public void setRES_ROUTE_TYPE(String rES_ROUTE_TYPE) {
        RES_ROUTE_TYPE = rES_ROUTE_TYPE;
    }

    public String getMID() {
        return MID;
    }

    public void setMID(String mID) {
        MID = mID;
    }


    public Double getRES_TDIS() {
        return RES_TDIS;
    }

    public void setRES_TDIS(Double rES_TDIS) {
        RES_TDIS = rES_TDIS;
    }

    public Double getRES_TTOL() {
        return RES_TTOL;
    }

    public void setRES_TTOL(Double rES_TTOL) {
        RES_TTOL = rES_TTOL;
    }

    public Double getRES_TDUR() {
        return RES_TDUR;
    }

    public void setRES_TDUR(Double rES_TDUR) {
        RES_TDUR = rES_TDUR;
    }

    public Double getRES_TRDUR() {
        return RES_TRDUR;
    }

    public void setRES_TRDUR(Double rES_TRDUR) {
        RES_TRDUR = rES_TRDUR;
    }

    @Override
    public String toString() {
        return "LogData [GCID=" + GCID + ", MID=" + MID + ", SKEY=" + SKEY + ", CIP=" + CIP + ", GC_TID=" + GC_TID
                + ", GCSVC=" + GCSVC + ", GCREQ=" + GCREQ + ", GCRES=" + GCRES + ", GCDELAY=" + GCDELAY + ", GM_TID="
                + GM_TID + ", GMREQ=" + GMREQ + ", GMRES=" + GMRES + ", GMDELAY=" + GMDELAY + ", GMSTAT=" + GMSTAT
                + ", REQST=" + REQST + ", REQDT=" + REQDT + ", REQWPCNT=" + REQWPCNT + ", REQAVIDCNT=" + REQAVIDCNT
                + ", GCERCODE=" + GCERCODE + ", GCTRYCNT=" + GCTRYCNT + ", GCKEY=" + GCKEY + ", GMKEY=" + GMKEY
                + ", REQAVCNT=" + REQAVCNT + ", RES_TDIS=" + RES_TDIS + ", RES_TTOL=" + RES_TTOL + ", RES_TDUR="
                + RES_TDUR + ", RES_TRDUR=" + RES_TRDUR + ", RES_CNT=" + RES_CNT + ", RES_ROUTE_TYPE=" + RES_ROUTE_TYPE
                + ", getGCID()=" + getGCID() + ", getSKEY()=" + getSKEY() + ", getCIP()=" + getCIP() + ", getGC_TID()="
                + getGC_TID() + ", getGCSVC()=" + getGCSVC() + ", getGCREQ()=" + getGCREQ() + ", getGCRES()="
                + getGCRES() + ", getGCDELAY()=" + getGCDELAY() + ", getGM_TID()=" + getGM_TID() + ", getGMREQ()="
                + getGMREQ() + ", getGMRES()=" + getGMRES() + ", getGMDELAY()=" + getGMDELAY() + ", getGMSTAT()="
                + getGMSTAT() + ", getREQST()=" + getREQST() + ", getREQDT()=" + getREQDT() + ", getREQWPCNT()="
                + getREQWPCNT() + ", getREQAVIDCNT()=" + getREQAVIDCNT() + ", getGCERCODE()=" + getGCERCODE()
                + ", getGCTRYCNT()=" + getGCTRYCNT() + ", getGCKEY()=" + getGCKEY() + ", getGMKEY()=" + getGMKEY()
                + ", getREQAVCNT()=" + getREQAVCNT() + ", getRES_TDIS()=" + getRES_TDIS() + ", getRES_TTOL()="
                + getRES_TTOL() + ", getRES_TDUR()=" + getRES_TDUR() + ", getRES_TRDUR()=" + getRES_TRDUR()
                + ", getRES_CNT()=" + getRES_CNT() + ", getRES_ROUTE_TYPE()=" + getRES_ROUTE_TYPE() + ", getMID()="
                + getMID() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
                + super.toString() + "]";
    }


}
