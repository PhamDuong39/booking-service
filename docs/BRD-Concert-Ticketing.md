# BRD — Concert Ticketing

**Business Requirements Document · Phiên bản 1.0 · Ngày 03/11/2026**  
**Trạng thái:** Hoàn thiện theo phạm vi và quyết định nghiệp vụ đã được chủ dự án xác nhận.

## 1. Mục tiêu

Xây dựng nền tảng bán vé concert có chọn ghế, giúp ban tổ chức mở bán và theo dõi kết quả; người mua đặt vé, thanh toán và sử dụng vé thuận tiện. Hệ thống phải bảo vệ quyền sở hữu ghế, không thu tiền trùng và có hướng xử lý rõ ràng khi đã thu tiền nhưng không thể cấp vé.

Đây là pet project phục vụ học hệ thống phân tán và xây dựng portfolio. Người dùng, thanh toán và tải vận hành được giả lập; email sử dụng sandbox. Giá trị được chứng minh qua luồng nghiệp vụ hoàn chỉnh, load test và mô phỏng lỗi có thể tái lập, không qua doanh thu hay số người dùng thật.

## 2. Vai trò và quyền hạn

| Vai trò | Quyền và nhu cầu chính |
|---|---|
| Người mua vé | Đăng ký, đăng nhập, khôi phục mật khẩu; tìm concert; đặt, thanh toán, xem đơn và vé của mình. Khách chưa đăng nhập được xem concert nhưng phải đăng nhập để đặt vé. |
| Ban tổ chức | Quản lý concert, suất diễn, ghế, giá và lịch bán; tra cứu đơn và báo cáo thuộc phạm vi của mình. |
| Nhân viên check-in | Quét và xác nhận vé cho suất diễn được phân công; xem kết quả và thông tin lần sử dụng trước khi vé bị quét lại. |
| Quản trị viên | Quản lý quyền/phân công; tra cứu giao dịch lỗi, hỗ trợ khách hàng và yêu cầu thử lại xử lý hợp lệ. Không được bỏ qua kiểm tra thanh toán hoặc ghế để xác nhận đơn/cấp vé. |

## 3. Phạm vi bàn giao

**MVP:** Quản lý concert và một khoảng mở–đóng bán cho mỗi suất diễn; tài khoản; tìm kiếm; chọn/giữ ghế; đặt hàng; thanh toán giả lập; hoàn tiền bù trừ; vé QR; check-in online; email sandbox; tra cứu/hỗ trợ giao dịch; báo cáo cơ bản và dấu vết thao tác quan trọng.

**Ngoài MVP:** Thanh toán thật, SMS/push, phòng chờ mở bán, nhiều đợt bán với giá/hạn mức riêng, vé đứng, đơn nhiều suất diễn, đổi ghế/chuyển nhượng/bán lại vé, hoàn tiền từng vé, khách tự trả vé đã xác nhận, check-in offline hoặc vào lại. Hủy/đổi lịch concert và chính sách hoàn tiền tương ứng thuộc giai đoạn mở rộng. Funnel, báo cáo streaming và cá nhân hóa tìm kiếm chưa thuộc nghiệm thu MVP.

Việc chưa giới hạn tổng vé/tài khoản/suất diễn là quyết định của MVP; giới hạn tần suất giữ ghế vẫn bắt buộc.

## 4. Luồng nghiệp vụ chính

1. Ban tổ chức tạo concert nháp, cấu hình suất diễn, địa điểm, sơ đồ ghế, giá và thời gian bán; công bố để khách xem.
2. Khách tìm concert, xem điều kiện mua và trạng thái ghế; đăng nhập và chọn tối đa 4 ghế thuộc cùng một suất diễn.
3. Hệ thống kiểm tra điều kiện bán, giữ toàn bộ ghế và tạo đơn chờ thanh toán. Khách xem thời gian còn lại, từng ghế, giá, phí và tổng tiền.
4. Khách thanh toán giả lập. Hệ thống xác minh kết quả và quyền giữ ghế trước khi xác nhận đơn, bán ghế và phát hành vé.
5. Vé xuất hiện trong tài khoản; email được gửi qua sandbox. Nếu file chưa sẵn sàng, hiển thị đang chuẩn bị vé và tiếp tục xử lý.
6. Nhân viên quét QR tại cổng. Vé hợp lệ được ghi nhận sử dụng một lần; lần quét sau bị từ chối và hiển thị thông tin lần sử dụng trước.
7. Ban tổ chức theo dõi báo cáo; quản trị viên tra cứu và xử lý các giao dịch cần hỗ trợ trong phạm vi được phép.

