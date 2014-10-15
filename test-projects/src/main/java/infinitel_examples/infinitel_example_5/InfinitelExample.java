package infinitel_examples.infinitel_example_5;

public class InfinitelExample {

	public InfinitelExample(int size) {
		consumer = new Consumer(size);
	}
	
	public boolean consume(String word) {
		int index = 0;
		while (canKeepConsuming(index, word)) {
			index++;
			consumer().consume();
		}
		return consumer().getConsumed() == consumer().getSize();
	}
	
	public boolean canKeepConsuming(int index, String word) {
		return index >= word.length() || word.charAt(index) != '.';
	}
	
	private Consumer consumer() {
		return consumer;
	}
	
	private Consumer consumer;
}

class Consumer {
	
	public Consumer(int size) {
		this.size = size;
		this.consumed = 0;
	}
	
	public void consume() {
		consumed++;
	}
	
	protected int getConsumed() {
		return consumed;
	}
	
	protected int getSize() {
		return size;
	}
	
	@SuppressWarnings("unused")
	private int getId() {
		return id;
	}
	
	private int size;
	private int consumed;
	private static int id = 18213131;
}