package lochai.web_ban_sach.controller;

import jakarta.servlet.http.HttpServletRequest;
import lochai.web_ban_sach.Dto.PaymentResDto;
import lochai.web_ban_sach.Dto.TransactionStatusDto;
import lochai.web_ban_sach.config.VNPay.VNPayConfig;
import lochai.web_ban_sach.dao.CartRepository;
import lochai.web_ban_sach.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class ThanhToanController {

    @Autowired
    CartRepository cartRepository;


    @PostMapping("")
    public ResponseEntity<?> payment(@RequestBody List<Integer> cartItemIds, HttpServletRequest req) throws UnsupportedEncodingException {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Danh sách sản phẩm không hợp lệ!");
        }

        List<Cart> carts = cartRepository.findAllById(cartItemIds);

        if (carts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy sản phẩm trong giỏ hàng!");
        }



        // Tính tổng tiền
        long totalAmount = 0;
        for (Cart cart : carts) {
            totalAmount += cart.getPrice();
        }
        totalAmount *= 100;

        return createPayment(req, totalAmount); // Truyền tổng tiền vào hàm createPayment
    }



    @GetMapping("/create/payment")
    public ResponseEntity<?> createPayment(HttpServletRequest req, long totalAmount) throws UnsupportedEncodingException {

        String orderType = "other";
        //long amount = Integer.parseInt(req.getParameter("totalAmount"))*100;
        //String bankCode = req.getParameter("bankCode");

        long amount = totalAmount*100;

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_Locale", "vn");



        vnp_Params.put("vnp_OrderType", orderType);


        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        PaymentResDto paymentResDto = new PaymentResDto();
        paymentResDto.setStatus("OKeee");
        paymentResDto.setMessage("Thành công");
        paymentResDto.setURL(paymentUrl);

        return ResponseEntity.status(HttpStatus.OK).body(paymentResDto);
    }

    @GetMapping("/payment/info")
    public ResponseEntity<?> transaction(@RequestParam(value = "vnp_Amount") String amount,
                                         @RequestParam(value = "vnp_BankCode") String bankCode,
                                         @RequestParam(value = "vnp_OrderInfo") String order,
                                         @RequestParam(value = "vnp_ResponseCode") String responseCode

    ){
        TransactionStatusDto transactionStatusDto = new TransactionStatusDto();
        if(responseCode.equals("00")){
            transactionStatusDto.setStatus("okkkkkkk");
            transactionStatusDto.setMessage("Thành công");
            transactionStatusDto.setData("");



        }else {
            transactionStatusDto.setStatus("No");
            transactionStatusDto.setMessage("Failed");
            transactionStatusDto.setData("");
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionStatusDto);
    }
}