**Nhánh ngoại lệ:** Không giữ đủ ghế thì không tạo giữ chỗ một phần. Hủy/hết hạn giải phóng ghế. Nếu tiền đã được thu nhưng không thể xác nhận đơn, tạo yêu cầu hoàn toàn bộ tiền; không tự phục hồi đơn đã hết hạn.

## 5. Quy tắc nghiệp vụ đã chốt

| Mã | Quy tắc |
|---|---|
| BR-01 | Một đơn thuộc một suất diễn, từ 1 đến 4 vé có ghế. Chưa giới hạn tổng vé một tài khoản mua cho cùng suất diễn. |
| BR-02 | Giữ ghế 5 phút từ thời điểm giữ thành công. Thời gian máy chủ là căn cứ; reload, đổi tab hoặc gửi lại yêu cầu không gia hạn. |
| BR-03 | Một ghế chỉ có một bên giữ hợp lệ và chỉ được bán một lần. Giữ nhiều ghế phải thành công toàn bộ hoặc không giữ ghế nào. |
| BR-04 | Thời gian mở/đóng bán do máy chủ quyết định. Mỗi suất diễn có một khoảng bán; yêu cầu đặt mới ngoài khoảng này bị từ chối. |
| BR-05 | Dùng VND; chưa thu phí riêng, hiển thị phí 0 đồng. Tổng tiền bằng tổng giá ghế. Giá do hệ thống tính và chốt khi tạo đơn; sửa giá chỉ áp dụng đơn mới, không đổi giá đơn đang giữ hợp lệ hoặc đã xác nhận. |
| BR-06 | Gửi lại cùng một thao tác mua/thanh toán không tạo thêm đơn hoặc thu tiền trùng. Sau mất mạng, khách có thể mở lại đơn để xem kết quả thực tế. |
| BR-07 | Không xác nhận mua vé chỉ dựa vào thông báo trên trình duyệt. Khi kết quả thanh toán chưa rõ, hiển thị “Đang xác minh thanh toán”; timeout không đồng nghĩa thanh toán thất bại. |
| BR-08 | Chỉ xác nhận đơn khi thanh toán thành công và toàn bộ ghế vẫn thuộc giữ chỗ hợp lệ của đơn. Khách chỉ hủy trước khi xác nhận; khi hủy và thanh toán đồng thời, hệ thống phải có một kết quả nhất quán. |
| BR-09 | Giữ chỗ hết hạn không được khôi phục bởi thanh toán đến muộn. Nếu đã thu tiền nhưng đơn không thể xác nhận, hoàn toàn bộ số tiền đã thu; retry không tạo hoàn tiền trùng. |
| BR-10 | “Đang hoàn tiền” khác “Đã hoàn tiền”. Trường hợp chưa thể giải quyết phải tiếp tục được theo dõi hoặc chuyển “Cần hỗ trợ”, không báo hoàn tất khi chưa có kết quả. |
| BR-11 | Đơn đã xác nhận không bị hủy vì email lỗi hoặc file vé chưa tạo xong. Vé vẫn thuộc người mua; việc chuẩn bị file/gửi email được thử lại. |
| BR-12 | Mỗi vé check-in online một lần, không hỗ trợ ra vào lại. Lưu thời gian, cổng và nhân viên; hai cổng quét đồng thời chỉ một lần thành công. |
| BR-13 | Không cho sửa/xóa ghế đang được giữ hợp lệ hoặc đã bán làm thay đổi quyền lợi của đơn/vé. Không hỗ trợ đổi ghế trong MVP. |
| BR-14 | Người mua chỉ xem đơn/vé của mình; ban tổ chức chỉ quản lý dữ liệu của mình; nhân viên chỉ check-in suất diễn được phân công. |
| BR-15 | Giới hạn tần suất giữ ghế, phản hồi rõ khi tranh chấp ghế, hết vé hoặc quá tải. Ngưỡng vận hành được cấu hình và chốt sau baseline. |
| BR-16 | Lưu dấu vết người/hệ thống thực hiện, thời gian, đối tượng và kết quả của sửa giá, đóng bán, hoàn tiền, check-in, thay đổi quyền và can thiệp đơn. |

