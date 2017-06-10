package jorge.rv.quizzz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jorge.rv.quizzz.model.UserInfo;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<UserInfo, Long> {
	UserInfo findByEmail(String email);
}
