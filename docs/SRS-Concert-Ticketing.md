# SRS — Concert Ticketing

**Software Requirements Specification — Đặc tả yêu cầu phần mềm**  
**Phiên bản:** 1.0 · **Ngày:** 03/11/2026  
**Trạng thái:** Bản đặc tả để review và thiết kế kỹ thuật; các quyết định mở tại mục 13 chưa được coi là đã phê duyệt.  
**Đối tượng sử dụng:** Chủ dự án, BA, kiến trúc sư, backend/frontend developer, QA và vận hành.

## 1. Mục đích, nguồn và nguyên tắc đặc tả

Tài liệu chuyển yêu cầu nghiệp vụ và luồng xử lý thành hành vi phần mềm có thể thiết kế, triển khai và nghiệm thu: ai được thao tác, dữ liệu vào/ra, điều kiện hợp lệ, thay đổi trạng thái, xử lý lỗi và kết quả cần bảo đảm.

### 1.1. Nguồn tham chiếu

| Mã nguồn | Tài liệu | Phiên bản | Phạm vi sử dụng |
|---|---|---|---|
| SRC-01 | BRD-Concert-Ticketing.md | 1.0, 03/11/2026 | Phạm vi, vai trò, BR-01–BR-16, BL-01–BL-23, chỉ số và nghiệm thu |
| SRC-02 | Process-Flows-Concert-Ticketing.md | 1.0, 03/11/2026 | PF-01–PF-11, trao đổi dữ liệu, ngoại lệ và các điểm chưa chốt |

BRD là căn cứ phạm vi nghiệp vụ. Process Flows diễn giải trình tự; SRS cụ thể hóa hành vi. Nếu phát hiện xung đột, ghi nhận để review theo BRD, không tự thay đổi quy tắc đã chốt. Tài liệu này không sửa hai tài liệu nguồn và không xác nhận lại các API/schema trong tài liệu kiến trúc cũ.

### 1.2. Quy ước đọc

- **FR-xx:** nhóm yêu cầu chức năng; các dòng `.01`, `.02` là yêu cầu riêng có thể truy vết.
- **AC-xx:** tiêu chí nghiệm thu theo tình huống, không phải mã unit test.
- **DR-xx / INT-xx / NFR-xx:** yêu cầu dữ liệu, giao tiếp và phi chức năng.
- **OPEN-xx:** điểm chưa được nguồn chốt; có đề xuất thì chỉ là phương án review, chưa là yêu cầu bắt buộc.
- Tên trạng thái viết hoa là **định danh logic của SRS**, chưa bắt buộc enum, tên bảng, topic hoặc API path cụ thể.
- Những chi tiết kiểm tra, dữ liệu và lỗi bổ sung dưới đây là đặc tả suy ra để thực hiện BRD, không mở rộng tính năng nghiệp vụ. Các lựa chọn làm thay đổi quyền lợi, chính sách hoặc phạm vi đều được tách vào mục 13.

## 2. Tổng quan và phạm vi

### 2.1. Mục tiêu hệ thống

Concert Ticketing là pet project có chọn ghế, giữ chỗ, thanh toán giả lập, phát hành QR và check-in online. Hệ thống phải chứng minh được tính nhất quán và khả năng phục hồi bằng người dùng giả lập, load test và mô phỏng lỗi có thể tái lập. Không dùng doanh thu thật hoặc số người dùng thật làm điều kiện hoàn thành.

### 2.2. Phạm vi MVP

Tất cả BL-01–BL-20 nằm trong bàn giao, bao gồm P1: tài khoản/quyền, quản lý concert và suất diễn, tìm kiếm, sơ đồ ghế, giữ chỗ/đơn, thanh toán, hoàn tiền bù trừ, vé QR, check-in, email sandbox, hỗ trợ, báo cáo, audit và thực nghiệm vận hành.

Ngoài MVP: thanh toán thật; SMS/push; phòng chờ; nhiều đợt bán; vé đứng; đơn nhiều suất diễn; đổi ghế/chuyển nhượng/bán lại; hoàn từng vé; khách tự trả vé đã xác nhận; hủy/đổi lịch concert; check-in offline/vào lại; funnel, cá nhân hóa và báo cáo streaming. Không bổ sung những chức năng này qua màn hình hỗ trợ hoặc quyền admin.

### 2.3. Ranh giới trách nhiệm

| Thành phần | Trách nhiệm trong SRS | Không được dùng làm căn cứ thay thế |
|---|---|---|
| Booking / Catalog | Concert, suất diễn, ghế cấu hình, giá, lịch bán, công bố | Không lấy trạng thái Search làm quyền được bán |
| Booking / Inventory | Khả dụng ghế, chủ giữ, thời hạn, chuyển ghế sang đã bán | Countdown trình duyệt không quyết định hết hạn |
| Booking / Ordering | Chủ đơn, giá chốt, trạng thái đơn, điều phối kết quả mua | Callback trình duyệt không xác nhận đơn |
| Booking / Ticketing | Vé, QR, quyền tải file, sử dụng vé | Email/file không quyết định quyền sở hữu |
| Payment | Thu tiền giả lập, xác minh, hoàn tiền, lịch sử giao dịch | Timeout không là bằng chứng thất bại |
| Notification | Email sandbox và lịch sử gửi | Đã gửi email không là bằng chứng đã bán ghế |
| Search | Bản sao tìm kiếm của nội dung được công bố | Không giữ ghế hoặc tính giá chốt |
| Report | Chỉ số, đối chiếu và mốc dữ liệu | Không sửa trạng thái giao dịch nguồn |

Giữ 5 microservices. Hệ thống danh tính, cổng thanh toán giả lập và lưu trữ file là năng lực hỗ trợ, không là service nghiệp vụ mới. Theo ràng buộc dự án: Java + Spring Boot; Booking/Payment dùng Hexagonal, Notification dùng Hexagonal gọn, Search và Report API dùng Layered, Spark là pipeline riêng. Việc phân bổ Kafka, Temporal, outbox, Redis, ShedLock và cơ chế lưu trữ thuộc thiết kế kỹ thuật; SRS chỉ yêu cầu kết quả đúng và phục hồi được.

## 3. Tác nhân và phân quyền

### 3.1. Ma trận quyền nghiệp vụ

| Chức năng | Khách chưa đăng nhập | Người mua | Ban tổ chức | Nhân viên check-in | Admin |
|---|---|---|---|---|---|
| Tìm/xem concert công bố | Có | Có | Có | Có | Có |
| Tạo checkout/thanh toán/hủy đơn chưa xác nhận | Không | Đơn của mình | Chỉ khi có vai trò người mua | Chỉ khi có vai trò người mua | Không được thao tác thay khách qua quyền hỗ trợ |
| Xem đơn/vé, tải QR/file | Không | Của mình | Tra cứu nghiệp vụ thuộc mình; không mặc định tải QR | Chỉ dữ liệu cần để kiểm vé được giao | Tra cứu hỗ trợ; quyền xem QR cần quyết định riêng |
| Quản lý concert/giá/ghế/lịch bán | Không | Không | Thuộc ban tổ chức của mình | Không | Không mặc định có quyền sửa nội dung thay ban tổ chức |
| Check-in | Không | Không | Chỉ khi được phân công check-in | Suất diễn được giao | Chỉ khi được phân công check-in |
| Tra cứu theo người mua | Không | Không | Dữ liệu thuộc mình | Không | Trong phạm vi hỗ trợ được cấp |
| Retry xử lý hỗ trợ | Không | Không | Không | Không | Có, nếu trạng thái cho phép |
| Báo cáo | Không | Không | Phạm vi quản lý | Không | Chỉ khi được cấp phạm vi phù hợp |
| Gán quyền/phân công; tra audit | Không | Không | Không | Không | Có |

Không suy ra admin có quyền bỏ qua mọi nghiệp vụ. Ma trận thể hiện quyền tối thiểu từ BRD; quyền mở rộng phải được review. Một người có thể dùng chức năng của nhiều vai trò nếu được gán rõ ràng, nhưng mỗi thao tác vẫn kiểm tra phạm vi đối tượng.

### 3.2. Quy tắc chung

Kiểm tra quyền tại hệ thống xử lý mỗi yêu cầu, kể cả truy cập bằng ID trực tiếp, gọi API từ ngoài giao diện, tải file và tác vụ retry. Danh tính/chủ đơn lấy từ phiên xác thực; không tin `buyerId` hoặc `organizerId` tùy ý do client gửi. Từ chối trái phạm vi không làm lộ thông tin ghế, giao dịch hoặc người mua của đối tượng bị truy cập.

## 4. Đặc tả chức năng

### 4.1. FR-01 — Tài khoản và phiên đăng nhập

**Nguồn:** BL-01, PF-01. **Ưu tiên:** P0. **Tác nhân:** Khách/người mua.  
**Đầu vào:** Thông tin đăng ký/đăng nhập hoặc yêu cầu khôi phục theo cấu hình hệ thống danh tính. **Đầu ra:** Kết quả xác thực, phiên hợp lệ hoặc lỗi có thể xử lý.

| ID | Yêu cầu |
|---|---|
| FR-01.01 | Cho phép đăng ký, đăng nhập và khôi phục mật khẩu qua quy trình xác minh; không cho đổi mật khẩu chỉ dựa vào việc biết tên/email tài khoản. |
| FR-01.02 | Khách chưa đăng nhập được duyệt concert và sơ đồ ghế công khai, nhưng không tạo giữ chỗ/đơn hoặc bắt đầu thanh toán. |
| FR-01.03 | Khi phiên không còn hợp lệ, yêu cầu xác thực lại trước thao tác được bảo vệ; đăng nhập lại không gia hạn giữ ghế. |
| FR-01.04 | Xác thực lại xong, khách có thể tra đơn của mình để tiếp tục hoặc xem trạng thái thực tế; không mặc định tạo đơn mới. |
| FR-01.05 | Lỗi đăng ký/đăng nhập/khôi phục phải có phản hồi phù hợp; quy tắc mật khẩu, định danh đăng nhập, xác minh đăng ký và thời hạn phiên thuộc OPEN-01. |

**AC-01:** Khách xem được concert nhưng tạo đơn bị từ chối khi chưa đăng nhập. Người mua đăng nhập lại sau mất phiên thấy đúng đơn cũ và `expiresAt` cũ. Yêu cầu đặt lại mật khẩu thiếu bằng chứng xác minh không thay đổi tài khoản.

### 4.2. FR-02 — Vai trò và phân công

**Nguồn:** BL-02, BR-14/16, PF-01. **Ưu tiên:** P0. **Tác nhân:** Admin.  
**Tiền điều kiện:** Admin đã xác thực và có quyền quản lý phân công. **Đầu vào:** Người dùng, vai trò, ban tổ chức/suất diễn liên quan. **Đầu ra:** Phân công đã lưu hoặc lỗi kiểm tra.