Trạng thái hiển thị cần phân biệt trạng thái đơn, thanh toán, hoàn tiền và file vé. Ví dụ: đơn đã hết hạn vẫn có thể đang xác minh thanh toán; đơn đã xác nhận có thể đang chuẩn bị file vé. Không gộp các trường hợp này thành một nhãn “Thất bại”.

## 6. Backlog tính năng

**Ưu tiên:** P0 = bảo vệ giao dịch/luồng bán vé; P1 = hoàn thiện quản lý và trải nghiệm; P2 = mở rộng. Tất cả dòng thuộc **MVP**, kể cả P1, đều nằm trong phạm vi bàn giao. Thứ tự ưu tiên không đồng nghĩa loại bỏ tính năng.

| Mã | Tính năng / vai trò | Yêu cầu và điều kiện nghiệm thu chính | Ưu tiên | Giai đoạn |
|---|---|---|---|---|
| BL-01 | Tài khoản — Người mua | Đăng ký, đăng nhập, khôi phục mật khẩu; người chưa đăng nhập không tạo checkout. | P0 | MVP |
| BL-02 | Quyền và phân công — Admin | Gán vai trò/phạm vi ban tổ chức, suất diễn cho nhân viên; truy cập trái phạm vi bị từ chối. | P0 | MVP |
| BL-03 | Quản lý concert — Ban tổ chức | Tạo/sửa bản nháp, công bố/ẩn concert; quản lý nội dung, nghệ sĩ, địa điểm và điều kiện mua. | P0 | MVP |
| BL-04 | Cấu hình suất diễn — Ban tổ chức | Thiết lập khu vực, hàng/số ghế, giá và một khoảng mở–đóng bán; bảo vệ ghế đã giữ/bán. | P0 | MVP |
| BL-05 | Tìm concert — Người mua | Tìm theo tên/nghệ sĩ; lọc địa điểm, thời gian, khoảng giá; chỉ trả nội dung được công bố. | P1 | MVP |
| BL-06 | Chi tiết và sơ đồ ghế — Người mua | Xem lịch diễn, địa điểm, giá, điều kiện và ghế còn bán/đang giữ/đã bán; kiểm tra lại khả dụng khi đặt. | P0 | MVP |
| BL-07 | Giữ ghế — Người mua | Giữ 1–4 ghế trong 5 phút; toàn bộ hoặc không ghế nào; hiển thị đếm ngược không reset khi reload. | P0 | MVP |
| BL-08 | Đơn hàng và tổng tiền — Người mua | Xem ghế, giá chốt, phí 0 đồng và tổng VND trước thanh toán; gửi lặp cùng yêu cầu không tạo đơn trùng. | P0 | MVP |
| BL-09 | Thanh toán giả lập — Người mua | Có thành công, thất bại, đang xử lý/đang xác minh; không thu trùng và không cấp vé từ kết quả chưa xác minh. | P0 | MVP |
| BL-10 | Hủy/hết hạn — Người mua, hệ thống | Chỉ hủy trước xác nhận; hết hạn tự giải phóng ghế; thao tác cũ không giải phóng ghế người khác đang giữ. | P0 | MVP |
| BL-11 | Hoàn tiền bù trừ — Hệ thống, admin | Không thể cấp vé sau thu tiền thì hoàn toàn bộ; theo dõi pending/thành công/cần hỗ trợ, không hoàn trùng. | P0 | MVP |
| BL-12 | Đơn và vé của tôi — Người mua | Xem danh sách/chi tiết đơn, thanh toán, refund và vé; mở lại sau mất mạng vẫn thấy kết quả thực tế. | P0 | MVP |
| BL-13 | Phát hành vé QR — Người mua | Mỗi ghế đã bán có một vé hợp lệ; xem/tải vé có kiểm soát quyền; lỗi tạo file không làm mất vé. | P0 | MVP |
| BL-14 | Check-in — Nhân viên | Quét, kiểm tra đúng suất diễn và ghi nhận sử dụng; quét lại báo thời gian/cổng trước; vé không hợp lệ bị từ chối. | P0 | MVP |
| BL-15 | Email — Người mua, admin | Gửi xác nhận vé và kết quả hoàn tiền vào sandbox; có lịch sử, retry và yêu cầu gửi lại có kiểm soát. | P1 | MVP |
| BL-16 | Tra cứu/hỗ trợ — Ban tổ chức, admin | Tìm đơn/payment/vé/refund theo mã hoặc người mua trong phạm vi quyền; admin thử lại xử lý hợp lệ và lưu dấu vết. | P1 | MVP |
| BL-17 | Báo cáo cơ bản — Ban tổ chức | Xem vé bán, tiền thu/hoàn/thu ròng, lấp đầy và check-in theo phạm vi quản lý; hiển thị thời điểm dữ liệu. | P1 | MVP |
| BL-18 | Audit nghiệp vụ — Admin | Tra được thao tác quan trọng theo đối tượng, người thực hiện và thời gian theo BR-16. | P1 | MVP |
| BL-19 | Bảo vệ mở bán — Hệ thống | Giờ server, giới hạn tần suất giữ ghế, phản hồi quá tải hữu hạn; không bán vượt ghế dưới tải đồng thời. | P0 | MVP |
| BL-20 | Phục hồi và mô phỏng lỗi — Vận hành | Tái lập lỗi/mất kết nối/khởi động lại; đơn tiếp tục xử lý hoặc hiện cần hỗ trợ; có kết quả đo và đối chiếu giao dịch. | P0 | MVP |
| BL-21 | Phòng chờ và nhiều đợt bán | Xếp lượt mở bán; nhiều đợt với giá/hạn mức riêng. Cần chốt chính sách trước triển khai. | P2 | Mở rộng |
| BL-22 | Hủy/đổi lịch concert | Quy trình thông báo, quyền phê duyệt và chính sách hoàn tiền riêng. | P2 | Mở rộng |
| BL-23 | Báo cáo nâng cao | Funnel, dữ liệu tương tác giả lập và cập nhật streaming sau khi báo cáo cơ bản ổn định. | P2 | Mở rộng |

