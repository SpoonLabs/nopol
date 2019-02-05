package infinitel_examples.infinitel_example_5;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InfinitelExampleTest {

	@Test
	public void consumedEverything() {
		String sentence = "This is a sentence.";
		InfinitelExample example = new InfinitelExample(sentence.length() - 1);
		assertTrue(example.consume(sentence));
	}
	
	@Test
	public void consumePartially() {
		String sentence = "www.anywebsite.com";
		InfinitelExample example = new InfinitelExample(3);
		assertTrue(example.consume(sentence));
	}
	
	@Test
	public void infiniteLoop() {
		String sentence = "There is no dot, it will loop forever";
		InfinitelExample example = new InfinitelExample(sentence.length());
		assertTrue(example.consume(sentence));
	}
}