| ID | Yêu cầu |
|---|---|
| FR-02.01 | Admin gán/cập nhật quyền và phạm vi cho ban tổ chức, nhân viên check-in; không chấp nhận tham chiếu người dùng/suất diễn không tồn tại. |
| FR-02.02 | Quyền ban tổ chức phải ràng buộc dữ liệu thuộc ban tổ chức; quyền check-in phải ràng buộc suất diễn được giao. |
| FR-02.03 | Ghi audit thay đổi quyền gồm tác nhân, đối tượng, thời gian, thay đổi và kết quả. |
| FR-02.04 | Khi quyền không còn hiệu lực, hệ thống từ chối thao tác ngoài quyền mới; cơ chế cập nhật cache/token và thời gian có hiệu lực cần thiết kế tại OPEN-01. |

**AC-02:** Nhân viên được giao suất A không check-in suất B. Ban tổ chức A không xem/sửa đơn, concert hoặc báo cáo của B dù biết ID. Chỉ người có quyền admin mới gán phân công.

### 4.3. FR-03 — Quản lý concert và công bố

**Nguồn:** BL-03, BR-14/16, PF-02. **Ưu tiên:** P0. **Tác nhân:** Ban tổ chức.  
**Đầu vào:** Nội dung concert, nghệ sĩ, địa điểm, điều kiện mua, thông tin suất diễn; hành động lưu nháp/công bố/ẩn. **Đầu ra:** Concert và trạng thái hiển thị, hoặc lỗi theo trường.

| ID | Yêu cầu |
|---|---|
| FR-03.01 | Cho phép tạo/sửa bản nháp thuộc ban tổ chức; lưu chủ quản lý từ phạm vi được cấp. |
| FR-03.02 | Khi công bố, kiểm tra đủ dữ liệu để khách hiểu concert, suất diễn, địa điểm và điều kiện mua; cấu hình bán phải thỏa FR-04 trước khi nhận đặt mới. |
| FR-03.03 | Nội dung nháp/ẩn không được cung cấp như nội dung công khai; thay đổi trạng thái phải được chuyển tới Search. |
| FR-03.04 | Ẩn hoặc đóng bán không tự hủy concert, đơn đã xác nhận hoặc vé. Ảnh hưởng tới giữ chỗ đang tồn tại được ghi tại OPEN-02. |
| FR-03.05 | Không dùng chức năng sửa nội dung để thực hiện đổi lịch/hủy concert hoặc thay ghế đã bán; những quy trình đó ngoài MVP. |

**AC-03:** Concert nháp không xuất hiện ở luồng công khai. Công bố thiếu dữ liệu bắt buộc trả lỗi và không nhận đơn với cấu hình không hợp lệ. Người mua vẫn thấy đơn/vé đã xác nhận khi concert bị ẩn.

### 4.4. FR-04 — Suất diễn, sơ đồ ghế, giá và khoảng bán

**Nguồn:** BL-04, BR-04/05/13/16, PF-02. **Ưu tiên:** P0. **Tác nhân:** Ban tổ chức.  
**Đầu vào:** Concert, suất diễn, địa điểm, khu vực, hàng/số ghế, giá, thời điểm mở/đóng bán. **Đầu ra:** Cấu hình hợp lệ và kết quả cập nhật.

| ID | Yêu cầu |
|---|---|
| FR-04.01 | Mỗi suất diễn có sơ đồ ghế định danh riêng và đúng một khoảng mở–đóng bán. Không tạo nhiều đợt với giá/hạn mức riêng. |
| FR-04.02 | Kiểm tra thời điểm mở trước đóng, giá không âm và không có ghế định danh trùng trong cùng suất diễn. Chính sách giá 0 và mốc bán so với giờ diễn tại OPEN-05. |
| FR-04.03 | Giá dùng VND; phí riêng bằng 0. Không nhận giá/tổng tiền client làm giá chuẩn. |
| FR-04.04 | Sửa giá chỉ áp dụng đơn mới; giá snapshot của đơn giữ hợp lệ/đã xác nhận không đổi. |
| FR-04.05 | Không sửa/xóa cấu hình ghế đang giữ hợp lệ hoặc đã bán làm thay đổi quyền lợi đơn/vé; đánh giá trạng thái ghế tại thời điểm cập nhật, không chỉ lúc mở màn hình. |
| FR-04.06 | Đặt mới phải được kiểm tra bằng giờ server và trạng thái cho phép bán. Khi tới giờ đóng, không tạo giữ mới. Quy ước biên đề xuất: mở ≤ thời điểm giữ < đóng; cần review OPEN-05. |
| FR-04.07 | Lưu audit sửa giá/đóng bán. Cập nhật cấu hình liên quan phải được phản ánh sang Search/Report mà không thay đổi dữ liệu giao dịch đã chốt. |

**AC-04:** Sửa giá từ 500.000 lên 600.000 đồng không đổi đơn cũ đã giữ ở 500.000 đồng. Xóa ghế đã bán bị từ chối. Đặt mới sau giờ đóng bị từ chối dù giao diện vẫn hiển thị đang bán.

### 4.5. FR-05 — Tìm và duyệt concert

**Nguồn:** BL-05, PF-03. **Ưu tiên:** P1. **Tác nhân:** Khách/người mua.  
**Đầu vào:** Từ khóa tên/nghệ sĩ; địa điểm, thời gian, khoảng giá. **Đầu ra:** Danh sách phù hợp, rỗng hoặc thông báo tìm kiếm tạm không khả dụng.

| ID | Yêu cầu |
|---|---|
| FR-05.01 | Tìm theo tên concert/nghệ sĩ, lọc địa điểm, thời gian và khoảng giá; chỉ trả nội dung được công bố. |
| FR-05.02 | Khoảng lọc không hợp lệ, ví dụ min > max hoặc ngày bắt đầu > kết thúc, phải báo lỗi; không âm thầm thay điều kiện. |
| FR-05.03 | Không có kết quả là kết quả rỗng, phân biệt với Search lỗi. Có phân trang/giới hạn kết quả để không trả dữ liệu vô hạn. |
| FR-05.04 | Search lỗi vẫn cho duyệt danh sách concert cơ bản từ Booking. Không yêu cầu fallback phải cung cấp toàn bộ khả năng tìm kiếm nâng cao. |
| FR-05.05 | Chỉ mục có thể trễ nhưng không được dùng để quyết định quyền mua hoặc làm lộ nội dung đã bị ẩn; thiết kế phải xử lý trường hợp bản sao còn dữ liệu công bố cũ. |
| FR-05.06 | Ngữ nghĩa lọc giá theo ghế còn bán hay giá cấu hình, kết hợp nhiều suất diễn, cách sắp xếp/tìm không dấu được chốt tại OPEN-06. |

**AC-05:** Tắt Search, khách vẫn duyệt được concert cơ bản và checkout theo đường dẫn hợp lệ. Nhập bộ lọc mâu thuẫn nhận lỗi có hướng sửa. Kết quả cũ không cho phép mua sai giá hoặc bỏ qua đóng bán.

### 4.6. FR-06 — Chi tiết concert và sơ đồ ghế

**Nguồn:** BL-06, PF-03. **Ưu tiên:** P0. **Tác nhân:** Khách/người mua.  
**Đầu vào:** Concert/suất diễn. **Đầu ra:** Lịch, địa điểm, điều kiện mua, giá và trạng thái ghế.

| ID | Yêu cầu |
|---|---|
| FR-06.01 | Hiển thị ghế còn bán, đang giữ và đã bán; có định danh khu vực/hàng/số ghế để khách nhận biết vị trí. Không tiết lộ người đang giữ hoặc người đã mua. |
| FR-06.02 | Hiển thị điều kiện 1–4 ghế/đơn, giữ 5 phút và giá VND/phí 0 trước checkout. |
| FR-06.03 | Trạng thái ghế là ảnh chụp tại thời điểm đọc; khi yêu cầu giữ phải kiểm tra lại. Không coi chọn ghế trên giao diện là đã có quyền giữ. |
| FR-06.04 | Khi ghế vừa bị người khác giữ/bán, thông báo ghế không còn khả dụng và cho chọn lại; không tự thay ghế khác. |

**AC-06:** Hai khách cùng nhìn thấy một ghế trống; khi cùng đặt, chỉ một khách giữ thành công. Khách còn lại nhận phản hồi tranh chấp, không nhận đơn với ghế thay thế.

### 4.7. FR-07 — Giữ ghế và tạo đơn nhất quán

**Nguồn:** BL-07, BR-01–BR-04/06/15, PF-03. **Ưu tiên:** P0. **Tác nhân:** Người mua.  
**Tiền điều kiện:** Đã xác thực, được đặt, suất diễn được phép bán.  
**Đầu vào:** Suất diễn, 1–4 ID ghế khác nhau, định danh thao tác. **Đầu ra thành công:** Mã đơn, danh sách ghế, giá chốt, tổng tiền, thời điểm giữ, `expiresAt`, giờ server.

| ID | Yêu cầu |
|---|---|
| FR-07.01 | Kiểm tra ghế tồn tại, cùng suất diễn, không trùng trong yêu cầu; từ chối 0 hoặc hơn 4 ghế. Không tự bỏ ghế lỗi rồi giữ phần còn lại. |
| FR-07.02 | Kiểm tra lịch bán, rate limit và quyền giữ từng ghế tại thời điểm quyết định. Một ghế có tối đa một chủ giữ hợp lệ. |
| FR-07.03 | Giữ toàn bộ ghế và tạo đơn chờ thanh toán thành một kết quả nghiệp vụ nhất quán. Lỗi giữa quá trình không để lại đơn thiếu ghế hoặc một phần giữ chỗ mồ côi có hiệu lực. |
| FR-07.04 | Thời hạn = thời điểm giữ thành công + 5 phút. Giữ chỉ hợp lệ trước `expiresAt`; tác vụ dọn dẹp chậm không kéo dài quyền giữ. |
| FR-07.05 | Reload, đổi tab, retry cùng thao tác không tạo đơn mới hoặc gia hạn. Nếu phản hồi bị mất, trả/tra được đơn đã tạo của thao tác cũ. |
| FR-07.06 | Cùng mã thao tác và cùng nội dung trả cùng kết quả nghiệp vụ; cùng mã nhưng nội dung khác bị từ chối xung đột, không sửa đơn đã có. |
| FR-07.07 | Không áp đặt giới hạn tổng vé mỗi tài khoản/suất diễn ngoài giới hạn 4 vé/đơn và rate limit đã quy định. |

