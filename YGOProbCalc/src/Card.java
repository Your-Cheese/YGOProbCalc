import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Card {
	String name;
	int num;
	int level;
	String[] categories;
	static List<Card> num_to_card; 
	static int num_created;
	static HashMap<String, List<Integer>> category_hash;
	static List<Integer> deck;
	static int deck_size;
	static {
	num_created=0;
	num_to_card = new ArrayList<Card>();
	category_hash =  new HashMap<String, List<Integer>>();
	category_hash.put("card", new ArrayList<Integer>());
	deck = new ArrayList<Integer>();
	deck_size = 0;
	}
	public Card(String name, int quantity)
	{
		this.name = name;
		this.num = num_created;
		num_to_card.add(this);
		category_hash.get("card").add(this.num);
		deck.add(quantity);
		deck_size+= quantity;
		num_created++;
		categories = new String[0];
	}
	public Card(String name, int quantity, int level)
	{
		this.name = name;
		this.num = num_created;
		this.level = level;
		num_to_card.add(this);
		category_hash.get("card").add(this.num);
		deck.add(quantity);
		deck_size+= quantity;
		num_created++;
		categories = new String[0];
	}
	public Card(String name, int quantity, String... cats)
	{
		this.name = name;
		this.num = num_created;
		num_to_card.add(this);
		category_hash.get("card").add(this.num);
		deck.add(quantity);
		deck_size+=quantity;
		num_created++;
		this.categories = cats;
		for(String cat : categories)
		{
			if (!category_hash.containsKey(cat))
			{
				List<Integer> cards = new ArrayList<Integer>();
				cards.add(this.num);
				category_hash.put(cat, cards);
			}
			else
			{
				category_hash.get(cat).add(this.num);
			}
		}
		
	}
	
	public Card(String name, int quantity, int level, String... cats)
	{
		this.name = name;
		this.num = num_created;
		this.level = level;
		num_to_card.add(this);
		category_hash.get("card").add(this.num);
		deck.add(quantity);
		deck_size+=quantity;
		num_created++;
		this.categories = cats;
		for(String cat : categories)
		{
			if (!category_hash.containsKey(cat))
			{
				List<Integer> cards = new ArrayList<Integer>();
				cards.add(this.num);
				category_hash.put(cat, cards);
			}
			else
			{
				category_hash.get(cat).add(this.num);
			}
			
			if(cat.equalsIgnoreCase("xyz"))
			{
				this.level = -level;
			}
		}
		
	}
	public static List<Integer> get_cards(String category)
	{
		if(category_hash.containsKey(category))
			return category_hash.get(category);
		else
		{
			System.out.println("Check category spelling: "+category);
			System.exit(0);
			return null;
		}
	}
	
	public static void set_count(Card c, int count)
	{
		int card_num = c.num;
		int deck_count = deck.get(card_num);
		int diff = deck_count-count;
		if(diff > 0)
		{
			for(int i = 0; i < diff; i++)
			{
				deck.set(card_num, deck.get(card_num)-1);
				deck_size--;
			}
		}
		else if(diff < 0)
		{
			for(int i = 0; i > diff; i--)
			{
				deck.set(card_num, deck.get(card_num)+1);
				deck_size++;
			}
		}
	}
	

}
