# HRM – Bảo vệ API nhân viên bằng Spring Security

Bài tập Spring Boot: tạo API `GET /api/v1/employees` trả về 3 nhân viên cố định và yêu cầu xác thực trước khi truy cập.

## 1. Công nghệ và yêu cầu

- Java 17 trở lên; có thể dùng JDK 17 hoặc 21 trong IntelliJ IDEA.
- Spring Boot 3.5.16, Maven, Spring Web, Spring Security.
- Maven Wrapper đi kèm tải Maven 3.9.9 khi chạy lần đầu; cần Internet để tải Maven và các dependency.
- Dữ liệu giả lập trong bộ nhớ, không cần MySQL và không cần Lombok.

## 2. Các file chính

| File | Vai trò |
| --- | --- |
| `src/main/java/com/example/hrm/HrmEmployeeSecurityApplication.java` | Điểm chạy ứng dụng Spring Boot |
| `src/main/java/com/example/hrm/model/Employee.java` | Model gồm `Long id`, `String fullName`, `BigDecimal salary` |
| `src/main/java/com/example/hrm/controller/EmployeeController.java` | API GET trả về danh sách 3 nhân viên cố định |
| `src/main/resources/application.properties` | Tên ứng dụng và cổng 8080 |
| `src/test/java/com/example/hrm/EmployeeSecurityIntegrationTest.java` | Kiểm tra xác thực và dữ liệu trả về |
| `postman/HRM_Employee_Security.postman_collection.json` | Các request mẫu kèm kiểm tra trong Postman |

`salary` dùng `BigDecimal` để biểu diễn tiền lương chính xác. Các số tiền mẫu trong bài tính theo VND.

## 3. Dependency bảo mật

Trong `pom.xml` đã thêm:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Khi có dependency này, Spring Boot tự cấu hình bảo vệ các endpoint, hỗ trợ HTTP Basic và trang đăng nhập. Bài tập sử dụng cấu hình mặc định để quan sát mật khẩu tự sinh trong Console; không cần tạo `SecurityConfig`.

## 4. Chạy ứng dụng

### Cách 1: IntelliJ IDEA

1. Giải nén project, chọn **Open** và mở thư mục `hrm-employee-security` có `pom.xml`.
2. Chọn JDK 17 hoặc 21 trong **File → Project Structure → Project SDK**.
3. Chờ Maven tải dependency. Nếu cần, mở cửa sổ Maven và chọn **Reload All Maven Projects**.
4. Mở `HrmEmployeeSecurityApplication.java` và bấm **Run** ở hàm `main`.
5. Giữ ứng dụng chạy trong lúc gọi Postman.

### Cách 2: Terminal

Mở Terminal ngay trong thư mục có `pom.xml`.

macOS / Linux:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Nếu đã cài Maven, có thể dùng `mvn spring-boot:run`.

## 5. Xác thực theo hai bước

### Bước 1: Lấy mật khẩu trong Console

Tìm dòng:

```text
Using generated security password: <MAT_KHAU_TU_SINH>
```

- Username mặc định: **`user`** (chữ thường).
- Password: sao chép **phần mật khẩu phía sau dấu hai chấm**, không sao chép cả dòng.
- Mỗi lần dừng và chạy lại ứng dụng, mật khẩu sẽ thay đổi. Cập nhật mật khẩu mới trong Postman.
- Project không đặt `spring.security.user.password`, nên vẫn sinh mật khẩu tự động đúng yêu cầu đề bài.

### Bước 2: Gửi Basic Auth trong Postman

1. Tạo request với method **GET** và URL:

   ```text
   http://localhost:8080/api/v1/employees
   ```

2. Tab **Authorization** → chọn **Basic Auth**.
3. Nhập **Username:** `user`.
4. Nhập **Password:** mật khẩu mới nhất vừa sao chép trong Console.
5. Tab **Headers**, thêm `Accept` với giá trị `application/json`.
6. Bấm **Send** → nhận **200 OK** và danh sách nhân viên.

Request GET này không cần Body.

### JSON trả về khi đăng nhập đúng

```json
[
  {
    "id": 1,
    "fullName": "Nguyễn Văn An",
    "salary": 18000000
  },
  {
    "id": 2,
    "fullName": "Trần Thị Bình",
    "salary": 22000000
  },
  {
    "id": 3,
    "fullName": "Lê Văn Cường",
    "salary": 25000000
  }
]
```

## 6. Dùng Postman Collection có sẵn

1. Trong Postman chọn **Import**, mở `postman/HRM_Employee_Security.postman_collection.json`.
2. Mở collection → tab **Variables**.
3. Giữ `baseUrl` là `http://localhost:8080`.
4. Điền mật khẩu trong Console vào giá trị sử dụng của biến `securityPassword` (Current Value / Local Value, tùy phiên bản Postman).
5. Lần lượt gửi các request:

| Request | Kết quả mong đợi |
| --- | --- |
| `01 - Chưa xác thực` | `401 Unauthorized`, không có danh sách nhân viên |
| `02 - Sai mật khẩu` | `401 Unauthorized`, không có danh sách nhân viên |
| `03 - Đúng tài khoản mặc định` | `200 OK`, JSON có đúng 3 nhân viên |

Collection đã có các kiểm tra status và dữ liệu trong phần scripts sau khi nhận response. Biến `securityPassword` được để trống trong file nộp bài; chỉ điền mật khẩu tại máy đang chạy ứng dụng.

Để thử request chưa xác thực chính xác, tắt **cookie jar** cho request hoặc xóa cookie `JSESSIONID` của `localhost` nếu trước đó đã đăng nhập qua session. Các request trong collection đã bật tùy chọn tắt cookie jar.

## 7. Gọi bằng cURL

Chưa xác thực:

```bash
curl -i -H 'Accept: application/json' \
  http://localhost:8080/api/v1/employees
```

Kết quả mong đợi: `401 Unauthorized`.

Đăng nhập đúng; cURL sẽ hỏi mật khẩu, nhập mật khẩu lấy từ Console:

```bash
curl -i -u user -H 'Accept: application/json' \
  http://localhost:8080/api/v1/employees
```

Kết quả mong đợi: `200 OK` và JSON có 3 nhân viên.

Khi mở URL bằng trình duyệt, cấu hình mặc định có thể chuyển hướng đến trang `/login` do trình duyệt yêu cầu HTML. Đăng nhập bằng `user` và mật khẩu trong Console để xem danh sách. Với API/Postman, dùng `Accept: application/json` để quan sát mã 401 khi chưa xác thực.

## 8. Kiểm tra tự động và đóng gói

macOS / Linux:

```bash
./mvnw clean verify
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean verify
```

Bốn ca kiểm tra sử dụng Spring Security thật trong context Spring Boot:

- Không có thông tin xác thực → 401 và không lộ danh sách.
- Sai mật khẩu → 401 và không lộ danh sách.
- Sai username → 401.
- Username `user` và mật khẩu do Spring Boot sinh → 200, đúng 3 nhân viên và các trường dữ liệu.

Các test đọc mật khẩu tự sinh từ `SecurityProperties`, không thay thế người dùng bằng `@WithMockUser`.

Kết quả chạy build, tests và gọi HTTP trực tiếp được ghi trong [docs/KET_QUA_KIEM_THU.md](docs/KET_QUA_KIEM_THU.md).

Sau khi build, có thể chạy:

```bash
java -jar target/hrm-employee-security-0.0.1-SNAPSHOT.jar
```

## 9. Giải thích luồng xử lý

1. Client gửi GET đến `/api/v1/employees`.
2. Spring Security kiểm tra thông tin xác thực trước khi cho request đến Controller.
3. Nếu thiếu hoặc sai thông tin, request API nhận 401.
4. Nếu đúng, `EmployeeController.getAllEmployees()` trả về danh sách cố định.
5. `@RestController` để Spring chuyển danh sách thành JSON.

## 10. Nộp bài lên GitHub

Tạo một repository trống trên GitHub, sau đó chạy các lệnh trong thư mục project. Thay URL mẫu bằng URL repository của mày:

```bash
git init
git add .
git commit -m "Add employee API with default Spring Security"
git branch -M main
git remote add origin https://github.com/TEN_GITHUB/hrm-employee-security.git
git push -u origin main
```

Nộp source code đã giải nén, gồm `src`, `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn`, README và collection Postman. `.gitignore` đã loại thư mục `target`, file IDE và log. Giữ biến mật khẩu trống khi xuất lại collection để nộp.

## 11. Lỗi thường gặp

| Hiện tượng | Cách xử lý |
| --- | --- |
| `401 Unauthorized` | Kiểm tra Basic Auth, username `user`, mật khẩu mới nhất, và bỏ khoảng trắng thừa khi dán |
| Trình duyệt chuyển đến trang đăng nhập | Hành vi mặc định; đăng nhập hoặc thử bằng Postman với `Accept: application/json` |
| Connection refused | Chạy ứng dụng và kiểm tra đúng cổng 8080 |
| Port 8080 already in use | Dừng ứng dụng đang chiếm cổng hoặc đổi `server.port`; cập nhật URL trong Postman tương ứng |
| `Permission denied: ./mvnw` | Chạy `chmod +x mvnw` |
| Lỗi Java / JAVA_HOME | Chọn JDK 17 hoặc 21 và cấu hình JAVA_HOME trỏ đến thư mục JDK |
| Maven chưa tải được dependency | Kiểm tra Internet và Reload Maven |

Tài liệu chính thức: [Spring Boot 3.5 – Spring Security](https://docs.spring.io/spring-boot/3.5/reference/web/spring-security.html).