**Ngoại lệ:** Chưa đăng nhập; ghế không tồn tại/khác suất; tranh chấp/hết vé; ngoài giờ bán; vượt tần suất; hệ thống chưa xác định được kết quả. Trường hợp chưa rõ phải hướng khách tra cứu thao tác/đơn, không khẳng định thất bại để khách đặt lại ngay.

**AC-07:** Chọn 4 ghế có 1 ghế đã giữ thì không giữ 3 ghế còn lại. Retry sau khi mất phản hồi trả đúng một đơn và hạn cũ. Request đến sau hạn không xác nhận giữ cũ dù tác vụ hết hạn chưa chạy.

### 4.8. FR-08 — Chi tiết đơn và tổng tiền

**Nguồn:** BL-08, BR-05/06, PF-03. **Ưu tiên:** P0. **Tác nhân:** Người mua.  
**Đầu vào:** Mã đơn thuộc người mua. **Đầu ra:** Chi tiết giá, ghế, thời hạn, trạng thái và hành động được phép.

| ID | Yêu cầu |
|---|---|
| FR-08.01 | Lưu snapshot giá từng ghế khi tạo đơn; tổng = tổng giá ghế, phí = 0, tiền tệ = VND. |
| FR-08.02 | Trước thanh toán hiển thị concert, suất diễn, ghế, đơn giá, phí, tổng, thời hạn còn lại và mã đơn. |
| FR-08.03 | Countdown được suy ra từ `expiresAt` và thời gian server; client hiển thị không thay đổi thời hạn thật. |
| FR-08.04 | Trạng thái đơn, payment, refund, vé/file và email phải tách biệt. Khi một nguồn trạng thái chưa đọc được, báo tạm chưa lấy được dữ liệu, không đổi thành thất bại/chưa thanh toán. |
| FR-08.05 | Nút thao tác phụ thuộc trạng thái; hệ thống vẫn kiểm tra lại khi nhận yêu cầu, không tin trạng thái nút trên giao diện. |

**AC-08:** Đơn có hai ghế 500.000 và 700.000 đồng hiển thị tổng 1.200.000, phí 0. Client gửi tổng 1 đồng không thay đổi số tiền thu. Đơn hết hạn vẫn hiển thị payment đang xác minh nếu chưa có kết luận.

### 4.9. FR-09 — Thanh toán giả lập và xác nhận đơn

**Nguồn:** BL-09, BR-06–BR-09, PF-04. **Ưu tiên:** P0. **Tác nhân:** Người mua, Booking, Payment, cổng giả lập.  
**Tiền điều kiện bắt đầu thu:** Chủ đơn hợp lệ; đơn chờ thanh toán; giữ còn hạn; không có khoản thu thành công hoặc giao dịch chưa rõ cần đối chiếu.  
**Đầu vào:** Mã đơn, định danh thao tác; Payment nhận số tiền/VND từ hệ thống. **Đầu ra:** Giao dịch và kết quả đang xử lý/đang xác minh/thành công/thất bại đã xác định; trạng thái đơn riêng.

| ID | Yêu cầu |
|---|---|
| FR-09.01 | Tạo hoặc lấy giao dịch của cùng thao tác; bấm nhiều lần, callback lặp, xử lý lại không tạo thu trùng. Khóa thao tác khác cũng không cho thu mới nếu đơn đã có payment chưa rõ hoặc thành công. |
| FR-09.02 | Simulator hỗ trợ thành công, thất bại rõ ràng, chậm/đang xử lý, timeout hoặc mất callback để kiểm chứng phục hồi. Không xử lý thẻ thật. |
| FR-09.03 | Payment xác minh đúng tham chiếu giao dịch, đơn, số tiền và tiền tệ trước khi công nhận thành công. Kết quả từ trình duyệt chỉ là tín hiệu tra cứu. |
| FR-09.04 | Timeout/mất kết nối chuyển sang hoặc giữ đang xác minh; truy vấn kết quả cũ. Không thu lại để thử xem lần trước có thành công không. |
| FR-09.05 | Booking chỉ xác nhận khi payment thành công đã xác minh, đơn chưa hủy/hết hạn, còn trước hạn và toàn bộ ghế vẫn do đơn giữ hợp lệ. Chốt xác nhận và bán toàn bộ ghế nhất quán. |
| FR-09.06 | Nếu đơn đã xác nhận bởi cùng giao dịch, kết quả lặp không phát hành thêm vé hoặc hoàn tiền nhầm. |
| FR-09.07 | Nếu đã thu nhưng không thể xác nhận do đơn hủy/hết hạn/mất quyền ghế, không khôi phục đơn; chuyển hoàn toàn bộ theo FR-11. |
| FR-09.08 | Khi thất bại đã xác định, ghi lý do phù hợp; giữ chỗ vẫn tuân theo hạn gốc và thao tác hủy, không tự gia hạn hoặc giải phóng ngay. Chính sách lần thu mới tại OPEN-03. |
| FR-09.09 | Callback không khớp, không xác thực được hoặc xung đột với kết quả đã xác minh không được ghi đè tùy tiện. Lưu dấu vết và đối chiếu kết quả đáng tin cậy. |

**AC-09:** Callback thành công tới sau hạn làm phát sinh hoàn tiền, không bán lại ghế. Mất phản hồi sau thu tiền vẫn tra được một giao dịch thu. Giả mạo kết quả thành công trên client không cấp vé. Payment thành công nhưng thiếu một quyền giữ không xác nhận một phần đơn.

### 4.10. FR-10 — Hủy và hết hạn giữ

**Nguồn:** BL-10, BR-02/03/08/09, PF-05. **Ưu tiên:** P0. **Tác nhân:** Người mua/hệ thống thời gian.  
**Đầu vào:** Mã đơn, thao tác hủy; hoặc hạn giữ tới thời điểm xử lý. **Đầu ra:** Trạng thái đơn thực tế, kết quả giải phóng và trạng thái tiền độc lập.

| ID | Yêu cầu |
|---|---|
| FR-10.01 | Người mua chỉ hủy đơn của mình trước xác nhận. Đơn đã xác nhận từ chối hủy; hủy lặp trên đơn đã hủy trả kết quả hiện có. |
| FR-10.02 | Từ `expiresAt`, giữ hết hiệu lực; hệ thống xử lý giải phóng và ghi đơn hết hạn nếu đơn chưa kết thúc bằng kết quả khác. |
| FR-10.03 | Hủy/hết hạn chỉ giải phóng ghế vẫn thuộc lần giữ của đơn đó; không giải phóng ghế đã chuyển sang lần giữ mới hoặc đã bán. |
| FR-10.04 | Khi hủy và xác nhận cạnh tranh, chỉ một chuyển trạng thái thắng. Xác nhận thắng hợp lệ thì không hủy; hủy thắng thì không xác nhận và hoàn khoản đã thu nếu có. |
| FR-10.05 | Khi hết hạn và xác nhận cạnh tranh, kiểm tra thời gian tại điểm chốt, không dựa vào thời gian callback được tạo hoặc tác vụ hết hạn đã chạy chưa. |
| FR-10.06 | Hủy/hết hạn không dừng việc xác minh tiền đang chưa rõ. Chưa thu/thất bại rõ thì không refund; đã thu thì refund; chưa rõ thì tiếp tục xác minh. |

**AC-10:** Chạy đồng thời hủy và callback thành công không thể có đơn vừa hủy vừa xác nhận. Tác vụ hết hạn cũ không làm trống ghế người khác đang giữ. Đơn hết hạn không chuyển lại chờ thanh toán.

### 4.11. FR-11 — Hoàn tiền bù trừ

**Nguồn:** BL-11, BR-09/10/16, PF-06. **Ưu tiên:** P0. **Tác nhân:** Hệ thống; admin retry hợp lệ.  
**Kích hoạt:** Khoản thu thành công đã xác minh nhưng đơn không thể xác nhận. **Đầu vào:** Giao dịch thu, đơn, lý do bù trừ, định danh yêu cầu. **Đầu ra:** Yêu cầu hoàn toàn bộ và tiến độ xác minh.

| ID | Yêu cầu |
|---|---|
| FR-11.01 | Hoàn toàn bộ số tiền đã thu của giao dịch; không hoàn theo từng vé, không cho khách tự hoàn đơn đã xác nhận. |
| FR-11.02 | Tạo hoặc lấy yêu cầu hoàn hiện có; retry, callback lặp hoặc khởi động lại không hoàn trùng. |
| FR-11.03 | Phân biệt đang hoàn, đã hoàn và cần hỗ trợ. Chỉ đánh dấu đã hoàn khi có kết quả thành công đã xác minh. |
| FR-11.04 | Nếu timeout, đối chiếu yêu cầu hoàn cũ trước khi gửi lại an toàn; không tạo giao dịch hoàn mới tùy tiện. |
| FR-11.05 | Khi chưa tự xử lý được, giữ hồ sơ, lý do, lần thử gần nhất và tác vụ tiếp theo/cờ cần hỗ trợ. Retry/chuyển hỗ trợ theo cấu hình OPEN-09. |
| FR-11.06 | Hoàn thành không phục hồi đơn hoặc cấp vé; cập nhật báo cáo theo ngày hoàn thực tế và yêu cầu email kết quả. |
| FR-11.07 | Ghi audit yêu cầu/tiến trình quan trọng và can thiệp admin; không cho admin đánh dấu đã hoàn bằng tay khi chưa xác minh. |

**AC-11:** Khoản thu 1.200.000 đồng cho đơn hết hạn có đúng một nghĩa vụ hoàn 1.200.000 đồng. Callback hoàn lặp không tăng tiền hoàn báo cáo. Hoàn timeout vẫn hiện đang hoàn/cần hỗ trợ, không hiện đã hoàn.

### 4.12. FR-12 — Đơn và vé của tôi

**Nguồn:** BL-12, BR-06/07/10/11/14, PF-07/09. **Ưu tiên:** P0. **Tác nhân:** Người mua.  
**Đầu vào:** Phiên và yêu cầu danh sách/chi tiết. **Đầu ra:** Đơn/vé thuộc mình và tình trạng xử lý hiện tại.

| ID | Yêu cầu |
|---|---|
| FR-12.01 | Cung cấp danh sách và chi tiết đơn/vé của chủ tài khoản, có giới hạn/phân trang và mã để tra lại sau mất mạng. |
| FR-12.02 | Hiển thị trạng thái đơn, các tham chiếu thanh toán/hoàn, ghế/giá, thời hạn, vé và trạng thái file; không cho xem đơn người khác bằng cách thay ID. |
| FR-12.03 | Đơn xác nhận nhưng phát hành vé chưa hoàn tất hiển thị đang chuẩn bị vé; vé đã có nhưng file chưa sẵn sàng hiển thị đang chuẩn bị file. Không hiển thị đơn thất bại. |
| FR-12.04 | Nếu tiền chưa rõ, hướng người mua theo dõi đơn hiện có; không yêu cầu mua lại để xử lý tình trạng chưa xác minh. |