## 7. Định nghĩa báo cáo

| Chỉ số | Cách hiểu thống nhất |
|---|---|
| Vé bán | Số vé được phát hành hợp lệ cho đơn đã xác nhận; không lấy số thanh toán thành công thay số vé. |
| Tiền thu | Tổng các khoản thanh toán thành công, mỗi giao dịch chỉ tính một lần. |
| Tiền hoàn | Tổng các khoản hoàn tiền thành công; chưa tính yêu cầu còn chờ. |
| Tiền thu ròng | Tiền thu trừ tiền hoàn; không gọi là lợi nhuận. |
| Tỷ lệ lấp đầy | Số ghế đã bán / tổng ghế được cấu hình bán của suất diễn × 100%; mẫu số bằng 0 hiển thị “Chưa áp dụng”. |
| Check-in | Số vé đã check-in thành công; quét lặp không làm tăng số lượng. |

Báo cáo chấp nhận trễ và phải hiển thị dữ liệu cập nhật đến đâu. Tiền thu và tiền hoàn ghi theo ngày giao dịch tương ứng, sử dụng ngày kinh doanh Việt Nam (Asia/Ho_Chi_Minh). Giao dịch đang xác minh/đang hoàn được phân biệt với số đã hoàn tất. Chênh lệch tạm thời giữa tiền thu và vé bán phải giải thích được, ví dụ tiền đã thu nhưng đang chờ hoàn.

## 8. Yêu cầu phi chức năng và nghiệm thu

