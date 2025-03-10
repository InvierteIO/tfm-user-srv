package es.miw.tfm.invierte.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import es.miw.tfm.invierte.user.data.dao.UserRepository;
import es.miw.tfm.invierte.user.data.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Test
  void testReadAll() {
    final var users = List.of(buildMockUser());

    when(userRepository.findAll()).thenReturn(users);

    Flux<User> actualResult = userService.readAll();

    assertEquals(1, actualResult.count().block());
    User actualUser = actualResult.blockFirst();
    User expectedUser = users.getFirst();
    assertEquals(expectedUser, actualUser);
  }

  private static User buildMockUser(){
    return User.builder()
        .id(1)
        .email("user@email.com")
        .firstName("FirstName")
        .familyName("FamilyName")
        .active(false)
        .registrationDate(LocalDateTime.now())
        .build();
  }

}
