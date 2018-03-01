package com.mfg.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.mfg.Entity.User;

/**
 * 
 * @author I337864
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	String UPDATE_USER_PASSWORD = "update User u set u.password = :pwd where u.id = :id";

	User findByUserNameAndPassword(String userName, String password);
	
	User findById(Long id);

	User findBySapId(String sapId);

	@Modifying(clearAutomatically = true)
	@Query(UPDATE_USER_PASSWORD)
	@Transactional
	boolean updateUserAgeById(@Param("pwd") String pwd,@Param("id") Long id);

	Long deleteById(Long userId);
}
	