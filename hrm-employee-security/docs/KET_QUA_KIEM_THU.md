# Kết quả kiểm tra

Thời điểm: 02/11/2026 19:16 JST.

## Build và kiểm tra tự động

- Maven `clean verify`: BUILD SUCCESS.
- 4 integration tests: 4 passed, 0 failures, 0 errors, 0 skipped.
- Dùng cấu hình mặc định và mật khẩu tự sinh của Spring Boot.

## Chạy ứng dụng và gọi HTTP thật

Đã khởi động file JAR trên localhost với cổng tạm để kiểm tra. Cấu hình project khi chạy bình thường vẫn dùng cổng 8080.

| Trường hợp | HTTP status thực tế | Kết quả |
| --- | --- | --- |
| Chưa xác thực | 401 | PASS – không trả danh sách nhân viên |
| Sai mật khẩu | 401 | PASS – không trả danh sách nhân viên |
| Sai username | 401 | PASS – không trả danh sách nhân viên |
| Đúng user và mật khẩu tự sinh trong Console | 200 | PASS – JSON đúng 3 nhân viên |

Mật khẩu kiểm thử không được lưu trong source code hoặc collection Postman. Khi chạy lại ứng dụng, lấy mật khẩu mới từ Console.
