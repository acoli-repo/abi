package utility;

/**
 * Token information class for storing sentence number, start and end span of a token in a sentence.
 * 
 * @author Sergej Jaschonkow
 * 
 */
public class TokenSpan {

	private final int sentenceNumber;
	private final int startSpan;
	private final int endSpan;

	/**
	 * Constructor
	 * 
	 * @param sentenceNumber
	 *            Number of the sentence of the token.
	 * @param startSpan
	 *            Token start span index in sentence.
	 * @param endSpan
	 *            Token end span index in sentence.
	 */
	public TokenSpan(int sentenceNumber, int startSpan, int endSpan) {
		this.sentenceNumber = sentenceNumber;
		this.startSpan = startSpan;
		this.endSpan = endSpan;
	}

	/**
	 * Get the sentence number of the token
	 * 
	 * @return Sentence number of the token.
	 */
	public int getSentenceNumber() {
		return sentenceNumber;
	}

	/**
	 * Get the start span of the token in the sentence.
	 * 
	 * @return Start span of the token in the sentence.
	 */
	public int getStartSpan() {
		return startSpan;
	}

	/**
	 * Get the end span of the token in the sentence.
	 * 
	 * @return End span of the token in the sentence.
	 */
	public int getEndSpan() {
		return endSpan;
	}

	/**
	 * Get hash code of instance.
	 * 
	 * @return Hash code of the instance.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endSpan;
		result = prime * result + sentenceNumber;
		result = prime * result + startSpan;
		return result;
	}

	/**
	 * Compare TokenSpans instances.
	 * 
	 * @param TokenSpan
	 *            Instance that should be compared
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TokenSpan other = (TokenSpan) obj;
		if (endSpan != other.endSpan)
			return false;
		if (sentenceNumber != other.sentenceNumber)
			return false;
		if (startSpan != other.startSpan)
			return false;
		return true;
	}
}