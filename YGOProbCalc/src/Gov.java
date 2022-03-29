import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Gov {
	static String[] locations=new String[] {"Deck", "Hand"};
	static int hand_size=5;
	static int maximum_depth=Integer.MAX_VALUE;
	static boolean cacheChecked = false;
	static List<Possibility> goal;
	static List<Termination_Possibility> terminations;
	static List<List<Modification>> successes;
	static {
		goal = new ArrayList<Possibility>();
		terminations = new ArrayList<Termination_Possibility>();
		successes = new ArrayList<List<Modification>>();
	}
	public static int num_locations()
	{
		return locations.length;
	}
	public static void locations(String... locs)
	{
		locations = locs;
	}
	public static void poss(Condition... conditions) //if something like allure, check for a dark to discard first 
	{
		goal.add(new Possibility(conditions));
	}
	public static void terminate(Action[] are_off, Condition... tconds)
	{
		terminations.add(new Termination_Possibility(are_off, tconds));
	}
	public static void terminate(Action are_off, Condition... tconds)
	{
		terminations.add(new Termination_Possibility(new Action[] {are_off}, tconds));
	}
	public static void terminate(Condition... tconds)
	{
		terminations.add(new Termination_Possibility(new Action[] {}, tconds));
	}
	public static boolean satisfies_possibilities(Gamestate g, int depth)//TODO: breadth-first
	{
		if(depth > maximum_depth)
		{
			return false;
		}
		if(g.triggers.size()>0)
		{
			Action trigger = g.triggers.remove(0).trigger;
			List<Integer> exec = g.executable(trigger);
			if(exec.size()==0)
				return satisfies_possibilities(g, depth);
			
			for(Integer i : exec)
			{
				List<Modification> modifications = g.modifications(trigger, trigger.possibilities.get(i));
				if(modifications.isEmpty())
					continue; //Only loop if move conditions can't be executed despite the naive check
				for(Modification mod : modifications)
				{
					g.modify(mod);
					if(satisfies_possibilities(g, depth+1))
						return true;
					g.unmodify(mod);
				}
				return false; 
			}
		}
		if(g.locations.satisfies(goal))
			return true;
		if(g.terminate(terminations))
			return false;
		
		if(!cacheChecked)
		{
			for(List<Modification> run : CachedRun.successes)
			{
				for(Modification mod : run)
				{
					g.modify(mod);
					if(g.locations.satisfies(goal))
						return true;
					if(g.terminate(terminations))
						return false;
					g.unmodify(mod);
				}
			}
			cacheChecked = true;
		}
		
		for(Action action : Action.open_actions)
		{
			for(int i : g.executable(action))
			{
				List<Modification> modifications = g.modifications(action, action.possibilities.get(i));
				for(Modification mod : modifications)
				{
					g.modify(mod);
					if(satisfies_possibilities(g, depth+1))
						return true;
					g.unmodify(mod);
				}
			}
		}

		return false;
	}
	public static double probability(FileWriter fw, int num_trials) throws IOException
	{
		int counter = 0;
		for(int i=0; i<num_trials; i++)
		{
			System.out.println("Trial number "+ (i+1) + "; Found so far: "+counter);
			Gamestate gamestate = new Gamestate();
			String hand = gamestate.locations.hand_string();
			String preloads = gamestate.preloads.toString();
			System.out.print(hand);
			System.out.print(preloads);
			if(satisfies_possibilities(gamestate,0))
			{
				fw.write("This Worked:\n");
				fw.write(hand+"\n");
				successes.add(gamestate.log);
				for(Modification mod : gamestate.log)
				{
					fw.write(mod.toString());
				}
				counter+=1;
				System.out.println("Found!\n");
			}
			else
			{
				fw.write("This failed: \n");
				fw.write(hand);
				fw.write(preloads);
				System.out.println("Not found!\n");
			}
			fw.write("\n\n");
		}
		double probability = ((double) counter)/ num_trials;
		fw.write(Double.toString(probability));
		return probability;
	}
	
	public static DeckEdit[] ratios(String filename, int num_trials, DeckEdit[] changes) throws IOException
	{
		File excel = new File(filename+".csv");
		FileWriter exc = new FileWriter(excel);
		for(int i = 0; i < changes.length; i++)
		{
			DeckEdit change = changes[i];
			System.out.println("Testing " + change.edits);
			
//			Set each card's quantity
			for(int j = 0; j < change.cards.length; j++)
			{
				Card.set_count(change.cards[j], change.count[j]);
			}
			
//			Basically running probability
			File f = new File(filename+ i +".txt");
			FileWriter fw = new FileWriter(f);
			int counter = 0;
			for(int k=0; k<num_trials; k++)
			{
				System.out.println("Trial number "+ (k+1) + "; Found so far: "+counter);
				Gamestate gamestate = new Gamestate();
				String hand = gamestate.locations.hand_string();
				String preloads = gamestate.preloads.toString();
				System.out.print(hand);
				System.out.print(preloads);
				if(satisfies_possibilities(gamestate,0))
				{
					fw.write("This Worked:\n");
					fw.write(hand+"\n");
					successes.add(gamestate.log);
					for(Modification mod : gamestate.log)
					{
						fw.write(mod.toString());
					}
					counter+=1;
					System.out.println("Found!\n");
				}
				else
				{
					fw.write("This failed: \n");
					fw.write(hand);
					fw.write(preloads);
					System.out.println("Not found!\n");
				}
				fw.write("\n\n");
			}
			fw.close();
			double probability = ((double) counter)/ num_trials;
			
//			Sets the result of this particular change and writes it to the file
			change.set_result(probability);
			exc.write(change.edits + "\n");
			System.out.println(probability);
			successes = new ArrayList<List<Modification>>();;
		}
		exc.close();
		return changes;
	}

}
