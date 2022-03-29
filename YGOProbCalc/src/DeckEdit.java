public class DeckEdit {
		
	Card[] cards;
	int[] count;
	String edits;
	double result;
	
	public DeckEdit(Card[] cards, int[] count) {
		this.cards = cards;
		this.count = count;
		edits = "";
		for(int i = 0; i < cards.length; i++)
		{
			edits += cards[i].name + "-";
			edits += count[i] + "_";
		}
	}
	
	public void set_result(double result)
	{
		this.result = result;
		edits += "," + result;
	}
}
