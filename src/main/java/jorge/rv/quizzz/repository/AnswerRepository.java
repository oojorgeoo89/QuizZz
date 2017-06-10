package jorge.rv.quizzz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jorge.rv.quizzz.model.Answer;

@Repository("AnswerRepository")
public interface AnswerRepository  extends JpaRepository<Answer, Long>{
	
}
