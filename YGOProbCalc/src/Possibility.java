import java.util.List;
import java.util.ArrayList;
public class Possibility {
	Condition[] conditions;
	int sum;
	List<Trigger> trigger = new ArrayList<Trigger>(); //for now just add one trigger at most; can have an intermediate if necessary
	public Possibility(Condition... conditions)
	{
		this.sum = -1;
		this.conditions=conditions;
	}
	public Possibility(Action action, Condition... conditions)
	{
		this.sum = -1;
		this.conditions=conditions;
		trigger.add( new Trigger(action));
	}
	public Possibility(int sum, Condition... conditions)
	{
		this.sum = sum;
		this.conditions=conditions;
	}
}
