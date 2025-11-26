package kidd.house.zerde.controller;

import kidd.house.zerde.dto.payments.PurchaseSubscriptionDto;
import kidd.house.zerde.dto.user.KaspiCallbackDto;
import kidd.house.zerde.dto.user.KaspiPaymentResponseDto;
import kidd.house.zerde.service.KaspiService;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserPanelController {
    private final UserService userService;
    private final KaspiService kaspiService;
//    @GetMapping("/profile")
//    public ResponseEntity<UserProfileDto> getUserProfiles(){
//          = userService.getUserProfiles();
//        return ResponseEntity.ok();
//    }
//    @PutMapping("/edit_user/{teacher_id}")
//    public ResponseEntity<String> editTeacher(@PathVariable int teacher_id, @RequestBody EditTeacherDto editTeacherDto){
//        userService.editTeacher(teacher_id,editTeacherDto);
//        return new ResponseEntity<>("Teacher edited", HttpStatus.OK);
//    }
     @PostMapping("/buy-subscription")
     public ResponseEntity<KaspiPaymentResponseDto> buySubscription(@RequestBody PurchaseSubscriptionDto dto) {
          KaspiPaymentResponseDto resp = userService.purchase(dto);
          return ResponseEntity.ok(resp);
     }
     // Endpoint для callback от Kaspi
     @PostMapping("/callback")
     public ResponseEntity<String> callback(
             @RequestBody KaspiCallbackDto dto,
             @RequestHeader(value = "X-Kaspi-Signature",
                     required = false)
             String signature) {

          // Для проверки подписи мы можем использовать raw body. Здесь предполагается, что
          // framework уже десериализовал в dto — в реальном коде лучше принимать raw String и передавать в verify
          String payloadString = dto.toString(); // замените на реальное тело (raw), если нужен точный расчет подписи

          if (signature != null && !kaspiService.verifyCallbackSignature(payloadString, signature)) {
               return ResponseEntity.status(400).body("Invalid signature");
          }

          // обновляем Payment и, если SUCCESS, создаём подписку
          userService.finalizePaymentAndCreateSubscription(dto.paymentId(), dto.status());

          return ResponseEntity.ok("OK");
     }
}
