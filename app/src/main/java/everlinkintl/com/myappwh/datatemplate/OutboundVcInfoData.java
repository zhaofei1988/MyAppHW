package everlinkintl.com.myappwh.datatemplate;

public class OutboundVcInfoData {
    private String trans_date;
    private String vc_biz_no;
    private String trans_route;
    private String vc_biz_no_desc;
    private String client_id;
    private int veh_sync_status;

    public int getVeh_sync_status() {
        return veh_sync_status;
    }

    public void setVeh_sync_status(int veh_sync_status) {
        this.veh_sync_status = veh_sync_status;
    }

    public String getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(String trans_date) {
        this.trans_date = trans_date;
    }

    public String getVc_biz_no() {
        return vc_biz_no;
    }

    public void setVc_biz_no(String vc_biz_no) {
        this.vc_biz_no = vc_biz_no;
    }

    public String getTrans_route() {
        return trans_route;
    }

    public void setTrans_route(String trans_route) {
        this.trans_route = trans_route;
    }

    public String getVc_biz_no_desc() {
        return vc_biz_no_desc;
    }

    public void setVc_biz_no_desc(String vc_biz_no_desc) {
        this.vc_biz_no_desc = vc_biz_no_desc;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