**AC-12:** Mất mạng sau thanh toán, mở lại tài khoản thấy kết quả thực tế mà không phát sinh đơn/thu mới. Đơn hủy có refund chờ vẫn xuất hiện với cả hai trạng thái.

### 4.13. FR-13 — Vé QR và file vé

**Nguồn:** BL-13, BR-11/14, PF-07. **Ưu tiên:** P0. **Tác nhân:** Hệ thống/người mua.  
**Tiền điều kiện:** Đơn xác nhận, ghế đã bán. **Đầu vào:** Đơn, từng ghế, chủ sở hữu, suất diễn. **Đầu ra:** Vé duy nhất cho ghế của đơn; định danh QR; file khi sẵn sàng.

| ID | Yêu cầu |
|---|---|
| FR-13.01 | Phát hành một vé hợp lệ cho mỗi ghế của đơn đã xác nhận; không tạo vé cho đơn chờ/hủy/hết hạn. |
| FR-13.02 | Tạo vé lặp phải lấy lại vé cũ; sau lỗi một phần, tiếp tục các vé còn thiếu, không tạo bản vé hợp lệ thứ hai cho ghế đã có vé. |
| FR-13.03 | QR xác định được vé để hệ thống kiểm tra; không cho phép sửa/đoán dữ liệu QR để tạo vé hợp lệ. Phương án token/chữ ký là thiết kế kỹ thuật. |
| FR-13.04 | Người mua xem/tải vé có kiểm tra quyền; file/QR không đặt trong chỉ mục công khai hoặc trả cho tài khoản khác. |
| FR-13.05 | Tách bản ghi vé khỏi file: đang chuẩn bị, sẵn sàng, cần xử lý lại. Lỗi tạo/lưu file không hủy đơn, giải phóng ghế hoặc tạo refund. |
| FR-13.06 | Retry file dùng lại vé/QR hiện có; định dạng file, quy cách một file hay nhiều file và thời hạn liên kết tại OPEN-07. |

**AC-13:** Đơn 4 ghế đã có 2 vé khi hệ thống dừng; chạy lại cuối cùng có đúng 4 vé. File lỗi không đổi quyền sở hữu. Người khác dùng URL/ID file không đọc được vé trái quyền.

### 4.14. FR-14 — Check-in online

**Nguồn:** BL-14, BR-12/14/16, PF-08. **Ưu tiên:** P0. **Tác nhân:** Nhân viên check-in.  
**Tiền điều kiện:** Đã xác thực, được phân công suất diễn, có kết nối tới hệ thống.  
**Đầu vào:** QR, suất diễn đang kiểm, cổng. **Đầu ra:** Thành công; đã dùng kèm thời gian/cổng trước; không hợp lệ/sai suất/không quyền; hoặc chưa thể xác minh.

| ID | Yêu cầu |
|---|---|
| FR-14.01 | Kiểm tra phân công, QR, vé tồn tại hợp lệ, đúng suất diễn và chưa sử dụng trước khi ghi nhận. |
| FR-14.02 | Ghi lần sử dụng bằng giờ server, cổng và nhân viên; chỉ một lần chuyển từ chưa dùng sang đã dùng được thành công. |
| FR-14.03 | Hai cổng cùng quét chỉ một lần thành công; lần còn lại trả đã dùng với thông tin lần sử dụng đã ghi. |
| FR-14.04 | Quét lại không tăng lượt vào; nếu phản hồi thành công trước đó bị mất, kết quả sau chỉ xác nhận lần dùng trước, không cấp một lượt mới. |
| FR-14.05 | Lỗi mạng/hệ thống trả chưa thể xác minh, không báo vé giả và không tự cho qua bằng offline. |
| FR-14.06 | Không hỗ trợ mở lại vé đã dùng/ra vào lại. Ghi audit kết quả kiểm vé quan trọng; thông tin QR không ghi thô vào log công khai. |
| FR-14.07 | Cổng phải được ghi nhận rõ; danh mục cổng và cửa sổ thời gian check-in tại OPEN-08, không tự đặt chính sách vào trước/sau giờ diễn. |

**AC-14:** 100 yêu cầu đồng thời với một vé có đúng một lần ghi nhận sử dụng. Vé của suất khác bị từ chối. Thiết bị mất mạng không hiện kết quả được vào nếu chưa có kết quả xác minh.

### 4.15. FR-15 — Email sandbox và lịch sử gửi

**Nguồn:** BL-15, BR-11, PF-07. **Ưu tiên:** P1. **Tác nhân:** Hệ thống/admin.  
**Kích hoạt:** Xác nhận vé hoặc kết quả hoàn tiền cần thông báo. **Đầu ra:** Yêu cầu thông báo và lịch sử từng lần gửi.

| ID | Yêu cầu |
|---|---|
| FR-15.01 | Gửi thông báo xác nhận vé và kết quả hoàn tiền vào email sandbox theo dữ liệu giao dịch đã xác minh. Không thông báo đã hoàn khi refund còn pending. |
| FR-15.02 | Lưu loại thông báo, đối tượng liên quan, người nhận, trạng thái, số lần thử, thời gian và lỗi phù hợp. |
| FR-15.03 | Sự kiện lặp không tạo thông báo nghiệp vụ mới tùy tiện; gửi lại có chủ đích phải được nhận diện và lưu thành lần gửi hỗ trợ. |
| FR-15.04 | Email lỗi không thay đổi đơn/vé/ghế; có retry và admin yêu cầu gửi lại có kiểm soát. Không cấp thêm quyền gửi lại cho khách nếu chưa được chốt. |
| FR-15.05 | Mẫu, điều kiện gửi khi file chưa sẵn sàng và liên kết/đính kèm tại OPEN-07. Không cam kết exactly-once ở hộp thư nếu sandbox không hỗ trợ đối chiếu lần gửi chưa rõ. |

**AC-15:** Tắt email sandbox không chặn checkout và check-in của vé đã phát hành. Retry email không phát hành thêm vé. Tra cứu được lịch sử lỗi và lần gửi lại.

### 4.16. FR-16 — Tra cứu và hỗ trợ giao dịch

**Nguồn:** BL-16, BR-10/14/16, PF-09. **Ưu tiên:** P1. **Tác nhân:** Ban tổ chức/admin.  
**Đầu vào:** Mã đơn/payment/refund/vé hoặc tiêu chí người mua; tác vụ hỗ trợ nếu là admin. **Đầu ra:** Hồ sơ liên kết, trạng thái và kết quả yêu cầu xử lý.

| ID | Yêu cầu |
|---|---|
| FR-16.01 | Tra cứu theo mã hoặc người mua trong phạm vi quyền; trả liên kết đơn–payment–refund–vé–file–email để giải thích trạng thái. Ngữ nghĩa tìm người mua tại OPEN-10. |
| FR-16.02 | Chỉ admin được yêu cầu xác minh payment, retry refund hợp lệ, chuẩn bị lại file hoặc gửi lại email. Ban tổ chức chỉ tra cứu theo phạm vi BRD. |
| FR-16.03 | Kiểm tra trạng thái hiện tại khi thực hiện: tác vụ đã hoàn tất trả kết quả cũ/no-op; tác vụ không hợp lệ bị từ chối có lý do. |
| FR-16.04 | Không ép xác nhận khi tiền chưa rõ, không bỏ qua quyền ghế, không phục hồi đơn hết hạn, không cấp vé cho đơn chưa xác nhận, không đánh dấu refund thành công bằng tay. |
| FR-16.05 | Yêu cầu retry được tiếp nhận phải có tham chiếu theo dõi; tiếp nhận không có nghĩa tác vụ đã thành công. Ghi admin, lý do can thiệp, đối tượng, thời gian và kết quả vào audit. |
| FR-16.06 | Theo dõi trường hợp chưa giải quyết cho tới có kết quả rõ hoặc tiếp tục cần hỗ trợ. Không bắt buộc thêm service hay hệ thống helpdesk riêng. |

**AC-16:** Admin retry refund đang chưa rõ chỉ đối chiếu nghĩa vụ cũ. Admin cố xác nhận đơn hết hạn bị từ chối. Ban tổ chức A tìm người mua có đơn ở A và B chỉ thấy đơn thuộc A.

### 4.17. FR-17 — Báo cáo cơ bản

**Nguồn:** BL-17, BRD mục 7, PF-10. **Ưu tiên:** P1. **Tác nhân:** Ban tổ chức.  
**Đầu vào:** Phạm vi concert/suất diễn và kỳ báo cáo. **Đầu ra:** Chỉ số, đơn vị, phạm vi, mốc dữ liệu, trạng thái cập nhật và phần giao dịch chưa hoàn tất.

| ID | Yêu cầu |
|---|---|
| FR-17.01 | Cung cấp vé bán, tiền thu, tiền hoàn, thu ròng, lấp đầy và check-in theo công thức mục 9. |
| FR-17.02 | Thu/hoàn theo ngày giao dịch thực tế tương ứng, múi giờ Asia/Ho_Chi_Minh; không dùng ngày chạy pipeline thay ngày giao dịch. |
| FR-17.03 | Sự kiện lặp/chạy lại pipeline không cộng trùng; dữ liệu tới trễ phải được đưa về đúng kỳ nghiệp vụ. |
| FR-17.04 | Hiển thị mốc dữ liệu đã xử lý và độ cũ; không trả dữ liệu cũ với nhãn thời gian hiện tại gây hiểu nhầm. |
| FR-17.05 | Khoản đang xác minh/đang hoàn tách khỏi số hoàn tất; giải thích được chênh lệch tiền thu và vé bằng các giao dịch liên quan. |
| FR-17.06 | Report lỗi không chặn checkout. Phạm vi ngày cho chỉ số vé/check-in/lấp đầy và lịch sử mẫu số tại OPEN-11. |

**AC-17:** Thu ngày 03/11, hoàn ngày 04/11 xuất hiện đúng hai ngày tương ứng. Chạy lại cùng dữ liệu không đổi tổng. Mẫu số ghế bằng 0 hiển thị Chưa áp dụng.

### 4.18. FR-18 — Audit nghiệp vụ

**Nguồn:** BL-18, BR-16, PF-09. **Ưu tiên:** P1. **Tác nhân:** Thành phần xử lý/admin tra cứu.

