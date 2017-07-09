package jorge.rv.quizzz.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "answer")
public class Answer extends BaseModel implements UserOwned {
	
	@Size(min=1, max=50, message = "The answer should be less than 50 characters")
	@NotNull(message = "No answer text provided.")
	private String text;
	
	@ManyToOne
	@JsonIgnore
	private Question question;

	@JsonIgnore
	@NotNull
	private Boolean iscorrect = false;
	
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable=false, updatable=false)
	private Calendar createdDate;

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Boolean getIscorrect() {
		return iscorrect;
	}

	public void setIscorrect(Boolean isCorrect) {
		this.iscorrect = isCorrect;
	}

	@Override
	@JsonIgnore
	public User getUser() {
		return question.getUser();
	}
	
}
