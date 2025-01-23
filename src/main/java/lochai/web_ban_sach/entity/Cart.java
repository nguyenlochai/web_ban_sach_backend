package lochai.web_ban_sach.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "cart")
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cart")
    private int maCart;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "tong_gia")
    private double totalPrice;

    @Column(name = "gia")
    private Double price;


//    @Column(name = "gia")
//    private double price;
//
//    @Column(name = "ten_Sach")
//    private String tenSach;


    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinTable(
            name = "sach_cart",
            joinColumns = @JoinColumn(name="ma_cart"),
            inverseJoinColumns = @JoinColumn(name = "ma_sach")
    )
    private List<Sach> danhSachSach;







}