| ID | Yêu cầu |
|---|---|
| FR-18.01 | Lưu dấu vết sửa giá, đóng bán, refund, check-in, đổi quyền và can thiệp đơn. |
| FR-18.02 | Audit tối thiểu có người/hệ thống thực hiện, thời gian server, loại hành động, đối tượng, kết quả và tham chiếu giao dịch/thao tác. Thay đổi cấu hình cần đủ giá trị trước/sau để giải thích. |
| FR-18.03 | Admin tra theo đối tượng, người thực hiện và thời gian; có giới hạn/phân trang. |
| FR-18.04 | Không cho người dùng sửa/xóa audit qua chức năng nghiệp vụ. Không log mật khẩu, token phiên hoặc QR nguyên bản vào dữ liệu tra cứu phổ thông. |
| FR-18.05 | Thiết kế phải bảo đảm không mất dấu vết bắt buộc khi tác vụ thành công nhưng tiến trình dừng; chính sách lưu giữ và phạm vi đọc audit tại OPEN-12. |

**AC-18:** Tra được ai đổi giá, giá trước/sau và thời gian. Yêu cầu hỗ trợ bị từ chối cũng có kết quả tra cứu. Audit check-in liên kết tới lần dùng vé tương ứng.

### 4.19. FR-19 — Bảo vệ mở bán và phản hồi tải

**Nguồn:** BL-19, BR-03/04/15, PF-03/11. **Ưu tiên:** P0. **Tác nhân:** Hệ thống.

| ID | Yêu cầu |
|---|---|
| FR-19.01 | Áp dụng giới hạn tần suất giữ ghế có cấu hình; xác định phạm vi tính ngưỡng ở thiết kế, không hardcode thành hạn mức vé/tài khoản. |
| FR-19.02 | Phân biệt tranh chấp ghế, hết vé, ngoài giờ bán, vượt tần suất và quá tải; phản hồi trong thời gian hữu hạn. |
| FR-19.03 | Quá tải không được bỏ kiểm tra chủ giữ, giá, idempotency hoặc quyền đối tượng. |
| FR-19.04 | Nếu đã ghi kết quả nhưng client timeout, có đường tra cứu/retry an toàn; không hứa rằng mọi timeout đều chưa xử lý. |
| FR-19.05 | Rate limit, thời hạn phản hồi và mức tải được chốt sau baseline tại OPEN-09; không tự cam kết TPS/latency trong SRS. |

**AC-19:** Tải đồng thời cao không bán vượt số ghế; yêu cầu bị giới hạn không tạo giữ chỗ mới. Lỗi quá tải có hướng thử lại/tra cứu phù hợp.

### 4.20. FR-20 — Phục hồi và mô phỏng lỗi

**Nguồn:** BL-20, PF-11. **Ưu tiên:** P0. **Tác nhân:** Vận hành thử nghiệm; không phải vai trò được sửa giao dịch.

| ID | Yêu cầu |
|---|---|
| FR-20.01 | Sau mất kết nối/khởi động lại, tìm được công việc dở dang từ dữ liệu bền vững, xác minh kết quả cũ và tiếp tục không tạo tác động trùng. |
| FR-20.02 | Không tự chuyển công việc chưa rõ thành hoàn tất/thất bại để hết cảnh báo; duy trì theo dõi hoặc cần hỗ trợ. |
| FR-20.03 | Tái lập mất callback, mất mạng sau thu, restart giữa giao dịch, refund chậm/lỗi, file/email lỗi, tranh ghế, hai cổng cùng quét và replay báo cáo. |
| FR-20.04 | Mỗi lần chạy ghi phiên bản, môi trường, cấu hình, dữ liệu đầu vào, workload, điểm gây lỗi, kết quả đo và đối chiếu trạng thái nghiệp vụ. |
| FR-20.05 | Đo riêng xem concert, xem ghế, giữ ghế, thanh toán và check-in; không dùng một số latency tổng để thay các luồng khác nhau. |

**AC-20:** Restart sau ghi thu thành công nhưng trước xác nhận đơn vẫn đưa giao dịch tới xác nhận hợp lệ hoặc hoàn tiền/cần hỗ trợ có lý do. Replay không sinh thêm thu, hoàn, vé hoặc check-in.

## 5. Mô hình trạng thái logic

### 5.1. Nguyên tắc

Không gộp trạng thái đơn, thanh toán, hoàn tiền, vé, file và email. Cờ `needsAttention` có thể đi kèm bất kỳ đối tượng đang cần theo dõi; không bắt buộc biến đơn thành một trạng thái Cần hỗ trợ. Mỗi chuyển trạng thái cần kiểm tra điều kiện ở thời điểm ghi, lưu được kết quả và xử lý lặp an toàn.

### 5.2. Đơn hàng

| Từ | Sự kiện / điều kiện | Sang | Tác động |
|---|---|---|---|
| Chưa có đơn | Giữ toàn bộ ghế thành công | PENDING_PAYMENT | Lưu giá chốt và hạn giữ |
| PENDING_PAYMENT | Payment thành công đã xác minh; còn hạn; đủ quyền giữ | CONFIRMED | Bán toàn bộ ghế; tiếp tục phát hành vé |
| PENDING_PAYMENT | Khách hủy hợp lệ và thắng chuyển trạng thái | CANCELLED | Giải phóng đúng lần giữ; đối chiếu tiền |
| PENDING_PAYMENT | Đến hạn, chưa xác nhận | EXPIRED | Giữ hết hiệu lực; giải phóng đúng chủ; đối chiếu tiền |
| CONFIRMED | Callback/xác nhận lặp | CONFIRMED | Trả kết quả hiện có, không refund giao dịch đã xác nhận |
| CANCELLED / EXPIRED | Payment thành công đến muộn | Không đổi | Tạo/lấy refund toàn bộ |
| CONFIRMED / CANCELLED / EXPIRED | Hủy/hết hạn lặp | Không đổi | Không giải phóng ghế sai chủ; trả kết quả thích hợp |

Không có chuyển CANCELLED/EXPIRED → CONFIRMED hoặc PENDING_PAYMENT. Không có CONFIRMED → CANCELLED trong MVP.

### 5.3. Giữ ghế và tồn kho

| Từ | Điều kiện | Sang |
|---|---|---|
| AVAILABLE | Yêu cầu hợp lệ giữ toàn bộ tập ghế | HELD, gắn đơn/lần giữ và expiresAt |
| HELD | Xác nhận đơn hợp lệ trước hạn | SOLD, gắn đơn được xác nhận |
| HELD | Hủy/hết hạn đúng lần giữ | AVAILABLE về mặt khả dụng, nếu không có quyền giữ/bán mới |
| SOLD | Retry release/xác nhận cũ | SOLD, không thay quyền |

HELD đã quá hạn không còn là quyền giữ hợp lệ dù bản ghi dọn dẹp chưa cập nhật. Hiển thị và cấp quyền mới phải xét thời hạn thật. Nếu cấp giữ mới, tác vụ của lần giữ trước không được tác động lần mới.

### 5.4. Payment và refund

| Đối tượng | Trạng thái | Ý nghĩa / chuyển tiếp |
|---|---|---|
| Payment | NOT_STARTED | Chưa bắt đầu thu; có thể sang PROCESSING khi đủ điều kiện |
| Payment | PROCESSING | Đã tiếp nhận xử lý; sang SUCCEEDED, FAILED hoặc VERIFYING |
| Payment | VERIFYING | Chưa kết luận; đối chiếu đến SUCCEEDED/FAILED hoặc tiếp tục theo dõi |
| Payment | SUCCEEDED | Thu đã xác minh; không đổi thành FAILED do timeout/callback cũ |
| Payment | FAILED | Đã xác định thất bại của lần thu; lần thu mới phụ thuộc OPEN-03 |
| Refund | NOT_APPLICABLE | Chưa có nghĩa vụ hoàn; không đồng nghĩa đã hoàn 0 đồng |
| Refund | PENDING | Nghĩa vụ hoàn tồn tại, đang xử lý/đối chiếu |
| Refund | SUCCEEDED | Đã xác minh hoàn thành; replay không tăng tiền hoàn |
| Refund | NEEDS_SUPPORT | Chưa giải quyết; admin retry/đối chiếu có thể đưa về PENDING hoặc SUCCEEDED |

Refund không thay đổi Payment SUCCEEDED: lịch sử vẫn ghi đã thu và đã hoàn riêng. Trạng thái payment ở đây áp dụng từng giao dịch/lần thu, không tự chốt chính sách nhiều lần thu cho một đơn.

### 5.5. Vé, file và email

| Trục | Chuyển trạng thái |
|---|---|
| Vé | Chưa phát hành → ISSUED khi đơn xác nhận và ghế đã bán → USED khi check-in thành công |
| File vé | PREPARING → READY; lỗi có thể sang RETRY_REQUIRED → PREPARING → READY |
| Email | PENDING → SENT; lỗi sang RETRY_REQUIRED → PENDING; gửi lại chủ đích lưu lần gửi riêng |

Không có USED → ISSUED để vào lại. File/email lỗi không làm lùi trạng thái vé/đơn.

### 5.6. Tổ hợp trạng thái và cách hiển thị

| Đơn | Payment | Refund | Vé/file | Thông điệp nghiệp vụ |
|---|---|---|---|---|
| PENDING_PAYMENT | VERIFYING | Không áp dụng | Chưa có vé | Đang xác minh thanh toán; hạn giữ vẫn tiếp tục |
| EXPIRED | VERIFYING | Chưa xác định cần hoàn | Chưa có vé | Đơn hết hạn; đang kiểm tra kết quả tiền |
| EXPIRED/CANCELLED | SUCCEEDED | PENDING | Chưa có vé | Đơn không được xác nhận; đang hoàn toàn bộ |
| EXPIRED/CANCELLED | SUCCEEDED | SUCCEEDED | Chưa có vé | Đơn kết thúc; đã hoàn tiền |
| CONFIRMED | SUCCEEDED | Không áp dụng | Chưa phát hành xong | Đặt vé thành công; đang chuẩn bị vé |
| CONFIRMED | SUCCEEDED | Không áp dụng | ISSUED; file chưa sẵn sàng | Vé thuộc khách; đang chuẩn bị file |
| CONFIRMED | SUCCEEDED | Không áp dụng | USED | Vé đã sử dụng, kèm lần check-in |

Tổ hợp CONFIRMED nhưng chưa có thu thành công đã xác minh, hoặc vé phát hành cho đơn EXPIRED, là sai lệch cần điều tra; không phải trạng thái nghiệp vụ bình thường.

## 6. Dữ liệu logic và quy tắc toàn vẹn

Các trường dưới đây là tập thông tin tối thiểu để thiết kế, không phải DDL. Tên/kiểu vật lý, giới hạn chuỗi, chỉ mục và cơ chế mã hóa thuộc thiết kế chi tiết.

