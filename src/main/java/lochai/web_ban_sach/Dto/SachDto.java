package lochai.web_ban_sach.Dto;

import lochai.web_ban_sach.entity.TheLoai;
import lombok.Data;

import java.util.List;

@Data
public class SachDto {
    private String tenSach;
    private String tenTacGia;
    private String isbn;
    private String moTa;
    private double giaNiemYet;
    private double giaBan;
    private int soLuong;
    private int maTheLoai;

    private List<HinhAnhDto> hinhAnhs;
    private List<String> theLoaiSach;






}
