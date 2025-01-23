package lochai.web_ban_sach.entity;


public class ThongBao extends Throwable {

    private String noiDung;

    public ThongBao(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}