| Nhóm | Tiêu chí nghiệm thu |
|---|---|
| Tính đúng đắn | Không bán trùng ghế, thu/hoàn tiền trùng hoặc check-in thành công hai lần khi yêu cầu đồng thời, gửi lại hay xử lý lại. |
| Khả năng phục hồi | Mất callback, mất mạng hoặc khởi động lại không làm mất giao dịch đang xử lý; kết quả được xác minh và tiến tới trạng thái rõ ràng. |
| Độc lập chức năng | Lỗi tìm kiếm/email/báo cáo không chặn checkout. Khi tìm kiếm lỗi, vẫn duyệt danh sách concert cơ bản; email lỗi không mất vé, báo cáo lỗi vẫn nêu độ cũ dữ liệu. |
| Bảo mật | Thực thi quyền theo đối tượng; không công khai QR, vé, thông tin người mua hoặc dữ liệu giao dịch. Không lưu dữ liệu thẻ thật. |
| Trải nghiệm | Hiển thị rõ thời hạn giữ ghế, tổng tiền, ghế vừa hết, quá tải, đang xác minh và đang hoàn tiền; không bắt người dùng mua lại vì chưa rõ kết quả. |
| Hiệu năng | Đo riêng xem concert, xem ghế, giữ ghế, thanh toán và check-in; ghi môi trường, dữ liệu, tải đồng thời, độ trễ, tỷ lệ lỗi và kết quả đối chiếu. |
| Khả năng quan sát | Theo dõi giao dịch treo, hoàn tiền chờ, lỗi gửi email, độ trễ dữ liệu và tranh chấp ghế; tra được dấu vết để hỗ trợ. |

**Kịch bản nghiệm thu trọng tâm:** Mua vé và check-in xuyên suốt; nhiều khách tranh cùng ghế; giữ nhiều ghế có một ghế không còn; reload không gia hạn; thanh toán/callback lặp hoặc đến muộn; hủy cạnh tranh thanh toán; mất mạng sau thu tiền; hoàn tiền chậm/lỗi; file vé/email lỗi; hai cổng cùng quét; khởi động lại giữa giao dịch; chạy lại báo cáo không cộng trùng; truy cập chéo ban tổ chức bị từ chối.

Mức tải, thời gian phản hồi, ngưỡng rate limit và độ trễ tối đa của báo cáo **chưa là cam kết định lượng**. Chủ dự án chốt các giá trị sau baseline với cấu hình môi trường và workload cụ thể; đây là bước hiệu chỉnh vận hành, không thay đổi phạm vi nghiệp vụ đã chốt.

## 9. Ràng buộc và quản lý thay đổi

- Giữ 5 microservices: Booking, Payment, Notification, Search, Report. Catalog, Inventory, Ordering và Ticketing nằm trong Booking; không thêm service nghiệp vụ.
- Backend Java + Spring Boot; tuân thủ kiến trúc nội bộ đã thống nhất. Kafka, Temporal, Redis và các công nghệ khác là phương tiện triển khai, không thay thế yêu cầu nghiệp vụ trong BRD.
- Triển khai tăng dần: nền tảng/quyền → quản lý và giữ ghế → thanh toán/hoàn tiền/vé/check-in → thông báo/tìm kiếm/báo cáo → thực nghiệm vận hành. MVP chỉ hoàn tất khi bao phủ toàn bộ backlog MVP, không dừng ở demo mua vé.
- Nguồn: yêu cầu khách hàng và các quyết định được xác nhận ngày 03/11/2026; đối chiếu bộ architecture/planning tổng thể và 5 service, phiên bản ngày 30/10/2026.
- BRD này là căn cứ phạm vi nghiệp vụ mới nhất. Khác biệt cần phản ánh khi triển khai gồm: 4 vé/đơn, VND/phí 0, tài khoản và khôi phục mật khẩu, tra cứu theo người mua, lọc khoảng giá, cổng check-in, audit và báo cáo lấp đầy. Không mặc định các API trong tài liệu kỹ thuật cũ đã bao phủ hết các yêu cầu này.
- Mọi bổ sung ngoài phạm vi phải được chủ dự án xác nhận về quy tắc, ưu tiên và tác động trước khi đưa vào backlog triển khai. Tài liệu architecture/planning nguồn không được sửa trong lần tạo BRD này.