| ID | Nhóm dữ liệu / chủ quản | Thông tin tối thiểu | Ràng buộc |
|---|---|---|---|
| DR-01 | Danh tính/phân công | User ID, vai trò, phạm vi ban tổ chức/suất diễn, hiệu lực | Không lấy quyền từ tham số client tùy ý |
| DR-02 | Concert / Catalog | ID, chủ tổ chức, tên, nghệ sĩ, nội dung, điều kiện mua, trạng thái công bố | Nội dung nháp/ẩn không công khai |
| DR-03 | Suất diễn / Catalog | ID, concert, địa điểm, lịch diễn, mở/đóng bán | Một khoảng bán/suất; cùng múi giờ diễn giải rõ |
| DR-04 | Ghế cấu hình / Inventory | ID trong suất, khu vực, hàng, số ghế, giá VND, cấu hình bán | Không đổi quyền lợi ghế đang giữ/đã bán |
| DR-05 | Quyền giữ / Inventory | ID lần giữ, đơn, ghế, thời điểm giữ, expiresAt, trạng thái | Một chủ giữ hợp lệ/ghế; toàn bộ hoặc không |
| DR-06 | Đơn và dòng đơn / Ordering | ID, chủ đơn, tổ chức/suất, ghế, snapshot giá, phí, tổng, tiền tệ, hạn, trạng thái | 1–4 ghế khác nhau, cùng suất; tổng do server tính |
| DR-07 | Payment / Payment | ID, đơn, tham chiếu simulator, số tiền/VND, trạng thái, thời gian thu, định danh thao tác, kết quả xác minh | Không thu trùng; tham chiếu và số tiền khớp |
| DR-08 | Refund / Payment | ID, payment, đơn, lý do, toàn bộ số tiền thu, trạng thái, thời gian hoàn, lịch sử thử | Một nghĩa vụ hoàn toàn bộ/giao dịch; không hoàn trùng |
| DR-09 | Vé / Ticketing | ID, đơn/dòng đơn, suất, ghế, chủ sở hữu, định danh QR, trạng thái, thời gian phát hành | Một vé hợp lệ/ghế đã bán của đơn |
| DR-10 | Lần check-in / Ticketing | Vé, suất, cổng, nhân viên, thời gian server, kết quả | Tối đa một lần sử dụng thành công/vé |
| DR-11 | File / Ticketing | Vé/đơn liên quan, tham chiếu file, trạng thái chuẩn bị, lần thử/lỗi | Không dùng URL công khai thay kiểm tra quyền |
| DR-12 | Thông báo / Notification | Loại, tham chiếu nghiệp vụ, người nhận, trạng thái, các lần gửi | Phân biệt replay và gửi lại chủ đích |
| DR-13 | Chỉ mục / Search | Dữ liệu công bố, ID nguồn, phiên bản/thời điểm cập nhật | Bản sao; không quyết định giá/giữ ghế |
| DR-14 | Báo cáo / Report | Phạm vi, kỳ, chỉ số, dữ liệu nguồn liên kết, mốc xử lý | Loại tác động trùng; đúng ngày nghiệp vụ |
| DR-15 | Audit / thành phần xử lý | Tác nhân, thời gian, đối tượng, hành động, kết quả, thay đổi/tham chiếu | Tra được chuỗi thao tác; không lộ bí mật |
| DR-16 | Theo dõi công việc | Đối tượng, loại việc, lần xử lý, lỗi, trạng thái/cờ hỗ trợ, tham chiếu thao tác | Phục hồi được, không bắt buộc bảng job riêng |

### 6.1. Bất biến bắt buộc

1. Một ghế của một suất diễn không thể bán cho hai đơn; mỗi ghế chỉ có một lần giữ hợp lệ tại một thời điểm.
2. Đơn được tạo với đủ tập ghế; không giữ/xác nhận một phần.
3. Xác nhận đơn đòi hỏi thu thành công đã xác minh và giữ toàn bộ ghế còn hợp lệ tại thời điểm chốt.
4. Giá snapshot không bị sửa bởi thay bảng giá hoặc dữ liệu client.
5. Đơn kết thúc hủy/hết hạn không phục hồi; thu thành công liên quan phải đi tới hoàn toàn bộ hoặc tiếp tục theo dõi có lý do.
6. Một tác động thu/hoàn/phát hành/check-in không được lặp do retry/replay.
7. Trạng thái chưa rõ phải còn dấu vết và khả năng đối chiếu; không bị xóa để che mất giao dịch.
8. Lỗi thành phần phụ không làm thay đổi quyền ghế, vé hay số tiền đã xác minh.

### 6.2. Thời gian và tiền

Giữ/xác nhận/hết hạn/check-in dùng giờ server. Truyền timestamp có múi giờ hoặc quy ước thống nhất, không truyền chuỗi giờ địa phương thiếu múi giờ. Báo cáo dùng ngày kinh doanh Asia/Ho_Chi_Minh. Phân biệt thời gian nghiệp vụ xảy ra với thời gian hệ thống nhận/xử lý sự kiện.

Tiền VND phải lưu/tính chính xác, không sai số số thực. Phí = 0, không tự thêm thuế/phí/chiết khấu. Nếu chấp nhận giá lẻ, giá 0 hoặc cần mức giá tối đa, phải chốt OPEN-05 trước khi hoàn thiện validation và hợp đồng API.

## 7. Giao tiếp bên ngoài và giữa các thành phần

### 7.1. Hợp đồng chức năng tối thiểu

| ID | Bên gọi → bên xử lý | Dữ liệu yêu cầu | Dữ liệu kết quả bắt buộc |
|---|---|---|---|
| INT-01 | Client → danh tính | Thông tin đăng ký/đăng nhập/khôi phục | Kết quả xác thực hoặc lỗi; phiên theo cơ chế được chọn |
| INT-02 | Ban tổ chức → Booking | Cấu hình concert/suất/ghế/giá/lịch, hành động | ID, phiên bản/trạng thái mới hoặc lỗi; không ghi đè âm thầm thay đổi cạnh tranh |
| INT-03 | Client → Search/Booking | Từ khóa, bộ lọc hoặc ID suất diễn | Nội dung công bố, sơ đồ ghế, dấu thời gian/phân trang phù hợp |
| INT-04 | Người mua → Booking | Suất, tập ghế, mã thao tác | Đơn, giá chốt, tổng/VND, expiresAt, giờ server hoặc lỗi nghiệp vụ |
| INT-05 | Booking → Payment | Đơn, số tiền/VND từ hệ thống, định danh thao tác | Payment ID, trạng thái, tham chiếu đối chiếu |
| INT-06 | Payment ↔ simulator | Thu/hoàn/truy vấn, tham chiếu và số tiền | Kết quả xác minh, thời gian nghiệp vụ; callback có thể trùng/muộn |
| INT-07 | Payment → Booking | Payment, đơn, số tiền/VND, kết quả đã xác minh | Booking xử lý trạng thái hiện có an toàn; không tin thông điệp client |
| INT-08 | Booking → Payment | Yêu cầu hoàn toàn bộ, giao dịch thu, lý do | Refund ID, trạng thái, kết quả đối chiếu |
| INT-09 | Nhân viên → Booking | QR, suất và cổng | Kết quả kiểm vé; lần sử dụng trước nếu đã dùng |
| INT-10 | Booking/Payment → Notification | Loại thông báo, tham chiếu nghiệp vụ, người nhận tối thiểu | Trạng thái tiếp nhận và lịch sử gửi |
| INT-11 | Booking → Search | Nội dung, trạng thái công bố, ID và phiên bản nguồn | Bản sao có thể xây dựng lại; xử lý ẩn/cập nhật cũ |
| INT-12 | Booking/Payment → Report | Ghế cấu hình/bán, vé, check-in, thu/hoàn, thời gian nguồn | Chỉ số đúng, không cộng trùng và mốc dữ liệu |
| INT-13 | Chủ vé → Booking/lưu trữ | Yêu cầu xem/tải theo định danh vé/file | Nội dung có kiểm soát quyền hoặc trạng thái chưa sẵn sàng |

Đây là hợp đồng logic, không quy định REST method, HTTP code, tên topic hay format wire. Thiết kế API phải map từng lỗi/hành vi ở SRS sang hợp đồng cụ thể.

### 7.2. Idempotency và thứ tự xử lý

| Loại thao tác | Cách nhận diện nghiệp vụ cần có | Kết quả khi lặp |
|---|---|---|
| Giữ ghế/tạo đơn | Người gọi, loại thao tác, mã thao tác, nội dung ban đầu | Lấy đơn/kết quả cũ, không reset hạn |
| Thu tiền | Đơn, giao dịch thu, mã thao tác/tham chiếu simulator | Không tạo thu mới khi cũ chưa rõ/đã thành công |
| Hoàn tiền | Giao dịch thu và nghĩa vụ hoàn toàn bộ | Lấy refund cũ; đối chiếu nếu chưa rõ |
| Phát hành vé | Đơn/dòng ghế đã bán | Lấy vé đã có hoặc tạo phần còn thiếu |
| Check-in | Vé và lần sử dụng đã được chốt | Báo đã sử dụng; không tăng lượt vào |
| Sự kiện Search/Report/Notification | Event ID và phiên bản/tham chiếu nghiệp vụ | Không cộng/gửi/cập nhật lặp ngoài chủ đích |

Correlation ID dùng nối dấu vết; không mặc định một correlation ID là đủ để định danh mọi thao tác nghiệp vụ. Thao tác mới cần phân biệt với retry của thao tác cũ. Cùng mã idempotency nhưng payload khác phải báo xung đột. Thời hạn lưu mã, hành vi sau khi hết thời hạn và cơ chế lưu kết quả đang xử lý phải được thiết kế theo OPEN-09; ràng buộc nghiệp vụ vẫn phải ngăn tác động trùng khi replay muộn.

Thông điệp lặp hoặc đảo thứ tự không làm lùi trạng thái đã xác minh, phục hồi đơn hết hạn hoặc đưa nội dung đã ẩn trở lại công khai từ bản cập nhật cũ. Hệ thống phải lưu đủ định danh/phiên bản để phát hiện và đối chiếu. Cơ chế outbox/Saga phải bảo đảm thay đổi đã ghi không làm mất công việc cần phát tiếp sau restart.

## 8. Giao diện logic và phản hồi lỗi

### 8.1. Nhóm màn hình

