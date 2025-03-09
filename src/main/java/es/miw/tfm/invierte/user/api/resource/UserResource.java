package es.miw.tfm.invierte.user.api.resource;

import es.miw.tfm.invierte.user.api.dto.UserDto;
import es.miw.tfm.invierte.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Log4j2
@RestController
@RequestMapping(UserResource.USERS)
@RequiredArgsConstructor
public class UserResource {

  public static final String USERS = "/users";

  private final UserService userService;

  @GetMapping
  public Flux<UserDto> readAll() {
    return this.userService.readAll()
        .map(UserDto::ofBasicInfo);
  }

}
