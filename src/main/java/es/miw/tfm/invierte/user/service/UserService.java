package es.miw.tfm.invierte.user.service;

import java.util.List;

import es.miw.tfm.invierte.user.data.dao.UserRepository;
import es.miw.tfm.invierte.user.data.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public Flux<User> readAll() {
    List<User> users = this.userRepository.findAll();
    return Flux.fromIterable(users);
  }

}