| Màn hình | Nội dung/hành động tối thiểu |
|---|---|
| Tài khoản | Đăng ký, đăng nhập, khôi phục và lỗi xác minh |
| Danh sách/chi tiết concert | Từ khóa, bộ lọc, lịch, địa điểm, điều kiện mua; fallback duyệt cơ bản |
| Sơ đồ ghế | Khu vực/hàng/ghế, giá, khả dụng; chọn 1–4 ghế; phản hồi tranh chấp |
| Checkout | Mã đơn, ghế/giá snapshot, tổng/phí/VND, countdown, thanh toán/hủy nếu hợp lệ |
| Đơn và vé của tôi | Danh sách/chi tiết; trạng thái tách trục; QR/tải file khi hợp lệ |
| Quản lý concert | Bản nháp/công bố/ẩn; suất, ghế, giá, khoảng bán; lỗi bảo vệ ghế |
| Check-in | Suất được phân công, cổng, quét QR; kết quả mới/đã dùng/chưa thể xác minh |
| Tra cứu hỗ trợ | Tìm mã/người mua; chuỗi giao dịch; tác vụ admin và kết quả theo dõi |
| Báo cáo | Phạm vi/kỳ, chỉ số và mốc cập nhật; khoản còn xử lý |
| Quyền và audit | Gán phạm vi; tra dấu vết theo người/đối tượng/thời gian |

Đây là yêu cầu thông tin và thao tác, không bắt buộc mỗi dòng là một trang riêng. Không bắt buộc WebSocket/polling hay một bố cục UI cụ thể.

### 8.2. Danh mục lỗi logic

Tên mã dưới đây là đề xuất hợp đồng để team kỹ thuật thống nhất; hành vi và phân biệt tình huống là yêu cầu bắt buộc.

| Tình huống | Mã logic gợi ý | Phản hồi/hành động |
|---|---|---|
| Chưa xác thực / trái phạm vi | AUTH_REQUIRED / ACCESS_DENIED | Yêu cầu đăng nhập hoặc từ chối; không lộ dữ liệu |
| Dữ liệu ghế/bộ lọc không hợp lệ | INVALID_INPUT | Chỉ rõ trường cần sửa, không tạo kết quả một phần |
| Ngoài giờ bán | SALE_NOT_OPEN / SALE_CLOSED | Không giữ mới; nêu tình trạng bán |
| Một số ghế không còn | SEAT_UNAVAILABLE | Không giữ một phần; cho chọn lại |
| Hết ghế bán | SOLD_OUT | Không tạo đơn; phân biệt với Search lỗi |
| Giữ đã hết hạn | HOLD_EXPIRED | Trả trạng thái đơn, không gia hạn |
| Cùng mã thao tác khác nội dung | IDEMPOTENCY_CONFLICT | Không thực hiện nội dung mới với mã cũ |
| Trạng thái không cho thao tác | STATE_CONFLICT | Trả trạng thái thực tế và lý do |
| Kết quả tiền chưa rõ | PAYMENT_VERIFYING | Tra cứu giao dịch cũ; không khuyên trả tiền lần nữa |
| Refund chưa xong | REFUND_PENDING / NEEDS_SUPPORT | Theo dõi tiến độ, không báo đã hoàn |
| Vé/file chưa sẵn sàng | TICKET_PREPARING / FILE_NOT_READY | Giữ quyền vé/đơn; tiếp tục chuẩn bị |
| QR không hợp lệ / sai suất | INVALID_TICKET / WRONG_PERFORMANCE | Từ chối lượt vào |
| Vé đã dùng | ALREADY_USED | Hiển thị thời gian/cổng lần trước |
| Không thể xác minh check-in | CHECKIN_UNAVAILABLE | Thử tra/quét lại khi có kết nối; không tự cho qua |
| Vượt tần suất / quá tải | RATE_LIMITED / TEMPORARILY_UNAVAILABLE | Hướng thử lại/tra cứu có kiểm soát |

Phản hồi cần có mã phân loại, thông điệp, tham chiếu hỗ trợ và lỗi trường khi thích hợp. Không trả stack trace hay bí mật. Thông báo “tiếp nhận xử lý” phải khác “hoàn tất”. Timeout của client không tự quyết định trạng thái nghiệp vụ.

## 9. Định nghĩa báo cáo và đối chiếu

| Chỉ số | Định nghĩa | Loại trừ / lưu ý |
|---|---|---|
| Vé bán | Đếm vé phát hành hợp lệ của đơn CONFIRMED | Không dùng payment thành công hoặc số ghế đang giữ để thay thế |
| Tiền thu | Tổng khoản thu SUCCEEDED, mỗi giao dịch một lần | Loại payment đang xác minh/thất bại; tính cả khoản đã thu đang chờ hoàn |
| Tiền hoàn | Tổng refund SUCCEEDED, mỗi giao dịch hoàn một lần | Không tính PENDING/NEEDS_SUPPORT |
| Thu ròng | Tiền thu − tiền hoàn | Không gọi là lợi nhuận; một kỳ có thể âm nếu hoàn khoản thu kỳ trước |
| Lấp đầy | Ghế đã bán / tổng ghế cấu hình bán của suất × 100% | Mẫu số 0: Chưa áp dụng; không lấy số ghế đang giữ làm tử số |
| Check-in | Số vé có lần sử dụng thành công | Quét lặp/từ chối không tăng số |

Mốc thu/hoàn dùng thời gian giao dịch thực tế đã xác minh, theo ngày Việt Nam. Ví dụ: thu 1.200.000 đồng lúc 23:59 ngày 03/11 và hoàn lúc 00:05 ngày 04/11 thì ngày 03/11 ghi thu 1.200.000, ngày 04/11 ghi hoàn 1.200.000. Không chuyển hoàn về ngày thu cho tiện tổng hợp.

Chênh lệch cần giải thích: payment thành công nhưng đơn hết hạn đang hoàn; đơn xác nhận nhưng phát hành vé chưa đủ; dữ liệu Booking/Payment tới Report khác thời điểm. Hiển thị mốc cập nhật theo nguồn nếu cần, không tạo cảm giác các nguồn đã đồng bộ khi chưa có bằng chứng. Công thức lấp đầy theo nhiều suất dùng tổng tử/tổng mẫu tương ứng, không lấy trung bình đơn giản tỷ lệ từng suất; cách chọn snapshot lịch sử cần chốt OPEN-11.

## 10. Yêu cầu phi chức năng

| ID | Nhóm | Yêu cầu / bằng chứng đánh giá |
|---|---|---|
| NFR-01 | Nhất quán | Không bán trùng, giữ một phần, thu/hoàn trùng hoặc check-in hai lần dưới tải đồng thời/replay. Đối chiếu dữ liệu nguồn sau kịch bản mục 11. |
| NFR-02 | Phục hồi | Sau restart/mất callback, công việc tiếp tục an toàn hoặc có cờ hỗ trợ và lý do; không mất giao dịch đã ghi. |
| NFR-03 | Cô lập lỗi | Search lỗi có duyệt cơ bản; email/file lỗi không mất vé; Report lỗi không chặn checkout và nêu độ cũ. |
| NFR-04 | Bảo mật | Xác thực và quyền theo đối tượng; bảo vệ QR/file/thông tin người mua; không lưu dữ liệu thẻ thật; không để client quyết tiền/quyền. |
| NFR-05 | Quan sát | Tra được đơn–payment–refund–vé; theo dõi giao dịch treo, refund chờ, lỗi email/file, trễ dữ liệu và tranh ghế. Có tham chiếu xuyên thành phần. |
| NFR-06 | Thời gian | Giờ server là căn cứ; ngày kinh doanh Việt Nam; phân biệt thời gian nguồn/nhận/xử lý; kiểm chứng biên hết hạn. |
| NFR-07 | Hiệu năng | Báo cáo tải đồng thời, throughput, p50/p95/p99 hoặc bộ percentile được chọn, tỷ lệ lỗi kỹ thuật và từ chối nghiệp vụ riêng cho từng luồng; ghi môi trường và dữ liệu. Chưa cam kết con số trước baseline. |
| NFR-08 | Trải nghiệm | Phân biệt hết ghế, hết hạn, quá tải, đang xác minh, đang hoàn và đang chuẩn bị vé; reload không làm mới hạn giữ. |
| NFR-09 | Tái lập | Có cấu hình simulator, seed/dataset hoặc bộ dữ liệu tái dùng và hướng chạy lại thực nghiệm; cô lập fault injection khỏi quyền của khách thông thường. |
| NFR-10 | Quản lý dữ liệu | Không dùng xóa dữ liệu chưa xử lý để kết thúc công việc; thời hạn lưu đơn/tiền/audit và bảo vệ dữ liệu theo OPEN-12. |

Không tự đặt SLA uptime, số người đồng thời, TPS, thời gian hoàn hoặc RPO/RTO khi hai tài liệu nguồn chưa chốt. Team kỹ thuật phải đề xuất và đo baseline, sau đó chủ dự án xác nhận ngưỡng nghiệm thu định lượng.

## 11. Kịch bản nghiệm thu tích hợp trọng tâm

Các kịch bản dưới đây là đầu vào cho QA/load test/fault simulation; không yêu cầu thêm unit test trong tài liệu thiết kế này.

| Mã | Tình huống và cách kích hoạt | Kết quả bắt buộc | Liên kết |
|---|---|---|---|
| E2E-01 | Công bố → tìm → giữ → thanh toán → xem QR → check-in | Đơn xác nhận, đủ vé, tiền đúng, đúng một lần dùng/vé | FR-01–FR-14 |
| E2E-02 | Nhiều người đồng thời giữ cùng ghế | Một chủ giữ hợp lệ; các yêu cầu còn lại bị từ chối đúng lý do | FR-07/19 |
| E2E-03 | Yêu cầu 4 ghế có 1 ghế không khả dụng | Không giữ 3 ghế còn lại, không tạo đơn thiếu ghế | FR-07 |
| E2E-04 | Reload/đổi tab/retry trước và sau hạn | Cùng expiresAt; không kéo dài quyền giữ | FR-07/08/10 |
| E2E-05 | Gửi lại tạo đơn sau mất phản hồi | Một đơn của cùng thao tác; payload khác với cùng mã bị từ chối | FR-07/08 |
| E2E-06 | Bấm thanh toán nhiều lần/callback lặp và đảo thứ tự | Một tác động thu; không lùi kết quả đã xác minh; không thêm vé/refund nhầm | FR-09 |
| E2E-07 | Payment thành công tới sau hết hạn | Đơn không phục hồi; ghế không bị lấy từ chủ mới; hoàn toàn bộ | FR-09–FR-11 |
| E2E-08 | Hủy và xác nhận thanh toán đồng thời | Một kết quả đơn; nếu hủy thắng thì xử lý khoản thu bằng refund | FR-09/10 |
| E2E-09 | Tác vụ hết hạn chậm; gửi xác nhận sau expiresAt | Không xác nhận dù chưa dọn bản ghi giữ; tiền đã thu được bù trừ | FR-07/09/10 |
| E2E-10 | Timeout sau thu, mất callback rồi restart | Tra được giao dịch; không thu mới; đi tới kết quả rõ hoặc hỗ trợ có lý do | FR-09/20 |
| E2E-11 | Refund chậm, mất phản hồi, retry/callback lặp | Một nghĩa vụ hoàn toàn bộ; chưa xác minh thì không báo đã hoàn | FR-11 |
| E2E-12 | Dừng giữa phát hành nhiều vé; lỗi file/email | Phát hành đủ, không trùng; đơn/ghế không bị hủy; retry độc lập | FR-13/15/20 |
| E2E-13 | Hai cổng quét cùng QR, mất phản hồi ở cổng thắng | Một lần dùng; quét sau trả thời gian/cổng cũ, không cho lượt mới | FR-14 |
| E2E-14 | Tắt Search và Report | Duyệt cơ bản/checkout hoạt động; báo cáo báo lỗi/độ cũ đúng | FR-05/17 |
| E2E-15 | Replay báo cáo, thu và hoàn khác ngày | Không cộng trùng; đúng kỳ Asia/Ho_Chi_Minh | FR-17 |
| E2E-16 | Thay ID để truy cập chéo tài khoản/ban tổ chức/suất | Từ chối dữ liệu và thao tác trái quyền; không tải được QR/file | FR-02/12/13/14/16 |
| E2E-17 | Sửa giá sau giữ; xóa ghế đang giữ/đã bán | Snapshot không đổi; thay đổi quyền lợi bị từ chối | FR-04/08 |
| E2E-18 | Tác vụ release cũ chạy sau khi ghế có chủ giữ mới | Không ảnh hưởng chủ mới | FR-10 |
| E2E-19 | Admin retry hợp lệ và ép xác nhận đơn hết hạn | Retry có theo dõi/audit; ép xác nhận bị từ chối | FR-16/18 |
| E2E-20 | Tăng tải và vượt rate limit | Phản hồi hữu hạn, lý do rõ; không vi phạm bất biến | FR-19/20 |

