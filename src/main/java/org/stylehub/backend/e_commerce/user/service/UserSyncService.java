package org.stylehub.backend.e_commerce.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;

    @Transactional
    public void upsert(String externalUserId, String email) {
          this.userRepository.findByExternalUserId(externalUserId)
                  .ifPresentOrElse(
                          user -> {
                              if(email!=null&&!email.equals(user.getEmail())){
                                  user.setEmail(email);
                              }
                              },()->{
                                    User newUser = new User();
                                    newUser.setExternalUserId(externalUserId);
                                    newUser.setEmail(email);
                                    newUser.setIsProfileCompleted(true);
                                    this.userRepository.save(newUser);

                                }
                  );
    }

    public boolean checkIsCompleted(String email) {
        return userRepository.findByEmail(email)
                .map(User::getIsProfileCompleted)
                .orElse(false);
    }
}
