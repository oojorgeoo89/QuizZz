package jorge.rv.quizzz.model.support;

public class Result {
	private int totalQuestions = 0;
	private int correctQuestions = 0;

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(int totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public int getCorrectQuestions() {
		return correctQuestions;
	}

	public void setCorrectQuestions(int correctQuestions) {
		this.correctQuestions = correctQuestions;
	}

	public void addAnswer(Boolean isCorrect) {
		totalQuestions++;
		if (isCorrect) {
			correctQuestions++;
		}
	}
}