Kết quả nghiệm thu phải gồm dữ liệu trước/sau và đối chiếu tổng hợp, không chỉ ảnh màn hình thành công. Lỗi nghiệp vụ dự kiến như tranh ghế không được gộp vào lỗi hệ thống để đánh giá sai chất lượng dưới tải.

## 12. Ma trận truy vết yêu cầu

| Backlog | Luồng | Đặc tả | Quy tắc/nguồn bổ sung | Ưu tiên |
|---|---|---|---|---|
| BL-01 | PF-01 | FR-01 | BRD vai trò; BR-14 | P0 |
| BL-02 | PF-01 | FR-02 | BR-14/16 | P0 |
| BL-03 | PF-02 | FR-03 | BR-14/16 | P0 |
| BL-04 | PF-02 | FR-04 | BR-04/05/13/16 | P0 |
| BL-05 | PF-03 | FR-05 | BRD yêu cầu độc lập Search | P1 |
| BL-06 | PF-03 | FR-06 | BR-03/04/05 | P0 |
| BL-07 | PF-03 | FR-07 | BR-01/02/03/04/06/15 | P0 |
| BL-08 | PF-03 | FR-08 | BR-05/06 | P0 |
| BL-09 | PF-04 | FR-09 | BR-06/07/08/09 | P0 |
| BL-10 | PF-05 | FR-10 | BR-02/03/08/09 | P0 |
| BL-11 | PF-06 | FR-11 | BR-09/10/16 | P0 |
| BL-12 | PF-07/09 | FR-12 | BR-06/07/10/11/14 | P0 |
| BL-13 | PF-07 | FR-13 | BR-11/14 | P0 |
| BL-14 | PF-08 | FR-14 | BR-12/14/16 | P0 |
| BL-15 | PF-07 | FR-15 | BR-11 | P1 |
| BL-16 | PF-09 | FR-16 | BR-10/14/16 | P1 |
| BL-17 | PF-10 | FR-17; mục 9 | BRD mục 7 | P1 |
| BL-18 | PF-09 | FR-18 | BR-16 | P1 |
| BL-19 | PF-03/11 | FR-19 | BR-03/04/15 | P0 |
| BL-20 | PF-11 | FR-20; NFR-01–NFR-10 | BRD mục 8 | P0 |
| BL-21–BL-23 | Không thuộc MVP | Mục 2.2 — ngoài phạm vi | BRD phạm vi mở rộng | P2 |

## 13. Các quyết định cần chốt trước thiết kế chi tiết

Các điểm mở không làm mất hiệu lực yêu cầu đã chốt và không được tự triển khai thành chính sách mới. Có thể thiết kế các phần độc lập trước; chỉ phần bị ảnh hưởng cần giữ quyết định mở.

| Mã | Câu hỏi cần quyết định | Phương án đưa ra review, chưa phê duyệt | Phần bị ảnh hưởng / người chốt |
|---|---|---|---|
| OPEN-01 | Dùng định danh gì để đăng nhập? Xác minh đăng ký/khôi phục, chính sách mật khẩu, TTL phiên và thời gian áp dụng đổi quyền? | Dùng năng lực hệ thống danh tính đã chọn; tránh tự xây thêm cơ chế xác thực khi không cần | FR-01/02, INT-01; chủ dự án + kỹ thuật |
| OPEN-02 | Ẩn/đóng bán sớm và đóng bán theo lịch tác động thế nào tới giữ đang còn hạn? | Chặn giữ mới, cho giữ hiện có chạy đến hạn gốc; không hủy đơn xác nhận. Đây là đề xuất, chưa là quy tắc được chốt | FR-03/04/09; chủ dự án |
| OPEN-03 | Có cho lần thanh toán mới sau thất bại đã xác định không? | Cho phép khi vẫn còn giữ hợp lệ, dùng lần thu mới gắn cùng đơn; tuyệt đối không làm mới khi lần cũ chưa rõ. Cần chốt trước triển khai nút thử thanh toán lại | FR-09; chủ dự án + kỹ thuật |
| OPEN-04 | Khi payment thành công nhưng quyền giữ bất ngờ không toàn vẹn dù chưa tới hạn, đơn kết thúc bằng trạng thái nào? | Không xác nhận, giải phóng phần còn thuộc đơn, hoàn toàn bộ, đánh dấu hỗ trợ; tên trạng thái kết thúc cần review, không tự thêm trạng thái ngoài mô hình hiện tại | FR-09/10/11, mục 5; chủ dự án + kỹ thuật |
| OPEN-05 | Biên mở/đóng, quan hệ lịch bán với giờ diễn, giá 0/giá lẻ/giá tối đa? | Biên [mở, đóng), số tiền nguyên đồng; yêu cầu giá dương nếu chưa thiết kế luồng vé miễn phí. Chưa áp dụng giá dương như yêu cầu bắt buộc | FR-04/07/08; chủ dự án |
| OPEN-06 | Khoảng giá dựa ghế cấu hình hay còn bán? Concert nhiều suất khớp bộ lọc thế nào? Sắp xếp và tìm không dấu? | Trả concert có ít nhất một suất thỏa tập bộ lọc, hiển thị giá tham khảo; Booking xác minh lại | FR-05/06; chủ dự án + frontend/Search |
| OPEN-07 | Nội dung email, khi nào gửi nếu vé/file chưa đủ, liên kết hay đính kèm, định dạng và quyền tải? | Email dẫn tới trang vé có xác thực; file PDF là lựa chọn để review, không phải định dạng đã chốt. Quyền QR của hỗ trợ phải cấp riêng nếu cần | FR-13/15; chủ dự án + kỹ thuật |
| OPEN-08 | Cổng lấy từ danh mục nào? Có giới hạn thời gian check-in trước/sau giờ diễn không? | Cổng gắn suất/địa điểm; không tự đặt cửa sổ giờ hoặc quyền vào lại | FR-14; chủ dự án |
| OPEN-09 | Rate limit, thời gian phản hồi, lịch retry/xác minh, ngưỡng hỗ trợ, lưu idempotency, tải/SLA và trễ dữ liệu? | Cấu hình theo môi trường, đo baseline rồi chốt ngưỡng; giữ nguyên bất biến kể cả khi vượt ngưỡng | FR-09/11/15/19/20; kỹ thuật đề xuất, chủ dự án xác nhận |
| OPEN-10 | Tra theo người mua bằng ID/email/tên, khớp chính xác hay một phần? | ID hoặc email chính xác trước; không suy ra quyền tìm toàn hệ thống cho ban tổ chức | FR-16; chủ dự án |
| OPEN-11 | Kỳ ngày cho vé/check-in/lấp đầy, mốc snapshot và mẫu số ghế khi cấu hình thay đổi? | Vé theo ngày phát hành, check-in theo ngày sử dụng; lấp đầy là snapshot có mốc. Cần chốt trước thiết kế báo cáo lịch sử | FR-17, mục 9; chủ dự án + Report |
| OPEN-12 | Thời hạn lưu đơn/tiền/audit, quyền đọc audit và che thông tin cá nhân? | Xác định chính sách cho dữ liệu giả lập; không xóa giao dịch còn đối chiếu. Không tự đặt yêu cầu pháp lý của thanh toán thật | DR/NFR-04/10, FR-18; chủ dự án + kỹ thuật |

## 14. Điều kiện bàn giao sang thiết kế kỹ thuật

Team kỹ thuật có thể dùng SRS để thiết kế domain, use case, API, dữ liệu và cơ chế xử lý đồng thời. Bộ thiết kế tiếp theo cần:

1. Map FR/DR/INT sang module và hợp đồng cụ thể trong 5 service hiện có; giữ ranh giới trách nhiệm tại mục 2.3.
2. Chỉ ra điểm chốt nhất quán cho giữ toàn bộ, xác nhận/bán ghế, hủy/hết hạn và check-in; mô tả cách phục hồi khi dừng trước/sau từng điểm chốt.
3. Định nghĩa API và sự kiện với danh tính, quyền đối tượng, lỗi, mã thao tác, tham chiếu và version; phân biệt yêu cầu tiếp nhận với kết quả hoàn tất.
4. Thiết kế state machine và dữ liệu bền vững bảo vệ các bất biến; mô tả rõ trường hợp thông điệp lặp, sai thứ tự, timeout và kết quả chưa xác minh.
5. Chốt OPEN tương ứng trước khi khóa thiết kế phần bị ảnh hưởng; ghi quyết định và cập nhật phiên bản SRS thay vì đưa giả định vào code như yêu cầu đã duyệt.
6. Lập kế hoạch nghiệm thu bao phủ BL-01–BL-20, dùng AC và E2E làm cơ sở; bổ sung ngưỡng định lượng sau baseline.

SRS hoàn thành vai trò đặc tả khi từng yêu cầu được truy vết vào thiết kế và có bằng chứng nghiệm thu. Việc triển khai được luồng mua vé đơn lẻ chưa đủ để xác nhận toàn bộ MVP hoàn tất.
